package com.bfd.casejoin.holder;

/**
 * 登录用户
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
public class LoginUser {
  private String id;
  private String name;
  private String ip;
  private String module;
  private String method;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
