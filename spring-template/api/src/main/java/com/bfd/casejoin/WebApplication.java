package com.bfd.casejoin;

import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.collect.Maps;

/**
 * web 服务 bean 生成类
 * <p>
 *
 * @author : by
 * @date : 2017-5-24
 */
@Configuration
public class WebApplication {

  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize("100MB");
    factory.setMaxRequestSize("120MB");
    return factory.createMultipartConfig();
  }

  
  @Bean
  public MBeanExporter mBeanExporter() {
    MBeanExporter exporter = new MBeanExporter();
    exporter.setAutodetectMode(MBeanExporter.AUTODETECT_NONE);
    return exporter;
  }

  /*  @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        return registrationBean;
    }
  */
  @Bean
  public FilterRegistrationBean druidWebStatFilter() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    WebStatFilter webStatFilter = new WebStatFilter();
    Map<String, String> initParam = Maps.newHashMap();
    initParam.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
    registrationBean.setFilter(webStatFilter);
    registrationBean.setInitParameters(initParam);
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }

  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
  }

  /**
   * 设置超时时间为30分钟
   * @return
   */
  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() {
    return new EmbeddedServletContainerCustomizer() {
      @Override
      public void customize(ConfigurableEmbeddedServletContainer Container) {
        Container.setSessionTimeout(1800);//单位为S
      }
    };
  }

}
