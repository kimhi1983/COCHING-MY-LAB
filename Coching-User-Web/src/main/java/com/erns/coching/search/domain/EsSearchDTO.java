package com.erns.coching.search.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidEsSearch1012;
import com.erns.coching.search.domain.vg.ValidEsSearch2012;

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
//			ValidEsSearch1011.class,	//원료
//			ValidEsSearch2011.class,	//상품
	    }, message = "text 누락")
	private String text;
	
	
	@NotEmpty(groups = {
			ValidEsSearch1012.class,	//원료
			ValidEsSearch2012.class,	//상품
	    }, message = "id 누락")
	private String id;

	//힌트 필드 hintField
	@NotEmpty(groups = {
		}, message = "hintField 누락")
	private String hintField;

	//원료 해시 테그
	@NotEmpty(groups = {
		}, message = "hashtag 누락")
	private String hashtag;

	//원료 성분 필터링
	private List<String> ingdIds; 

	//원료 성분
	private int minIngdCount; //최소 성분 갯수
	private int maxIngdCount; //최대 성분 갯수

	private int ptnSeq; // 파트너 시퀀스
	private String ptnNation; // 파트너 국가코드
	

	private List<String> categories; // 필터링할 카테고리 코드 리스트
	
	private String reSearchText; // 결과 내 재검색 텍스트
	
	
	//정렬 필드
	private String sortField;
	private String sortOrder;
	
	private String index; // 인덱스

	// 정확한 매치만 검색할지 여부 (true: keyword 필드의 정확한 매치만, false: 정확한 매치 + 부분 매치)
	private boolean exactMatchOnly = false;
}
