package com.bfd.casejoin.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 出错处理
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
@RestController
@RequestMapping("")
public class HttpErrorController implements ErrorController {
  private final static String ERROR_PATH = "/error";

  @RequestMapping(value = ERROR_PATH)
  public ResponseEntity error(HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Path not found");
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }
}
