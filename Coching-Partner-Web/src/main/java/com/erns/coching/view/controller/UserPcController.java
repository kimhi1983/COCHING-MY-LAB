package com.erns.coching.view.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.controller.AbstractViewController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/coching-pn")
public class UserPcController extends AbstractViewController{

	@RequestMapping(value= {"/**", "/**/**"})
    public String main(ModelMap model, HttpServletRequest req) {
		log.debug("Coching partner pc");

		String reqURL = req.getRequestURL().toString();
		log.debug("REQ_URL:{}", reqURL );
		model.addAttribute("currentURL", reqURL);

		return "app/userPc";
//		return "app/system"; //시스템점검
    }

}
