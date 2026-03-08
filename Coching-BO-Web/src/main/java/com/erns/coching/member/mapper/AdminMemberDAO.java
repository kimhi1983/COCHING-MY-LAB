package com.erns.coching.member.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.AdminMemberSearchDTO;
import com.erns.coching.member.domain.AdminMemberVO;

/**
 *
 * <p>관리자 정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface AdminMemberDAO {

	public List<Map<String, Object>> getList(AdminMemberSearchDTO param);
	public int getListCount(AdminMemberSearchDTO param);

	public Map<String, Object> load(AdminMemberSearchDTO param);
	public AdminMemberVO loadWithCheckingPasswd(AdminMemberVO param);
	public int insert(AdminMemberVO param);
	public int update(AdminMemberVO param);


	// 추가
	public int updatePwd(AdminMemberVO param);
//	public int updatePwErrorCountReset(AdminMemberVO param);
//	public int updateGrade(AdminMemberVO param);
//	public int updateUseYn(AdminMemberVO param);

}
