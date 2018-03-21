package com.bfd.casejoin.holder;

/**
 * 登录用户容器
 * <p>
 *   
 * @author : 江涌
 * @date :2017-10-16
 */
public class UserHolder {
  private static ThreadLocal<LoginUser> users = new ThreadLocal<LoginUser>();

  public static void setUser(LoginUser user) {
    users.set(user);
  }

  public static LoginUser getUser() {
    LoginUser t = new LoginUser();
    t.setId("1");
    t.setName("Administrator");

    LoginUser tUser = users.get();
    t = tUser == null ? t : tUser;
    return t;
  }

  public static String getUserName() {
    LoginUser user = getUser();

    return user == null ? null : user.getName();
  }

  public static String getUserId() {
    LoginUser user = getUser();
    if (user == null) {
      return null;
    }
    return user.getId() + "";
  }

  public static void clear() {
    users.remove();
  }
}
