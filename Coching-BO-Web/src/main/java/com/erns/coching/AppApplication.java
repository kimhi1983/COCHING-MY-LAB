package com.erns.coching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * <p>SpringBoot Application</p>
 *
 * @author Hunwoo Park
 *
 */
@EnableScheduling
@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplicationBuilder()
            .sources(AppApplication.class)
            .listeners(new ApplicationPidFileWriter("./application.pid"))
            .build();

		application.run(args);
	}
}
