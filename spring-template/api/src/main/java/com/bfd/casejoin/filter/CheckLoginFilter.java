package com.bfd.casejoin.filter;

import com.bfd.casejoin.holder.LoginUser;
import com.bfd.casejoin.holder.UserHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * 登录过滤器，进行登录认证或者权限管理
 * <p>
 *
 * @author : 拜力文
 * @date : 2017-5-27
 */
@WebFilter(urlPatterns = "/*")
@Component
public class CheckLoginFilter implements Filter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void destroy() {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    HttpSession session = request.getSession();
    String requestURI = request.getRequestURI();
    String[] split = requestURI.split("/");
    ArrayList<String> newArrayList = Lists.newArrayList(split);
    List<String> reverse = Lists.reverse(newArrayList);
    LoginUser user = new LoginUser();
    user.setId("1");
    user.setName("admin");
    Enumeration<String> attributeNames = session.getAttributeNames();
  /*  ShiroUser shiroUser = ShiroUserHolder.getUser();
    if (shiroUser != null) {
      user.setId(shiroUser.getId());
      user.setName(shiroUser.getName());

    } else {
      user.setId("1");
      user.setName("test");

    }*/
    logger.info("session id is " + session.getId() + " requestURI is " + requestURI + " ip is "
        + request.getRemoteAddr() + " userId is " + user.getId() + " attrs"
        + JSONObject.toJSONString(attributeNames));
    user.setIp(request.getRemoteAddr());
    user.setMethod("null");
    user.setModule("null");
    if (reverse.size() > 0) {
      user.setMethod(reverse.get(0));
    }
    if (reverse.size() > 1) {
      user.setModule(reverse.get(1));
    }

    UserHolder.setUser(user);
    //TODO 进行登录认证或者权限校验
    filterChain.doFilter(request, response);

  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {

  }

}
