package com.bfd.casejoin.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 * <p>
 *
 * @author : 江涌
 * @date : 2017-10-16
 */
@RestController
@RequestMapping("/fileUpload")
public class FileUploadController {

  @PostMapping(value = "/path")
  public String fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest req) throws IOException {

    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
    String dateDir = df.format(new Date());// new Date()为获取当前系统时间
    String serviceName = file.getOriginalFilename()
        .substring(file.getOriginalFilename().lastIndexOf("."));
    File tempFile = new File(dateDir + File.separator + serviceName);
    if (!tempFile.getParentFile().exists()) {
      tempFile.getParentFile().mkdirs();
    }
    if (!file.isEmpty()) {
      try {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
        // "d:/"+file.getOriginalFilename() 指定目录
        out.write(file.getBytes());
        out.flush();
        out.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return "上传失败," + e.getMessage();
      } catch (IOException e) {
        e.printStackTrace();
        return "上传失败," + e.getMessage();
      }
      return "上传成功";
    } else {
      return "上传失败，因为文件是空的";
    }
  }
}

