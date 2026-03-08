package com.erns.es.search.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;
import com.erns.es.search.domain.vg.ValidEsSearch0012;
import com.erns.es.search.domain.vg.ValidEsSearch1011;
import com.erns.es.search.domain.vg.ValidEsSearch1012;
import com.erns.es.search.domain.vg.ValidEsSearch2011;
import com.erns.es.search.domain.vg.ValidEsSearch2012;
import com.erns.es.search.domain.vg.ValidEsSearch3011;
import com.erns.es.search.domain.vg.ValidEsSearch3012;
import com.erns.es.search.service.EsSearchV2Service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EsSearchDTO extends BaseSearchDTO implements IReqDto{
	
	@NotEmpty(groups = {
// 			ValidEsSearch0011.class,
//			ValidEsSearch1011.class,
//			ValidEsSearch2011.class,
//			ValidEsSearch3011.class,
	    }, message = "text 누락")
	private String text;
	
	@NotEmpty(groups = {
			ValidEsSearch0012.class,
			ValidEsSearch1012.class,
			ValidEsSearch2012.class,
			ValidEsSearch3012.class,
	    }, message = "id 누락")
	private String id;

	//성분 검색용
	public static final String ID_TYPE_RAW = "R";
	public static final String ID_TYPE_KCAI = "K";
	public static final String ID_TYPE_EWG = "E";
	public static final String ID_TYPE_HW = "H";

	//ingredient 검색
	@NotEmpty(groups = {
			ValidEsSearch3012.class,
	    }, message = "idType 누락")
	@Pattern(groups = {
			ValidEsSearch3012.class,
		},regexp = "^(R|K|E|H)$" 
		, message = "idType 형식오류(R|K|E|H)")
	protected String idType;
	
	//Raw Search
	@NotEmpty(groups = {
			ValidEsSearch1011.class,
			ValidEsSearch1012.class,
			ValidEsSearch2011.class,
			ValidEsSearch2012.class,
			ValidEsSearch3011.class,
			ValidEsSearch3012.class,
	    }, message = "index 누락")
	private String index;

	@NotEmpty(groups = {
		}, message = "hashtag 누락")
	private String hashtag;
	
	//원료 성분 필터링
	private List<String> ingdIds; 
	
	
	@NotEmpty(groups = {
			//ValidEsSearch1011.class
	    }, message = "field 누락")
	private String field;
	
	@NotEmpty(groups = {
			//ValidEsSearch1011.class
	    }, message = "value 누락")
	private String value;
	
	
	private List<String> categories; // 필터링할 카테고리 코드 리스트, 원료일 경우 원료 타입
	
	private String reSearchText; // 결과 내 재검색 텍스트
	
	private int minIngdCount; //최소 성분 갯수
	private int maxIngdCount; //최대 성분 갯수
	
	//최대 hitCount
	private int maxHitsCount = EsSearchV2Service.MAX_HITS_COUNT;
	
	//정렬 필드
	private String sortField;
	private String sortOrder;
}
