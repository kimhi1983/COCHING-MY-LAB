package com.erns.coching.view.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.controller.AbstractViewController;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>나이스오너 관리자 View</p>
 *
 * @author Hunwoo Park
 *
 */
@Controller
@Slf4j
@RequestMapping("/coching-bo")
public class BackOfficeController extends AbstractViewController{

	@RequestMapping(value= {"/**", "/**/**"})
    public String main(ModelMap model, HttpServletRequest req) {
		log.debug("Nice 4B2B BackOffice App");

		String reqURL = req.getRequestURL().toString();
		log.debug("REQ_URL:{}", reqURL );
		model.addAttribute("currentURL", reqURL);

		return "app/backOffice";
    }

}
