package com.erns.coching.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

//import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

import com.erns.coching.common.asc.ApiInterceptor;
import com.erns.coching.common.excel.CommonExcelView;
import com.erns.coching.common.excel.CommonExcelViewWrap;

/**
 *
 * <p>WebConfig</p>
 *
 * @author Hunwoo Park
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer, WebMvcRegistrations {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.jsp");
	}

  	//정적리소스
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	//캐시 정책 적용
        CacheControl cacheControl = CacheControl
                //.noCache();
                .maxAge(7776000, TimeUnit.SECONDS);

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl);
    }

    //jsonView 설정
	@Bean
	MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}

	@Bean
	CommonExcelView excelView() {
		return new CommonExcelView();
	}

	@Bean
	CommonExcelViewWrap excelViewWrap() {
		return new CommonExcelViewWrap();
	}

    // /**
    //  * Lucy Xss filter 적용
    //  * @return
    //  */
    // @Bean
    // public FilterRegistrationBean<XssEscapeServletFilter> getFilterRegistrationBean(){
    //     FilterRegistrationBean<XssEscapeServletFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new XssEscapeServletFilter());
    //     registrationBean.setOrder(1);
    //     registrationBean.addUrlPatterns("*.do");
    //     return registrationBean;
    // }

    //API
    private static final List<String> API_URL_PATTERNS =
    		Arrays.asList("/api/**");

    private static final List<String> API_URL_EXCLUDE_PATTERNS =
    		Arrays.asList(
                "/api/common/**" 		//메인페이지 jwe로 받아 처리해야함으로 예외
                , "/api/login/**"	    //로그인
            );

    /**
     * 인터셉터 주소 세팅
     */
  	@Override
  	public void addInterceptors(InterceptorRegistry registry) {
  		//API
  		registry.addInterceptor(new ApiInterceptor())
  		.addPathPatterns(API_URL_PATTERNS)
  		.excludePathPatterns(API_URL_EXCLUDE_PATTERNS);
  	}



    //개발용 - Cors 설정
    //TODO: 개발완료 후 삭제필요
  	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:8380", "http://localhost:9728", "http://localhost:9727", "http://localhost:9731")
            .allowCredentials(true)
            .allowedMethods("GET", "POST");

        registry.addMapping("/file/**")
	        .allowedOrigins("http://localhost:8380", "http://localhost:9728", "http://localhost:9727", "http://localhost:9731")
	        .allowCredentials(true)
	        .allowedMethods("GET", "POST");
    }
}

