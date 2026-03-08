package com.erns.es.search.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.erns.es.search.domain.EsResultDTO;
import com.erns.es.search.domain.EsSearchDTO;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EsSearchService {

	private final ElasticsearchClient client;

	public static final int MAX_HITS_COUNT = 600; // 최대 Hit_Count

	

	@Autowired
	public EsSearchService(ElasticsearchClient client) {
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
		"product.name^1.1"
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
		String text = escapeElasticsearchSpecialChars(param.getText());
		String reSearchText = param.getReSearchText() != null
				? escapeElasticsearchSpecialChars(param.getReSearchText())
				: null;

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

		if (StringUtils.hasText(text)) {
			// product.name에 대한 query_string 쿼리
			QueryStringQuery productQuery = QueryStringQuery.of(q -> q
					.query(text)
					.fields(PROD_SEARCH_INFO_FIELDS)
			// .defaultOperator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
			);

			// ingredients.korean에 대한 nested query_string 쿼리
			NestedQuery ingredientsQuery = NestedQuery.of(n -> n
					.path("ingredients")
					.query(q -> q.queryString(qs -> qs
							.query(text)
							.fields(PROD_SEARCH_ELEM_INFO_FIELDS)
					// .defaultOperator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
					)));

			// 기본 검색 쿼리 추가
			boolQueryBuilder.should(q -> q.queryString(productQuery));
			boolQueryBuilder.should(q -> q.nested(ingredientsQuery));
		}

		// 결과 내 재검색 조건 추가
		if (reSearchText != null && !reSearchText.isEmpty()) {
			// product.name에 대한 query_string 쿼리
			QueryStringQuery reSearchProductQuery = QueryStringQuery.of(q -> q
					.query(reSearchText)
					.fields(PROD_SEARCH_INFO_FIELDS));

			// ingredients.korean 및 ingredients.english에 대한 nested query_string 쿼리
			NestedQuery reSearchIngredientsQuery = NestedQuery.of(n -> n
					.path("ingredients")
					.query(q -> q.queryString(qs -> qs
							.query(reSearchText)
							.fields(PROD_SEARCH_ELEM_INFO_FIELDS))));

			// reSearchText 조건을 필터로 추가
			boolQueryBuilder.should(f -> f.queryString(reSearchProductQuery)); // product.name 필터
			boolQueryBuilder.should(f -> f.nested(reSearchIngredientsQuery)); // ingredients 필터

			// 최소 조건 만족 설정
			boolQueryBuilder.minimumShouldMatch("50%"); // 최소 매칭 조건
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

		// 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> {
			s.index(param.getIndex())
					.query(q -> q.bool(boolQueryBuilder.build()))
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

		SearchResponse<Object> response = client.search(searchRequest, Object.class);
		// return searchResponse.hits().hits().stream()
		// .map(Hit::source)
		// .collect(Collectors.toList());

		HitsMetadata<Object> hits = response.hits();
		List<Object> itemList = hits.hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		long totalCount = hits.total().value();
		if (totalCount > MAX_HITS_COUNT) {
			totalCount = MAX_HITS_COUNT;
		}
		EsResultDTO result = EsResultDTO.builder()
				.list(itemList)
				.maxScore(hits.maxScore())
				.total(totalCount)
				.build();

		return result;
	}

	/**
	 * 화해상품정보 텍스트 검색
	 * 
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public EsResultDTO searchHwProduct2(EsSearchDTO param) throws IOException {
		// ingredients.korean 필드에 대한 nested 쿼리
		NestedQuery ingredientsQuery = NestedQuery.of(n -> n
				.path("ingredients")
				.query(q -> q.match(m -> m
						.field("ingredients.korean")
						.query(param.getText()))));

		// 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(q -> q.nested(ingredientsQuery))
				.from(param.getOffset()) // 시작 위치
				// 페이지당 문서 수
				.size(Math.min(param.getRowSize(), Math.max(0, MAX_HITS_COUNT - param.getOffset()))) // 최대 hitCount로 제한
		);

		SearchResponse<Object> response = client.search(searchRequest, Object.class);

		HitsMetadata<Object> hits = response.hits();
		List<Object> itemList = hits.hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		long totalCount = hits.total().value();
		if (totalCount > MAX_HITS_COUNT) {
			totalCount = MAX_HITS_COUNT;
		}
		EsResultDTO result = EsResultDTO.builder()
				.list(itemList)
				.maxScore(hits.maxScore())
				.total(totalCount)
				.build();

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

		// Build the Match query
		MatchQuery matchQuery = MatchQuery.of(m -> m.field("product.id").query(param.getId()));

		// Create the search request
		SearchRequest searchRequest = SearchRequest
				.of(s -> s.index(param.getIndex())
						.query(q -> q.match(matchQuery))
						.from(0) // 시작 위치
						.size(1) // 페이지당 문서 수
				);

		// Execute the search request
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

	//원료 성분 검색
	public static final List<String> INGREDIENT_SEARCH_RESULT_FIELDS = Arrays.asList(
		"id", "rep_name", "rep_name_en",
					"kcai_id", "ewg_id", "hw_ingd_id",
					"inci", "names_ko", "names_en",
					"purposes", "cas_no", "ec_no",
					"formula", "exp_url_1", "exp_url_2",
					"ewg_score_min", "ewg_score", "ewg_data_lebel",
					"ewg_rawcno_id", "ewg_url"
	);

	//성분 keyword
	public static final List<String> INGREDIENT_SEARCH_KEYWORD_FIELDS = Arrays.asList(
			"names_ko.keyword"
			, "names_en.keyword"
			, "cas_no.keyword"
			, "ec_no.keyword"
	);

	//성분 keyword
	public static final List<String> INGREDIENT_SEARCH_PARTIAL_FIELDS = Arrays.asList(
			"names_ko"
			, "names_en"
			, "cas_no"
			, "ec_no"
	);
	
	//성분 검색어
	public static final List<String> INGREDIENT_SEARCH_WORD_FIELDS = Arrays.asList(
			"names_ko"
			, "names_en"
			, "cas_no"
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
	    //String text = escapeElasticsearchSpecialChars(param.getText()).toLowerCase();
	    String text = replaceElasticsearchSpecialChars(param.getText()).toLowerCase();
	    log.debug("orgText:[{}], text:[{}]", param.getText(), text);
	
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
			
			// 기본 검색 쿼리 추가
			// `names_ko`, `names_en`에 대한 검색
			String matchText = text.trim().replaceAll("\\s+", "-");
			log.debug("matchText:[{}]", matchText);
			for (String field : INGREDIENT_SEARCH_WORD_FIELDS) {
				log.debug("field:[{}]", field);
				boolQueryBuilder.should(q -> q.match(m -> m
			        .field(field) // ✅ 검색할 필드 지정
			        .query(matchText) 
			        .boost(2.0f) // ✅ 가중치 적용 가능 (선택 사항)
			    ));
			}
			
			
			// 부분 검색 (ngram 필드를 활용한 query_string 검색)
			BoolQuery.Builder ngramQueryBuilder = new BoolQuery.Builder();
			for (String keyword : keywords) {
					if (!keyword.isEmpty()) {
							ngramQueryBuilder.should(q -> q.queryString(qs -> qs
									.query(keyword.trim()) // 입력된 키워드를 query_string으로 검색
									.fields(INGREDIENT_SEARCH_PARTIAL_FIELDS) // ngram 적용 필드 대상
									.boost(1.0f) // ✅ `boost`를 추가하여 `_score` 영향을 주도록 설정
							));
					}
			}
			boolQueryBuilder.should(q -> q.bool(ngramQueryBuilder.build()));
			
			
//			boolQueryBuilder.minimumShouldMatch("1");
	    }

		// Elasticsearch 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(q -> q.bool(boolQueryBuilder.build()))
				.source(src -> src
						.filter(f -> f
								.includes(INGREDIENT_SEARCH_RESULT_FIELDS))) // 반환할 필드 지정				
				.from(param.getOffset()) // 시작 위치
				.size(Math.min(param.getRowSize(), Math.max(0, MAX_HITS_COUNT - param.getOffset()))) // 페이지당 문서 수
		);

		// 검색 요청 실행
		SearchResponse<Object> response = client.search(searchRequest, Object.class);

		// 검색 결과 처리
		HitsMetadata<Object> hits = response.hits();
		List<Object> itemList = hits.hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		long totalCount = hits.total().value();
		if (totalCount > MAX_HITS_COUNT) {
			totalCount = MAX_HITS_COUNT;
		}

		// 결과 DTO 생성
		EsResultDTO result = EsResultDTO.builder()
				.list(itemList)
				.maxScore(hits.maxScore())
				.total(totalCount)
				.build();

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
		MatchQuery matchQuery = MatchQuery.of(m -> m.field(fieldName).query(param.getId()));

		// Create the search request
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.index(param.getIndex())
				.query(q -> q.match(matchQuery))
				.source(src -> src
						.filter(f -> f
								.includes( // 반환할 필드 지정
									INGREDIENT_SEARCH_RESULT_FIELDS)))
				.from(0) // 시작 위치
				.size(1) // 페이지당 문서 수
		);

		// 검색 요청 실행
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

	//원료 검색
	public static final List<String> RAW_SEARCH_INFO_FIELDS = Arrays.asList(
		"rawName^1.6"
	);

	//원료 성분 검색
	public static final List<String> RAW_SEARCH_ELEM_INFO_FIELDS = Arrays.asList(
		"rawElemInfo.repNameKo^1.5"	
			, "rawElemInfo.repNameEn^1.5"
			, "rawElemInfo.namesKo^1.2"
			, "rawElemInfo.namesEn^1.2"
			, "rawElemInfo.casNo^1.2"
			, "rawElemInfo.ecNo^1.2"
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
		String text = escapeElasticsearchSpecialChars(param.getText());
		String reSearchText = param.getReSearchText() != null
				? escapeElasticsearchSpecialChars(param.getReSearchText())
				: null;

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
		
		if (StringUtils.hasText(text)) {

			// product.name에 대한 query_string 쿼리
			QueryStringQuery rawInfoQuery = QueryStringQuery.of(q -> q
					.query(text)
					.fields(RAW_SEARCH_INFO_FIELDS)
			// .defaultOperator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
			);

			// rawElemInfo.repNameKo 등에 대한 nested query_string 쿼리
			NestedQuery rawElemInfoQuery = NestedQuery.of(n -> n
					.path("rawElemInfo")
					.query(q -> q.queryString(qs -> qs
							.query(text)
							.fields(RAW_SEARCH_ELEM_INFO_FIELDS)
					// .defaultOperator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
					)));

			// 기본 검색 쿼리 추가
			boolQueryBuilder.should(q -> q.queryString(rawInfoQuery)); // rawName 필터
			boolQueryBuilder.should(q -> q.nested(rawElemInfoQuery)); // rawElemInfo.repNameKo 등 필터			
		}
		
		// 결과 내 재검색 조건 추가
		if (reSearchText != null && !reSearchText.isEmpty()) {
			// product.name에 대한 query_string 쿼리
			QueryStringQuery reSearchRawInfoQuery = QueryStringQuery.of(q -> q
					.query(reSearchText)
					.fields(RAW_SEARCH_INFO_FIELDS));

			// rawElemInfo.repNameKo 및 rawElemInfo.repNameEn에 대한 nested query_string 쿼리
			NestedQuery reSearchRawElemInfoQuery = NestedQuery.of(n -> n
					.path("rawElemInfo")
					.query(q -> q.queryString(qs -> qs
							.query(reSearchText)
							.fields(RAW_SEARCH_ELEM_INFO_FIELDS))));

			// reSearchText 조건을 필터로 추가
			boolQueryBuilder.should(f -> f.queryString(reSearchRawInfoQuery)); // rawName 필터
			boolQueryBuilder.should(f -> f.nested(reSearchRawElemInfoQuery)); // rawElemInfo.repNameKo 등 필터

			// 최소 조건 만족 설정
			boolQueryBuilder.minimumShouldMatch("50%"); // 최소 매칭 조건
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

		
		// 성분수 최대 필터
		if (param.getMaxIngdCount() > 0) {
			NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(r -> r
					.field("rawElemInfoCount") // 필드명 설정
					.lte(0.0 + param.getMaxIngdCount()) // 최대값 조건
			);

			boolQueryBuilder.filter(f -> f.range(n -> n.number(numberRangeQuery)));
		}

		// 성분수 최소 필터
		if (param.getMinIngdCount() > 0) {
			NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(r -> r
					.field("rawElemInfoCount") // 필드명 설정
					.gte(0.0 + param.getMinIngdCount()) // 최대값 조건
			);

			boolQueryBuilder.filter(f -> f.range(n -> n.number(numberRangeQuery)));
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
		
		// 원료 선분 필터링 조건 추가
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
		

		// 검색 요청 생성
		SearchRequest searchRequest = SearchRequest.of(s -> {
			s.index(param.getIndex())
					.query(q -> q.bool(boolQueryBuilder.build()))
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

		SearchResponse<Object> response = client.search(searchRequest, Object.class);
		// return searchResponse.hits().hits().stream()
		// .map(Hit::source)
		// .collect(Collectors.toList());

		HitsMetadata<Object> hits = response.hits();
		List<Object> itemList = hits.hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		long totalCount = hits.total().value();
		if (totalCount > MAX_HITS_COUNT) {
			totalCount = MAX_HITS_COUNT;
		}
		EsResultDTO result = EsResultDTO.builder()
				.list(itemList)
				.maxScore(hits.maxScore())
				.total(totalCount)
				.build();

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

		// Build the Match query
		MatchQuery matchQuery = MatchQuery.of(m -> m.field("rawSeq").query(param.getId()));

		// Create the search request
		SearchRequest searchRequest = SearchRequest
				.of(s -> s.index(param.getIndex())
						.query(q -> q.match(matchQuery))
						.from(0) // 시작 위치
						.size(1) // 페이지당 문서 수
				);

		// Execute the search request
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

	// 유틸리티 메서드 수정: '+-' 제외하고 나머지 특수 문자만 이스케이프 처리
	private static String escapeElasticsearchSpecialChars(String text) {
		if (StringUtils.hasText(text)) {
			return text.replaceAll("([!(){}\\[\\]^\"~*?:\\\\/|&])", "\\\\$1");
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
}
