package com.bfd.casejoin.vo;

/**
 * 异常时的返回
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
public class ExceptionResponse {
  /**
   * 返回信息，会做国际化处理
   */
  private String msg;

  /**
   * 异常信息，不做国际化处理
   */
  private String detailInfo;

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getDetailInfo() {
    return detailInfo;
  }

  public void setDetailInfo(String detailInfo) {
    this.detailInfo = detailInfo;
  }
}
