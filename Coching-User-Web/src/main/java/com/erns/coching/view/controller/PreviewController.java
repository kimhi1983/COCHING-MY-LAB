package com.erns.coching.view.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.erns.coching.common.controller.AbstractViewController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/common/raw")
public class PreviewController extends AbstractViewController{

	private static final String REDIRECT_USER_RAW_DETAIL_PREVIEW_URL = "redirect:/coching-user/search/preview";

	@RequestMapping("/preview")
    public String main(ModelMap model, HttpServletRequest req,
					   RedirectAttributes redirect) throws JsonProcessingException {
		log.debug("Coching User Raw Detail Preview");
		String preData = "";
		// JSON 파싱을 위한 ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		String dataStr = req.getParameter("data");

		try {
			Object jsonObject  = objectMapper.readValue(dataStr, Object.class);
			preData = objectMapper.writeValueAsString(jsonObject);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		// Model에 추가
		redirect.addFlashAttribute("previewData", preData);

		return REDIRECT_USER_RAW_DETAIL_PREVIEW_URL;
    }

}
