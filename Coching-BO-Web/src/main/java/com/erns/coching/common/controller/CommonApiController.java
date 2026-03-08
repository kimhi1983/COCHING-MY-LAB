package com.erns.coching.common.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.file.domain.PageImageDTO;
import com.erns.coching.common.file.domain.PdfInfoVO;
import com.erns.coching.common.file.domain.ResultPdfAllProcessDTO;
import com.erns.coching.common.file.service.PdfInfoService;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>공통 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonApiController extends AbstractApiController {

    @Autowired
    private PdfInfoService pdfInfoService;

    @Getter
    @Setter
    static class PdfInfoDTO {
        private String fileId;
        private String noCache;
    }

    @ApiLogging(logType = UserLogType.FILE_PDF_IMAGE_INFO)
	@RequestMapping(value = "/pdfImageInfo.api")
	public ApiResult pdfImageInfo(
        HttpServletRequest request,
        @RequestBody @Validated() PdfInfoDTO param,
        Errors errors) {

        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
            return bindError(errors, axRet);
        }

        try {
            //PDF INFO 부터 체크
            PdfInfoVO pdfVO = pdfInfoService.loadVo(param.fileId);
            if(pdfVO != null){
                List<PageImageDTO> imgList = pdfInfoService.getPageImageList(pdfVO);            
                return axRet.setResultData(imgList).set(ApiResultError.NO_ERROR);
            }

            //변환처리
            ResultPdfAllProcessDTO pdfAllProcess = pdfInfoService.pdfAllProcess(param.fileId);
            if(pdfAllProcess == null){
                return axRet.set(ApiResultError.ERROR_DEFAULT).setResultMessage("File Not Found(1)");
            }

            //다시시도
            pdfVO = pdfInfoService.loadVo(param.fileId);
            if(pdfVO != null){
                List<PageImageDTO> imgList = pdfInfoService.getPageImageList(pdfVO);            
                return axRet.setResultData(imgList).set(ApiResultError.NO_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return axRet.set(ApiResultError.ERROR_DEFAULT).setResultMessage("File Not Found(0)");
        }
        
        return axRet.set(ApiResultError.ERROR_DEFAULT).setResultMessage("Cannot get pdf info");
	}

    
}
