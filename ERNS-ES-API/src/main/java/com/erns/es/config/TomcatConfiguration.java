package com.erns.es.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>Tomcat Config</p>
 *
 * @author Hunwoo Park
 *
 */
@Configuration
@Slf4j
public class TomcatConfiguration {
    @Value("${tomcat.ajp.protocol}")
    String ajpProtocol;

    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    @Value("${tomcat.jvmRoute:}")
    private String jvmRoute;

    @PostConstruct
    public void setJvmRoute() {
        // embedded tomcat uses this property to set the jvmRoute
    	log.info("Tomcat jvmRoute:{}", jvmRoute);
        System.setProperty("jvmRoute", jvmRoute);
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public TomcatServletWebServerFactory servletContainer() {

    	log.info("Tomcat jvmRoute:{}", jvmRoute);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        if (tomcatAjpEnabled) {
            Connector ajpConnector = new Connector(ajpProtocol);
            ajpConnector.setPort(ajpPort);
            ajpConnector.setSecure(false);
            ajpConnector.setAllowTrace(false);
            ajpConnector.setScheme("http");

            ((AbstractAjpProtocol) ajpConnector.getProtocolHandler()).setSecretRequired(false);

            try{
                ((AbstractAjpProtocol) ajpConnector.getProtocolHandler()).setAddress(InetAddress.getByName("0.0.0.0"));
            }catch(UnknownHostException e){
                e.printStackTrace();
            }

            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        }

        return tomcat;
    }
}
