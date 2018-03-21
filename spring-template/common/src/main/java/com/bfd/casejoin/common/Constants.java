package com.bfd.casejoin.common;

import com.bfd.casejoin.utils.ConfigHandler;
import java.net.InetAddress;

/**
 * 系统常量类
 * <p>
 *
 * @author : 拜力文
 * @date : 2017-5-27
 */
public class Constants {

  /**
   * 默认配置文件名称
   */
  public static final String DEFAULT_CONFIG_NAME = "config.properties";
  /**
   * 本系统名称
   */
  public static final String PLATFORM = ConfigHandler.getConfig().get("system.platform");
  /**
   * 本系统编码
   */
  public static final String SYSTEM_CODE = ConfigHandler.getConfig().get("system.code");

  /**
   * 服务地址
   */
  public static final String SERVER_IP = getLocalAddr();

  private static String getLocalAddr() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      return "unknown address";
    }
  }
}
