package com.erns.es.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.erns.es.common.Const;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>검색용 DTO Base</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseSearchDTO {

	protected int page = 1;		//요청페이지
	protected int offset = 0;	//요청 offset
	protected int pageSize = Const.DEFAULT_PAGESIZE;	//페이지 수
    protected int rowSize = Const.DEFAULT_ROWSIZE; 		//한 페이지의 row 수
    protected String searchField = "";
    protected String searchText = "";

    public int getOffset() {
        return (this.rowSize * (this.page - 1));
    }

}
