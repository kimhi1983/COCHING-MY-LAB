package com.erns.coching.favorite.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.favorite.domain.FavoriteSearchDTO;
import com.erns.coching.favorite.domain.FavoriteSetDTO;
import com.erns.coching.favorite.domain.FavoriteVO;
import com.erns.coching.favorite.domain.vg.ValidFavorite0011;
import com.erns.coching.favorite.domain.vg.ValidFavorite0020;
import com.erns.coching.favorite.domain.vg.ValidFavorite0040;
import com.erns.coching.favorite.service.FavoriteService;
import com.erns.coching.login.domain.LoginUserDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 찜 API
 * @author hw.park@erns.co.kr
 */
@Slf4j
@RestController
@RequestMapping("/api/favorite")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class FavoriteApiController extends AbstractApiController {
  
  @Autowired
  private FavoriteService favoriteService;

  /**
	 * 찜한 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.FAVORITE_MY_LIST)
	@PostMapping("/my/list.api")
	public ApiResult getMyList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidFavorite0011.class) FavoriteSearchDTO param,
            Errors errors) {
		
		ApiResult axRet = new ApiResult();
    if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setMembSeq(loginUser.getSeq());
		    
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = favoriteService.getListCount(param);
		List<Map<String, Object>> list = new ArrayList<>();
		if(totalItem > 0) {
			list = favoriteService.getList(param); //쪽지 목록			
		}
    pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 찜하기
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.FAVORITE_MY_TOGGLE)
	@PostMapping("/my/toggle.api")
	public ApiResult toggleMyFavorite(
            HttpServletRequest request,
            @RequestBody @Validated(ValidFavorite0020.class) FavoriteSetDTO param,
            Errors errors) {
		log.debug("param: {}", param);
		
		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
    param.setMembSeq(loginUser.getSeq());
        
		try{
			//찜 등록
			FavoriteVO vo = FavoriteVO.AddFavoriteBuilder()
          .fromDto(param)
					.build();
			
			int ret = favoriteService.toggle(vo);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR, getLocale());
        
		}catch(Exception e) {
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}
		
		return axRet;
	}

	/**
	 * 찜목록 삭제
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.FAVORITE_MY_DEL)
	@PostMapping("/my/del.api")
	public ApiResult deleteMessage(
						HttpServletRequest request,
						@RequestBody @Validated(ValidFavorite0040.class) FavoriteSearchDTO param,
						Errors errors) {
		log.debug("param: {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			param.setMembSeq(loginUser.getSeq());
			
			int ret = favoriteService.deleteForUser(param);
			if(ret > 0) {
				axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR, getLocale());
			}
						
		}catch(Exception e) {
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet;
	}
  
}
