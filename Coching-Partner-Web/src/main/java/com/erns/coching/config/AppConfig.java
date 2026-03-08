package com.erns.coching.config;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AppConfig {
    
    @Value("${ga.key}")
    private String gaKey;

    private final ServletContext servletContext;

    public AppConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @PostConstruct
    public void init() {
        servletContext.setAttribute("gaKey", gaKey);
    }
}