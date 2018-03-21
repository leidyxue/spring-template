package com.bfd.casejoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bfd.casejoin.common.Constants;

/**
 * 服务主程序入口类
 * <p>
 *
 * @author : By
 * @date : 2017-5-24
 */
@SpringBootApplication
@EnableWebMvc
@EnableScheduling
@ComponentScan("com.bfd")
public class Application extends SpringBootServletInitializer {
  private final static Logger logger = LoggerFactory.getLogger(Application.class);

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    logger.info("system [" + Constants.SYSTEM_CODE + "] is up");
  }

}
