package com.erns.coching.rnd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.util.RestTemplateUtil;
import com.erns.coching.rnd.domain.AiLabResVO;
import com.erns.coching.rnd.domain.AiPrescriptionDTO;
import com.erns.coching.rnd.domain.IngredientDTO;
import com.erns.coching.rnd.domain.LabElementInfVO;
import com.erns.coching.rnd.domain.LabMasterVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RndService extends AbstractCochingApiService {

  // 테스트 모드
	@Value("${prsc.ai.demo.api.isDummyTest:true}")
	protected boolean isDummyTest;

	@Value("${prsc.ai.demo.api.protocal:http}")
	protected String prscApiProtocal;

	@Value("${prsc.ai.demo.api.host:localhost}")
	protected String prscApiHostname;

	@Value("${prsc.ai.demo.api.port:8000}")
	protected int prscApiPort;

	@Autowired
	private AiLabResService aiLabResService;

	@Autowired
	private LabMasterService labMasterService;

	@Autowired
	private LabElementInfService labElementInfService;

	@Autowired
	public RndService(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}

  /**
	 * AI API 의 Base URL 리턴
	 * 
	 * @return
	 */
	protected final String getApiBaseUrl() {
		return String.format("%s://%s:%d", prscApiProtocal, prscApiHostname, prscApiPort);
	}

  /**
	 * AI API 의 전체 경로를 리턴
	 * 
	 * @param subPath
	 * @return
	 */
	protected final String getApiUrl(String pSubPath) {
		String subPath = pSubPath;
		if (subPath.startsWith("/")) {
			subPath = subPath.substring(1);
		}
		return String.format("%s/%s", getApiBaseUrl(), subPath);
	}

  /**
   * AI 처방
   * 
   * @param res
   * @param param
   * @throws JsonProcessingException
   */
  public ApiResult aiPrscResultV1(AiPrescriptionDTO param) throws JsonProcessingException {
		
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    String apiUrl = getApiUrl("/formula");

    String model_name = param.getAiOptModel();

    if(model_name.equals("gpt-4o-mini")) {
      param.setModel_name("gpt-4o-mini");
      param.setTemperature(param.getAiOptTemperature());
    } else {
      param.setModel_name(model_name);
      param.setTemperature(1);
    }

		if (isDummyTest) {
      String filePath = String.format("classpath:/api/rnd/formula_ok.json");
			Map<String, Object> dummyResult = loadDummyData(filePath);
			axRet = new ApiResult(dummyResult);
      
    } else {
			// By-PASS 리턴
			log.debug("Call AI Api:{}", apiUrl);
			axRet = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);
		}

		long labMstSeq = param.getLabMstSeq();
		if(ApiResultError.isOk(axRet.getResultCode())) { 
			//성공시 DB 저장

			//파라미터 추출
			String requestDataJson = null;
			{
				Map<String, Object> requestData = (Map<String, Object>) axRet.get("request_data");	
				requestDataJson = objectMapper.writeValueAsString(requestData);

				if(labMstSeq == 0L) {
					//LabMaster 저장
					LabMasterVO labMasterVo = LabMasterVO.builder()
					.title(param.getTitle())
					.prodCateGroup(param.getProdCateGroup())
					.prodCateCode(param.getProdCateCode())
					.content(param.getDirection())
					.rgtr(param.getMembSeq())
					.chnr(param.getMembSeq())
					.build();

					labMasterService.insert(labMasterVo);
					labMstSeq = labMasterVo.getLabMstSeq();

					//LabElementInf 저장
					List<LabElementInfVO> labElementInfList = new ArrayList<>();
					List<Map<String, Object>> ingredients = (List<Map<String, Object>>) requestData.get("ingredients");
					for(Map<String, Object> ingredient : ingredients) {
						String repName = (String) ingredient.get("repName");
						String repNameEn = (String) ingredient.get("repNameEn");
						Integer esId = (Integer) ingredient.get("esId");

						LabElementInfVO labElementInfVo = LabElementInfVO.builder()
						.labMstSeq(labMstSeq)
						.rawElmId(esId)
						.rawElmKr(repName)
						.rawElmEn(repNameEn)
						.build();
						labElementInfList.add(labElementInfVo);
					}
					labElementInfService.insert(labElementInfList);
				}
			}

			//결과 JSON 저장
			String resJson = objectMapper.writeValueAsString(axRet);

			//AiLabRes 저장
			AiLabResVO aiLabResVo = AiLabResVO.builder()
			.labMstSeq(labMstSeq)
			.aiLabParam(requestDataJson)
			.aiLabRes(resJson)
			.rgtr(param.getMembSeq())
			.build();
			aiLabResService.insert(aiLabResVo);

			axRet.put("labMstSeq", labMstSeq);
			axRet.put("aiLabMstSeq", aiLabResVo.getAiLabMstSeq());
		}

		return axRet;

	}
}
