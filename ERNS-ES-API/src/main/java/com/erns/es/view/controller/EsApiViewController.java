package com.erns.es.view.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.es.common.controller.AbstractViewController;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>ERNS ES API 관리자 View</p>
 *
 * @author Hunwoo Park
 *
 */
@Controller
@Slf4j
@RequestMapping("/ernsesapi-bo")
public class EsApiViewController extends AbstractViewController{

	@RequestMapping(value= {"/**", "/**/**"})
    public String main(ModelMap model, HttpServletRequest req) {
		log.debug("ErnsEsApi App");

		String reqURL = req.getRequestURL().toString();
		log.debug("REQ_URL:{}", reqURL );
		model.addAttribute("currentURL", reqURL);

		//return "app/apiView";
		return "app/apiView_index";
    }

}
