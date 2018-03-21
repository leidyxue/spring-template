package com.bfd.casejoin.utils;

import com.bfd.casejoin.common.Constants;

public class ConfigHandler {

  private static ConfigReader cr;

  public static ConfigReader getConfig() {
    if (cr != null) {
      return cr;
    }
    synchronized (ConfigHandler.class) {
      if (cr != null) {
        return cr;
      }
      cr = ConfigReader.getInstance(Constants.DEFAULT_CONFIG_NAME);
    }
    return cr;
  }
}
