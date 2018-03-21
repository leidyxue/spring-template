package com.bfd.casejoin.conf;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18nHolder {
  @Resource
  protected MessageSource messageSource;

  /**
   * <p>方法名称：getI18nString</p>
   * <p>方法描述：获取国际化属性，前端页面调用时加参数lang=zh_CN en_US等国家标识切换语言</p>
   * <p> 创建时间：2017年6月1日上午10:57:30</p>
   * <p>@param key
   * <p>@return String</p>
   *
   * @author by
   **/
  public String getI18nString(String key) {
    try {
      return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      //没有的字段返回null
      return null;
    }
  }

  /**
   * <p>方法名称：getI18nString</p>
   * <p>方法描述：获取国际化属性，前端页面调用时加参数lang=zh_CN en_US等国家标识切换语言</p>
   * <p> 创建时间：2017年6月1日上午10:57:30</p>
   * <p>@param key
   * <p>@return String</p>
   *
   * @author by
   **/
  public String getI18nString(String key, Object... params) {
    try {
      return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      //没有的字段返回null
      return null;
    }
  }
}
