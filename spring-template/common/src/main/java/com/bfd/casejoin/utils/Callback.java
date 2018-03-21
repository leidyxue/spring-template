package com.bfd.casejoin.utils;

/**
 *通用回调接口类
 * <p>
 *
 * @author : by
 */
public interface Callback<T> {

  /** 
  * <p>方法名称：handle</p>
  * <p>方法描述：业务处理方法</p>
  *<p> 创建时间：2017年9月6日下午3:02:37</p>
  * <p>@param t
  * <p>@return </p>
  ** <p>boolean</p>  
  *
  * @author by
   **/
  boolean handle(T t);
}
