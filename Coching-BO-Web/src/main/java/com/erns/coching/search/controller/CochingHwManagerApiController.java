package com.erns.coching.search.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.search.domain.EsSearchDTO;
import com.erns.coching.search.domain.HwBrandVO;
import com.erns.coching.search.service.EsSearchService;
import com.erns.coching.search.service.HwBrandService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 코칭 화해 상품 ES 관리 API Controller
 * </p>
 * 
 * @author Hunwoo Park
 */
@Slf4j
@RestController
@RequestMapping("/api/search/manager/coching/hw")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class CochingHwManagerApiController extends AbstractApiController {

  @Autowired
  private HwBrandService hwBrandService;

  @Autowired
  private SysPropService sysPropService;

  @Autowired
  private EsSearchService esSearchService;

  /**
   * 코칭 화해 상품 DB 테이블 초기화
   * 
   * @param request
   * @param param
   * @param errors
   * @return
   */
  @ApiLogging(logType = UserLogType.ES_COCHING_HW_BRAND_RESET_TABLE)
  @RequestMapping("/resetHwBrandTable.api")
  public ApiResult resetHwBrandTable(
      HttpServletRequest request,
      @RequestBody Map<String, Object> param,
      Errors errors) {

    ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
      return bindError(errors, axRet);
    }

    try {
      // API Call
      resetHwBrand();

      axRet.set(ApiResultError.NO_ERROR);

    } catch (Exception e) {
      log.error("error", e);
      return axRet.set(ApiResultError.ERROR_DEFAULT);
    }

    return axRet;
  }

  @SuppressWarnings("unchecked")
  private void resetHwBrand() {

    EsSearchDTO param = new EsSearchDTO();
    {
      SysPropVO loadProp = sysPropService.loadVo(Const.ES_HW_PROD_ACTIVE_INDEX_SYSKEY);
      param.setIndex(loadProp.getSysValue());
    }

    try {
      // Call ES API
      ApiResult result = esSearchService.searchHwBrandTop10000(param);
      if (!ApiResultError.isOk(result.getResultCode())) {
        log.error("searchHwBrandTop10000 Error", result.getResultMessage());
        return;
      }

      Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
      Map<String, Object> aggregations = (Map<String, Object>) resultData.get("aggregations");
      Map<String, Object> top_brands = (Map<String, Object>) aggregations.get("top_brands");
      List<Map<String, Object>> buckets = (List<Map<String, Object>>) top_brands.get("buckets");

      List<HwBrandVO> hwBrandList = new ArrayList<>();

      int seq = 1;
      for (Map<String, Object> bucket : buckets) {
        String brandFullName = (String) bucket.get("key");

        // 정규식을 사용해서 한글명과 영문명 분리
        String brandKo = "";
        String brandEn = "";

        if (brandFullName != null && !brandFullName.trim().isEmpty()) {
          // "한글명 (영문명)" 형식 매칭
          java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(.+?)\\s*\\(([^)]*)\\)$");
          java.util.regex.Matcher matcher = pattern.matcher(brandFullName.trim());

          if (matcher.find()) {
            // 괄호가 있는 경우
            brandKo = matcher.group(1).trim();
            brandEn = matcher.group(2).trim();
          } else {
            // 괄호가 없는 경우 전체를 한글명으로 처리
            brandKo = brandFullName.trim();
            brandEn = "";
          }
        }

        int productCount = (int) bucket.get("doc_count");

        HwBrandVO hwBrand = HwBrandVO.builder()
            .seq(seq++)
            .brandFullName(brandFullName)
            .brandKo(brandKo)
            .brandEn(brandEn)
            .count(productCount)
            .build();

        hwBrandList.add(hwBrand);
      }

      hwBrandService.deleteAll();
      hwBrandService.insert(hwBrandList);
    } catch (Exception e) {
      log.error("resetHwBrand Error", e);
    }
  }

}
