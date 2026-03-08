package com.erns.es.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.erns.es.search.domain.EsHitDTO;
import com.erns.es.search.domain.EsResultDTO;
import com.erns.es.search.domain.EsSearchDTO;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EsSearchV2Service {

	private final ElasticsearchClient client;

	public static final int MAX_HITS_COUNT = 600; // 최대 Hit_Count

	

	@Autowired
	public EsSearchV2Service(ElasticsearchClient client) {
		this.client = client;
	}

	public EsResultDTO searchByField(EsSearchDTO param) throws IOException {
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(q -> q
						.match(m -> m
								.field(param.getField())
								.query(param.getValue()))));

		SearchResponse<Object> response = client.search(searchRequest, Object.class);

		HitsMetadata<Object> hits = response.hits();
		List<Object> itemList = hits.hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		EsResultDTO result = EsResultDTO.builder()
				.list(itemList)
				.maxScore(hits.maxScore())
				.total(hits.total().value())
				.build();

		return result;
	}

	//상품 검색
	public static final List<String> PROD_SEARCH_INFO_FIELDS = Arrays.asList(
		 "product.name"
		, "product.name.keyword^1.5"
		, "product.name.static^1.4"
		, "product.name.ko^1.3"
		, "product.name.en^1.3"

		, "product.brand.name^1.5"
		, "product.brand.name.keyword^2.5"
		, "product.brand.name.static^1.4"
		, "product.brand.name.ko^1.3"
		, "product.brand.name.en^1.3"

		, "product.brand.full_name^1.5"
		, "product.brand.full_name.keyword^2.5"
		, "product.brand.full_name.static^1.4"
		, "product.brand.full_name.ko^1.3"
		, "product.brand.full_name.en^1.3"

	);

	//상품 성분 검색
	public static final List<String> PROD_SEARCH_ELEM_INFO_FIELDS = Arrays.asList(
		"ingredients.korean^1.5"
			, "ingredients.english^1.5"
	);

	/**
	 * 화해상품정보 텍스트 검색
	 * 
	 * @param pText
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchHwProduct(EsSearchDTO param) throws IOException {
		// Elasticsearch 특수 문자 이스케이프 처리 (+, - 제외)
		//String text = escapeElasticsearchSpecialChars(param.getText()).toLowerCase();
		//String text = replaceElasticsearchSpecialChars(param.getText()).toLowerCase();
		String text = getSearchText(param.getText(), false);
		String reSearchText = getSearchText(param.getReSearchText(), false);
	  log.debug("orgText:[{}], text:[{}]", param.getText(), text);
		log.debug("orgReText:[{}], text:[{}]", param.getReSearchText(), reSearchText);

		//검색어 목록
		List<String> keywords = new ArrayList<>();
		if (StringUtils.hasText(text)) {
			String[] keywordsArr = text.split(",");
			// ✅ 키워드 개수가 5개를 초과하면 5개까지만 유지
			keywords = Arrays.stream(keywordsArr)
			        .map(String::trim)
							.filter(s -> !s.isEmpty())
							.limit(5) // 최대 5개까지만 허용
							.collect(Collectors.toList());

			log.debug("Limited keywords: {}", Arrays.toString(keywords.toArray()));
		}

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

		if(!keywords.isEmpty()) {
			// product 레벨에 대한 처리리
			// 각각의 multi_match 쿼리 구성			
			List<Query> rawsShouldQueries = keywords.stream()
				.map(term -> Query.of(q -> q
					.multiMatch(m -> m
							.query(term)
							.fields(PROD_SEARCH_INFO_FIELDS)
					)
			))
			.collect(Collectors.toList());

			// rawElemInfo 레벨에 nested multi_match 쿼리
			// 각각의 multi_match 쿼리 구성
			List<Query> rawElemInfoShouldQueries = keywords.stream()
					.map(term -> Query.of(q -> q
						.nested(n -> n
							.path("ingredients")
							.query(nq -> nq
                .bool(b -> b
                    .should(sq -> sq
                        .multiMatch(m -> m
                            .query(term)
                            .fields(PROD_SEARCH_ELEM_INFO_FIELDS)
                        )
                    )
                )
            	)
						)					
			))
			.collect(Collectors.toList());

			// 기본 검색 쿼리 추가
			boolQueryBuilder.should(rawsShouldQueries); // RAW_SEARCH_INFO_FIELDS
			boolQueryBuilder.should(rawElemInfoShouldQueries); // RAW_SEARCH_ELEM_INFO_FIELDS
			boolQueryBuilder.minimumShouldMatch("25%"); // 또는 적절히 조정
		}

		// 결과 내 재검색 조건 추가
		if (StringUtils.hasText(reSearchText)) {
			List<String> reKeywords = Arrays.stream(reSearchText.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.limit(5)
					.collect(Collectors.toList());

			// rawName 쿼리 (multi_match)
			List<Query> reRawShouldQueries = reKeywords.stream()
					.map(term -> Query.of(q -> q
						.multiMatch(m -> m
							.query(term)
							.fields(PROD_SEARCH_INFO_FIELDS)
						)
					))
					.collect(Collectors.toList());

			// rawElemInfo 쿼리 (nested + multi_match)
			List<Query> reRawElemInfoShouldQueries = reKeywords.stream()
					.map(term -> Query.of(q -> q
						.nested(n -> n
							.path("ingredients")
							.query(nq -> nq
								.bool(b -> b
									.should(sq -> sq
										.multiMatch(m -> m
														.query(term)
														.fields(PROD_SEARCH_ELEM_INFO_FIELDS)
										)
									)
								)
							)
						)
					))
					.collect(Collectors.toList());

			// 쿼리에 추가
			boolQueryBuilder.should(reRawShouldQueries);
			boolQueryBuilder.should(reRawElemInfoShouldQueries);

			// reSearch 조건도 있어야 하므로 minimumShouldMatch 강화 가능
			boolQueryBuilder.minimumShouldMatch("50%"); // 또는 적절히 조정
		}

		// categories 필터링 조건 추가
		List<String> categories = param.getCategories();
		if (categories != null && !categories.isEmpty()) {
			BoolQuery.Builder categoryBoolQuery = new BoolQuery.Builder();

			for (String category : categories) {
				categoryBoolQuery.must(m -> m
						.nested(n -> n // 개별적으로 `nested` 조건 적용
								.path("product.categories")
								.query(q -> q.term(t -> t
										.field("product.categories.code")
										.value(category) // 개별적으로 검색
								))
						)
				);
			}

			boolQueryBuilder.filter(f -> f.bool(categoryBoolQuery.build())); // 모든 필터 적용
		}

		// 하이라이트 설정
		Highlight.Builder highlightBuilder = new Highlight.Builder();		
		{
			//highlightBuilder.fragmentSize(SNIPPET_LENGTH); // 하이라이트 조각 크기
			highlightBuilder.preTags("<em>");
			highlightBuilder.postTags("</em>");

			// 루트 필드 하이라이트 적용
			PROD_SEARCH_INFO_FIELDS.stream()
				.map(f -> f.replaceAll("\\^.*", "")) // ^1.6 같은 가중치 제거
				.distinct()
				.forEach(field -> highlightBuilder.fields(field, h -> h));

			// nested 필드 하이라이트 적용
			PROD_SEARCH_ELEM_INFO_FIELDS.stream()
				.map(f -> f.replaceAll("\\^.*", "")) // ^1.6 같은 가중치 제거
				.distinct()
				.forEach(field -> highlightBuilder.fields(field, h -> h));
		}
		Highlight highlight = highlightBuilder.build();

		// 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> {
			s.index(param.getIndex())
				.query(q -> q.bool(boolQueryBuilder.build()))
				.highlight(highlight)
				.from(param.getOffset()) // 시작 위치

				// 페이지당 문서 수, 최대 hitCount로 제한
				.size(Math.min(param.getRowSize(), Math.max(0, MAX_HITS_COUNT - param.getOffset())));

			// 동적 정렬 추가
			if (param.getSortField() != null && !param.getSortField().isEmpty()) {
				s.sort(so -> so.field(f -> f
						.field(param.getSortField()) // 정렬 필드 설정
						.order("desc".equalsIgnoreCase(param.getSortOrder()) ? SortOrder.Desc : SortOrder.Asc) // 정렬 순서 설정
				));
			}

			return s;
		});

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
		SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		// 검색 결과 처리
		EsResultDTO result = esResponseToEsResultDTO(response);

		if (MAX_HITS_COUNT < result.getTotal()) {
			result.setTotal(MAX_HITS_COUNT);
		}

		return result;
	}

	/**
	 * 화해제품ID로 상세 정보 리턴
	 * 
	 * @param productId
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchProductById(EsSearchDTO param) throws IOException {

		// Build the  query
		Query query = Query.of(q -> q
				.match(m -> m
					.field("product.id")
					.query(param.getId())
				)
		);
		
		// Create the search request
		SearchRequest searchRequest = SearchRequest.of(s -> s
			.index(param.getIndex())
			.query(query)
			.from(0) // 시작 위치
			.size(1) // 페이지당 문서 수
		);

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
		SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		EsResultDTO result = esResponseToEsResultDTO(response);

		return result;
	}

	//원료 성분 검색
	public static final List<String> INGREDIENT_SEARCH_RESULT_FIELDS = Arrays.asList(
		"id", "rep_name", "rep_name_en",
		"kcai_id", "ewg_id", "hw_ingd_id",
		"inci", "names_ko", "names_en", "nick_name",
		"purposes", "cas_no", "ec_no",
		"formula", "exp_url_1", "exp_url_2",
		"ewg_score_min", "ewg_score", "ewg_data_lebel",
		"ewg_rawcno_id", "ewg_url"
	);

	//성분 keyword
	public static final List<String> INGREDIENT_SEARCH_KEYWORD_FIELDS = Arrays.asList(
			  "rep_name.keyword"
			, "rep_name_en.keyword"
			, "names_ko.keyword"
			, "names_en.keyword"
			, "cas_no.keyword"
			, "ec_no.keyword"
	);

	//성분 검색어
	public static final List<String> INGREDIENT_SEARCH_WORD_FIELDS = Arrays.asList(			
			"names_ko"
			, "names_en"			
			, "cas_no"
			, "ec_no"
	);

		//성분 keyword
		public static final List<String> INGREDIENT_SEARCH_CASNO_FIELDS = Arrays.asList(			
			"cas_no"
			, "ec_no"
	);


	/**
	 * 코칭 성분검색, 검색어 제안용
	 * 
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchCochingIngredientForSuggest(EsSearchDTO param) throws IOException {
	    // 검색어 가져오기 및 Elasticsearch 특수 문자 이스케이프 처리
	    String text = getSearchText(param.getText(), false);
			String ecText = getSearchText(param.getText(), true); //query_string 용
	    log.debug("orgText:[{}], text:[{}]", param.getText(), text);
			log.debug("orgText:[{}], ecText:[{}]", param.getText(), ecText);
	
	    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
	
	    if (StringUtils.hasText(text)) {
				String[] keywords = text.split(",");
				// ✅ 키워드 개수가 5개를 초과하면 5개까지만 유지
				keywords = Arrays.stream(keywords)
								.limit(5) // 최대 5개까지만 허용
								.toArray(String[]::new);
				log.debug("Limited keywords: {}", Arrays.toString(keywords));

				// ✅ 정확한 keyword 검색 (완전 일치 검색)
				BoolQuery.Builder keywordExactQueryBuilder = new BoolQuery.Builder();
				for (String keyword : keywords) {
						if (!keyword.isEmpty()) {
								for (String field : INGREDIENT_SEARCH_KEYWORD_FIELDS) {
										log.debug("field:[{}]", field);								
										keywordExactQueryBuilder.should(m -> m.term(t -> t
												.field(field) // keyword 필드에 대한 term 검색
												.value(keyword.trim())
												.boost(5.0f) // ✅ `boost`를 추가하여 `_score` 영향 유지
										));
								}
						}
				}
				boolQueryBuilder.should(q -> q.bool(keywordExactQueryBuilder.build()));

				// 부분매칭			
				BoolQuery.Builder keywordMultiQueryBuilder = new BoolQuery.Builder();
				for (String keyword : keywords) {
					if (!keyword.isEmpty()) {
						keywordMultiQueryBuilder.should(q -> q.multiMatch(m -> m
							.query(keyword.trim())
							.fields(INGREDIENT_SEARCH_WORD_FIELDS)
							.type(TextQueryType.MostFields) // 또는 MOST_FIELDS
							.operator(Operator.Or) // OR: 많이 매칭될수록 높은 점수							
							.boost(1.0f)
						));
					}
				}
				boolQueryBuilder.should(q -> q.bool(keywordMultiQueryBuilder.build()));

//			boolQueryBuilder.minimumShouldMatch("1");
	    }

		// 하이라이트 설정
		Highlight.Builder highlightBuilder = new Highlight.Builder()
		//.fragmentSize(SNIPPET_LENGTH) // 하이라이트 조각 크기
		.preTags("<em>")
		.postTags("</em>");

		// 검색 필드 리스트를 이용해 하이라이트 필드 추가
		INGREDIENT_SEARCH_KEYWORD_FIELDS.forEach(field -> 
				highlightBuilder.fields(field, new HighlightField.Builder().build())
		);
		INGREDIENT_SEARCH_WORD_FIELDS.forEach(field -> 
				highlightBuilder.fields(field, new HighlightField.Builder().build())
		);
		// INGREDIENT_SEARCH_CASNO_FIELDS.forEach(field -> 
		// 		highlightBuilder.fields(field, new HighlightField.Builder().build())
		// );		
		Highlight highlight = highlightBuilder.build();

		// Elasticsearch 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(q -> q.bool(boolQueryBuilder.build()))
				.highlight(highlight)
				.source(src -> src
						.filter(f -> f
								.includes(INGREDIENT_SEARCH_RESULT_FIELDS))) // 반환할 필드 지정				
				.from(param.getOffset()) // 시작 위치
				.size(Math.min(param.getRowSize(), Math.max(0, MAX_HITS_COUNT - param.getOffset()))) // 페이지당 문서 수
		);

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
		SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		// 검색 결과 처리
		EsResultDTO result = esResponseToEsResultDTO(response);

		if (MAX_HITS_COUNT < result.getTotal()) {
			result.setTotal(MAX_HITS_COUNT);
		}

		return result;
	}

	/**
	 * 코칭 성분 로드, ID로 
	 * 
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO loadCochingIngredient(EsSearchDTO param) throws IOException {
		
		// Build the Match query
		final String fieldName;
		switch (param.getIdType()) {
				case EsSearchDTO.ID_TYPE_KCAI:
						fieldName = "kcai_id";
						break;

				case EsSearchDTO.ID_TYPE_EWG:
						fieldName = "ewg_id";
						break;

				case EsSearchDTO.ID_TYPE_HW:
						fieldName = "hw_ingd_id";
						break;

				case EsSearchDTO.ID_TYPE_RAW:
				default:
						fieldName = "id";
						break;
		}

		Query query = Query.of(q -> q
          .match(m -> m
        	.query(param.getId())
        	.field(fieldName)
          )
		);
		
		// Create the search request
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(query)
				.source(src -> src
						.filter(f -> f
								.includes( // 반환할 필드 지정
									INGREDIENT_SEARCH_RESULT_FIELDS)))
				.from(0) // 시작 위치
				.size(1) // 페이지당 문서 수
		);

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
	  SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		EsResultDTO result = esResponseToEsResultDTO(response);

		return result;
	}

	//원료 검색
	public static final List<String> RAW_SEARCH_INFO_FIELDS = Arrays.asList(
		"rawName^1.3"
		, "rawName.keyword^2.5"
		, "rawName.static^1.4"
		, "rawName.ko^1.3"
		, "rawName.en^1.3"

		, "prodCompany^1.5"
		, "prodCompany.keyword^1.5"
		, "prodCompany.static^1.4"
		// , "prodCompany.ko^1.3"
		// , "prodCompany.en^1.3"
		

		, "prodCountryName"
		, "prodCountryName.keyword^1.5"
		, "prodCountryName.static^1.4"
		, "prodCountryName.ko^1.3"
		, "prodCountryName.en^1.3"

		, "ptnName^2.5"
		, "ptnName.keyword^2.5"
		, "ptnName.static^1.4"
		// , "ptnName.ko^1.3"
		// , "ptnName.en^1.3"
	);

	//원료 성분 검색
	public static final List<String> RAW_SEARCH_ELEM_INFO_FIELDS = Arrays.asList(
		"rawElemInfo.repNameKo^1.4"	
			, "rawElemInfo.repNameEn^1.4"
			, "rawElemInfo.casNo^1.4"
			, "rawElemInfo.ecNo^1.4"
			, "rawElemInfo.namesKo^1.3"
			, "rawElemInfo.namesEn^1.3"
	);

	/**
	 * 코칭 원료 화해상품정보 텍스트 검색
	 * 
	 * @param pText
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchCochingRaw(EsSearchDTO param) throws IOException {
		// Elasticsearch 특수 문자 이스케이프 처리 (+, - 제외)
		//String text = escapeElasticsearchSpecialChars(param.getText()).toLowerCase();
		//String text = replaceElasticsearchSpecialChars(param.getText()).toLowerCase();
		String text = getSearchText(param.getText(), false);
		String reSearchText = getSearchText(param.getReSearchText(), false);
	  log.debug("orgText:[{}], text:[{}]", param.getText(), text);
		log.debug("orgReText:[{}], text:[{}]", param.getReSearchText(), reSearchText);

		int minimumShouldMatchVal = 0;

		//검색어 목록
		List<String> keywords = new ArrayList<>();
		if (StringUtils.hasText(text)) {
			String[] keywordsArr = text.split(",");
			// ✅ 키워드 개수가 5개를 초과하면 5개까지만 유지
			keywords = Arrays.stream(keywordsArr)
			        .map(String::trim)
							.filter(s -> !s.isEmpty())
							.limit(5) // 최대 5개까지만 허용
							.collect(Collectors.toList());

			log.debug("Limited keywords: {}", Arrays.toString(keywords.toArray()));
		}

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

		if(!keywords.isEmpty()) {
			// product 레벨에 처리
			// 각각의 multi_match 쿼리 구성			
			List<Query> rawsShouldQueries = keywords.stream()
				.map(term -> Query.of(q -> q
					.multiMatch(m -> m
							.query(term)
							.fields(RAW_SEARCH_INFO_FIELDS)
					)
			))
			.collect(Collectors.toList());

			// rawElemInfo 레벨에 nested multi_match 쿼리
			// 각각의 multi_match 쿼리 구성
			List<Query> rawElemInfoShouldQueries = keywords.stream()
					.map(term -> Query.of(q -> q
						.nested(n -> n
							.path("rawElemInfo")
							.query(nq -> nq
                .bool(b -> b
                    .should(sq -> sq
                        .multiMatch(m -> m
                            .query(term)
                            .fields(RAW_SEARCH_ELEM_INFO_FIELDS)
                        )
                    )
                )
            	)
						)					
			))
			.collect(Collectors.toList());

			// 기본 검색 쿼리 추가
			boolQueryBuilder.should(rawsShouldQueries); // RAW_SEARCH_INFO_FIELDS
			boolQueryBuilder.should(rawElemInfoShouldQueries); // RAW_SEARCH_ELEM_INFO_FIELDS
			minimumShouldMatchVal += 25;
			//boolQueryBuilder.minimumShouldMatch("25%"); // 또는 적절히 조정
		}
		
		// 결과 내 재검색 조건 추가
		if (StringUtils.hasText(reSearchText)) {
			List<String> reKeywords = Arrays.stream(reSearchText.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.limit(5)
					.collect(Collectors.toList());

			// rawName 쿼리 (multi_match)
			List<Query> reRawShouldQueries = reKeywords.stream()
					.map(term -> Query.of(q -> q
						.multiMatch(m -> m
							.query(term)
							.fields(RAW_SEARCH_INFO_FIELDS)
						)
					))
					.collect(Collectors.toList());

			// rawElemInfo 쿼리 (nested + multi_match)
			List<Query> reRawElemInfoShouldQueries = reKeywords.stream()
					.map(term -> Query.of(q -> q
						.nested(n -> n
							.path("rawElemInfo")
							.query(nq -> nq
								.bool(b -> b
									.should(sq -> sq
										.multiMatch(m -> m
														.query(term)
														.fields(RAW_SEARCH_ELEM_INFO_FIELDS)
										)
									)
								)
							)
						)
					))
					.collect(Collectors.toList());

			// 쿼리에 추가
			boolQueryBuilder.should(reRawShouldQueries);
			boolQueryBuilder.should(reRawElemInfoShouldQueries);

			// reSearch 조건도 있어야 하므로 minimumShouldMatch 강화 가능
			minimumShouldMatchVal += 25;
			//boolQueryBuilder.minimumShouldMatch("50%"); // 또는 적절히 조정
		}

		//hashtag 필터링
		String hashtag = param.getHashtag();
		if (StringUtils.hasText(hashtag)) {
		    // 해시태그를 개별적으로 분할
		    String[] hashtags = hashtag.trim().split("#");
		    
		    BoolQuery.Builder hashtagBoolQuery = new BoolQuery.Builder();

		    for (String tag : hashtags) {
		        if (!tag.isEmpty()) {
		            hashtagBoolQuery.must(m -> m.wildcard(w -> w
		                .field("rawDetailInfo.hashtag.keyword")
		                .value(String.format("*#%s*", tag.trim())) // 개별 태그 필터링
		            ));
		        }
		    }

		    NestedQuery hashtagQuery = NestedQuery.of(n -> n
		        .path("rawDetailInfo")
		        .query(q -> q.bool(hashtagBoolQuery.build())) // 모든 태그가 포함된 문서만 검색
		    );

		    boolQueryBuilder.filter(q -> q.nested(hashtagQuery)); // rawDetailInfo.hastag 필터 적용
		}
		
		boolean addedIngdCountFilter = false;
		// 성분수 최대 필터
		if (param.getMaxIngdCount() > 0) {
			NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(r -> r
					.field("rawElemInfoCount") // 필드명 설정
					.lte(0.0 + param.getMaxIngdCount()) // 최대값 조건
			);

			boolQueryBuilder.filter(f -> f.range(n -> n.number(numberRangeQuery)));
			addedIngdCountFilter = true;
		}

		// 성분수 최소 필터
		if (param.getMinIngdCount() > 0) {
			NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(r -> r
					.field("rawElemInfoCount") // 필드명 설정
					.gte(0.0 + param.getMinIngdCount()) // 최대값 조건
			);

			boolQueryBuilder.filter(f -> f.range(n -> n.number(numberRangeQuery)));			
			addedIngdCountFilter = true;
		}

		if(addedIngdCountFilter){
			minimumShouldMatchVal += 25;
		}

		if(minimumShouldMatchVal > 0){
			boolQueryBuilder.minimumShouldMatch(minimumShouldMatchVal + "%"); // 또는 적절히 조정
		}

		// 원료타입 필터링 조건 추가
		List<String> categories = param.getCategories();
		if (categories != null && !categories.isEmpty()) {
		    BoolQuery.Builder categoryBoolQuery = new BoolQuery.Builder();

		    for (String category : categories) {
		        categoryBoolQuery.must(m -> m
		            .nested(n -> n // 개별적으로 `nested` 조건 적용
		                .path("rawTypeInfo")
		                .query(q -> q.term(t -> t
		                    .field("rawTypeInfo.codes")
		                    .value(category) // 개별적으로 검색
		                ))
		            )
		        );
		    }

		    boolQueryBuilder.filter(f -> f.bool(categoryBoolQuery.build())); // 모든 필터 적용
		}
		
		// 원료 성분 필터링 조건 추가
		List<String> ingdIdList = param.getIngdIds();
		if (ingdIdList != null && !ingdIdList.isEmpty()) {
		    BoolQuery.Builder ingdIdBoolQuery = new BoolQuery.Builder();

		    for (String ingdId : ingdIdList) {
		        ingdIdBoolQuery.must(m -> m
		            .nested(n -> n // 개별적으로 `nested` 조건 적용
		                .path("rawElemInfo")
		                .query(q -> q.term(t -> t
		                    .field("rawElemInfo.id")
		                    .value(Integer.parseInt(ingdId)) // String → Integer 변환 후 검색
		                ))
		            )
		        );
		    }

		    boolQueryBuilder.filter(f -> f.bool(ingdIdBoolQuery.build())); // 모든 필터 적용
		}

		// 하이라이트 설정
		Highlight.Builder highlightBuilder = new Highlight.Builder();
		{
			//highlightBuilder.fragmentSize(SNIPPET_LENGTH); // 하이라이트 조각 크기
			highlightBuilder.preTags("<em>");
			highlightBuilder.postTags("</em>");
		
			// 루트 필드 하이라이트 적용
			RAW_SEARCH_INFO_FIELDS.stream()
				.map(f -> f.replaceAll("\\^.*", "")) // ^1.6 같은 가중치 제거
				.distinct()
				.forEach(field -> highlightBuilder.fields(field, h -> h));

			// nested 필드 하이라이트 적용
			RAW_SEARCH_ELEM_INFO_FIELDS.stream()
				.map(f -> f.replaceAll("\\^.*", "")) // ^1.6 같은 가중치 제거
				.distinct()
				.forEach(field -> highlightBuilder.fields(field, h -> h));
		}
		Highlight highlight = highlightBuilder.build();
		

		// 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> {
			s.index(param.getIndex())
				.query(q -> q.bool(boolQueryBuilder.build()))
				.highlight(highlight)
				.from(param.getOffset()) // 시작 위치

				// 페이지당 문서 수, 최대 hitCount로 제한
				.size(Math.min(param.getRowSize(), Math.max(0, MAX_HITS_COUNT - param.getOffset())));

			// 동적 정렬 추가
			if (param.getSortField() != null && !param.getSortField().isEmpty()) {
				s.sort(so -> so.field(f -> f
						.field(param.getSortField()) // 정렬 필드 설정
						.order("desc".equalsIgnoreCase(param.getSortOrder()) ? SortOrder.Desc : SortOrder.Asc) // 정렬 순서 설정
				));
			}

			return s;
		});

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
		SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		// 검색 결과 처리
		EsResultDTO result = esResponseToEsResultDTO(response);

		if (MAX_HITS_COUNT < result.getTotal()) {
			result.setTotal(MAX_HITS_COUNT);
		}

		return result;
	}

	/**
	 * 코칭 원료 ID로 상세 정보 리턴
	 * 
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchCochingRawById(EsSearchDTO param) throws IOException {

		// Build the  query
		Query query = Query.of(q -> q
				.match(m -> m
					.field("rawSeq")
					.query(param.getId())
				)
		);

		// Create the search request
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(query)
				.from(0) // 시작 위치
				.size(1) // 페이지당 문서 수
		);

		// 검색 요청 실행
		@SuppressWarnings("unchecked")
		SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		EsResultDTO result = esResponseToEsResultDTO(response);

		return result;
	}

	private static String getSearchText(String text, boolean isEc) {
		if (StringUtils.hasText(text)) {

			String result = text.trim().toLowerCase();
			if(isEc){
				return escapeElasticsearchSpecialChars(result);
			}
			return result;
		}

		return "";
	}

	// 유틸리티 메서드 수정: '+-' 제외하고 나머지 특수 문자만 이스케이프 처리
	private static String escapeElasticsearchSpecialChars(String text) {
		if (StringUtils.hasText(text)) {
			// Elasticsearch에서 의미 있는 특수문자 목록
			final String regex = "([+\\-=&|><!(){}\\[\\]^\"~*?:/\\\\])";
			return text.replaceAll(regex, "\\\\$1");
		}

		return "";
	}
	
	// 구둣점 문자제거
	private static String replaceElasticsearchSpecialChars(String text) {
		if (StringUtils.hasText(text)) {
			String result = text.replaceAll("[.()!?;:\\[\\]{}]", " ");
			
			// 연속된 공백을 하나의 공백으로 변환
	        result = result.replaceAll("\\s+", " ");
			return result;
		}

		return "";
	}

	// 응답결과를 DTO로 변환
	private EsResultDTO esResponseToEsResultDTO(SearchResponse<Map<String, Object>> response) {
    HitsMetadata<Map<String, Object>> hits = response.hits();		
	  List<Object> itemList = hits.hits().stream()
			.map(hit -> {
          Map<String, Object> source = hit.source();
          Map<String, List<String>> highlightMap = hit.highlight();

          return EsHitDTO.builder()
            .score(hit.score()) // score 추가
            .source(source)  // 원본 데이터
            .highlight(highlightMap)  // 하이라이트 데이터 추가
            .build();
        }
			)
			.collect(Collectors.toList());
	
	  EsResultDTO result = EsResultDTO.builder()
			.list(itemList)
			.maxScore(hits.maxScore())
			.total(hits.total().value())
			.build();

    return result;
  }
}
