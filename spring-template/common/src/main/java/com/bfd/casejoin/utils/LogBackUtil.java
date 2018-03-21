package com.bfd.casejoin.utils;

import com.bfd.casejoin.common.Constants;
import com.bfd.casejoin.holder.LoginUser;
import com.bfd.casejoin.holder.UserHolder;
import java.text.MessageFormat;

/**
 * LogBack 审计日志工具类
 * <p>
 *
 * @author : 江涌
 * @date : 2017-6-26
 */
public class LogBackUtil {

  // 标记是否需要日志审计
  private static boolean isAudit;

  static {
    String str = ConfigHandler.getConfig().getString("log.audit", "true");
    isAudit = Boolean.parseBoolean(str);
  }

  /**
   * 按审计的格式格式化日志
   * <p>
   *
   * @param mess 日志消息
   * @return 带审计格式的日志消息
   */
  public static String getAuditMessage(String mess) {
    if (!isAudit) {
      return mess;
    }

    LoginUser user = UserHolder.getUser();
    return MessageFormat.format("[{0}][{1}][{2}][{3}][{4}][{5}]{6}", user.getId(),
        Constants.PLATFORM, user.getModule(), user.getMethod(), user.getIp(),
        Constants.SERVER_IP, mess);
  }
}
