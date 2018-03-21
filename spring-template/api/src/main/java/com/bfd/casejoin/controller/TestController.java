package com.bfd.casejoin.controller;

import com.bfd.casejoin.exception.InvalidParamException;
import java.io.InvalidClassException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

/**
 * 服务测试控制层，不具有具体业务含义
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
@RestController
public class TestController {

  @GetMapping("/test")
  public JSONObject test(String name, String id) {
    JSONObject obj = new JSONObject();
    obj.put("result", "test success");
    return obj;
  }

  @GetMapping("/exception")
  public String exception() {
    throw new InvalidParamException("wrong type");
  }

  @GetMapping("/run")
  public String runexception() {
    throw new NullPointerException("null point");
  }
}
