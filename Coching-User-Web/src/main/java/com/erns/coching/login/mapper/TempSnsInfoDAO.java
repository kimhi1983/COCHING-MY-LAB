package com.erns.coching.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.login.domain.TempSnsInfoVO;

/**
*
* <p>sns info Mapper</p>
*
* @author Hunwoo Park
*
*/
@Mapper
public interface TempSnsInfoDAO {
	
	public List<TempSnsInfoVO> getList();
	
	public TempSnsInfoVO load(String key);
	public int insert(TempSnsInfoVO param);
	public int delete(String key);
	
	//만료된 임시 데이터 삭제
	public int deleteExpired();

}
