package com.erns.coching.view.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.erns.coching.common.util.DateUtil;
import com.erns.coching.login.controller.BaseLoginController;
import com.erns.coching.member.domain.MemberVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 에뮬레이터 용
 * @author zuch
 *
 */
@Slf4j
@Controller
@RequestMapping("/common/sns/login")
public class SnsLoginController extends BaseLoginController{

	private static final String REDIRECT_USER_SNS_LOGIN_URL = "redirect:/coching-user/snsLogin";

	//@Value("${server.base.url:https://www.coching.co.kr}" )
	@Value("#{'${server.base.url:https://www.coching.co.kr}'.replaceAll('\\s+', '').split(',')}")
	private String SERVER_BASE_URL = "";

	@Value("${member.rejoin.limit:0}")
	private int rejoinLimitDate;

	@PostConstruct
	public void startup()
	{
		log.info("SnsLoginController is started");
	}

	/**
	 * 에뮬레이터 로그인
	 * @param request
	 * @param response
	 * @param redirect
	 * @return
	 */
	@RequestMapping("/emulator/callback.do")
	public String emulatorResult(
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirect) {

		String snsAction = ServletRequestUtils.getStringParameter(request, "snsAction", "");
		String snsLoginRet = ServletRequestUtils.getStringParameter(request, "snsLoginRet", "");
		String snsLogindUri = ServletRequestUtils.getStringParameter(request, "snsLogindUri", "");

		log.debug("snsAction:{}", snsAction);
		log.debug("snsLoginRet:{}", snsLoginRet);
		log.debug("snsLogindUri:{}", snsLogindUri);

		redirect.addFlashAttribute("snsAction", snsAction);
	    redirect.addFlashAttribute("snsLoginRet", snsLoginRet);
	    redirect.addFlashAttribute("snsLogindUri", snsLogindUri);
	    return REDIRECT_USER_SNS_LOGIN_URL;
	}

	/**
	 * 탈회 회원 메시지
	 * @param membVo
	 * @return
	 */
	public static String getMessageForWithdrawLimit(MemberVO membVo) {
		String mftmsg = "탈회한 회원은 한달 이내 재가입이 제한됩니다.";
		mftmsg +="\n한달 경과 후 다시 진행해주세요.";

		String retMsg = null;
		if(membVo != null && membVo.getWtdrDttm() != null) {
			mftmsg +="\n탈회일자 : %s";

			retMsg = String.format(mftmsg, DateUtil.SDF_DATEDOT.format(membVo.getWtdrDttm()));
		}else {
			retMsg = mftmsg;
		}

		return retMsg;
	}
}

