package com.bfd.casejoin.controller;

import com.bfd.casejoin.common.ReturnMessage;
import com.bfd.casejoin.conf.I18nHolder;
import com.bfd.casejoin.exception.BaseException;
import com.bfd.casejoin.vo.ExceptionResponse;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 异常处理
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
@ControllerAdvice
public class ExceptionHandlerController {
  @Resource
  protected I18nHolder i18nHolder;

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(BaseException.class)
  @ResponseBody
  public ResponseEntity handleUdpException(HttpServletRequest req, BaseException e) {
    LOGGER.error("BaseException", e);
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    exceptionResponse.setMsg(i18nHolder.getI18nString(e.getCode()));
    exceptionResponse.setDetailInfo(e.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(exceptionResponse);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  ResponseEntity handleAllException(HttpServletRequest req, Exception e) {
    LOGGER.error("INTERNAL_ERROR", e);
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    exceptionResponse.setMsg(i18nHolder.getI18nString(ReturnMessage.SystemError));
    exceptionResponse.setDetailInfo(e.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(exceptionResponse);
  }
}
