package com.bfd.casejoin.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * 访问过滤器，进行web请求的过滤
 * <p>
 *
 * @author : 拜力文
 * @date : 2017-5-27
 */
@WebFilter
@Component
public class AccessFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // TODO Auto-generated method stub

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    request.setCharacterEncoding("utf-8");
    HttpServletResponse res = (HttpServletResponse) response;
    res.setHeader("Access-Control-Allow-Origin",
        ((HttpServletRequest) request).getHeader("Origin"));
    res.setHeader("Access-Control-Allow-Methods", "POST, GET,PUT, OPTIONS, DELETE");
    res.setHeader("Access-Control-Max-Age", "3600");
    res.setHeader("Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    res.setCharacterEncoding("utf-8");
    res.setHeader("Access-Control-Allow-Credentials", "true");
    res.setHeader("XDomainRequestAllowed", "1");
    chain.doFilter(request, res);
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

}
