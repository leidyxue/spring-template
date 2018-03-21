package com.bfd.casejoin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

/**
 * properties文件读取类
 * <p>
 *
 * @author : by
 */
public class ConfigReader {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
  protected Map<String, String> constantsMap;

  private ConfigReader() {}


  /** 
  * <p>方法名称：getInstance</p>
  * <p>方法描述：获取配置读取类实例</p>
  *<p> 创建时间：2017年9月7日下午4:10:32</p>
  * <p>@param filePath
  * <p>@return </p>
  ** <p>ConfigReader</p>  
  *
  * @author by
   **/
  public static ConfigReader getInstance(String filePath) {
    InputStream in = null;
    Properties prop = new Properties();
    if (StringUtils.isEmpty(filePath) || !filePath.endsWith(".properties")) {
      throw new IllegalArgumentException("only properties to read");
    }
    in = ConfigReader.class.getClassLoader().getResourceAsStream(filePath);

    try {
      if (in == null) {
        in = new FileInputStream(new File(filePath));
      }
      prop.load(in);
      ConfigReader configReader = new ConfigReader();
      configReader.constantsMap = Maps.newHashMap();
      prop.forEach((k, v) -> {
        configReader.constantsMap.put((String) k, (String) v);
      });
      return configReader;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }

  /** 
  * <p>方法名称：getInstance</p>
  * <p>方法描述：获取配置实例， 该实例会将zk上的配置内容覆盖配置文件内容，并根据zk配置改变而实时更新</p>
  *<p> 创建时间：2017年9月7日下午4:23:48</p>
  * <p>@param filePath
  * <p>@param zkAddr            zk地址 如 127.0.0.1:2181,127.0.0.2:2181
  * <p>@param zkConfigParent    zk上配置的根路径 如/zk/config
  * <p>@param zkTimeOut          zk连接超时事件
  * <p>@return </p>
  ** <p>ConfigReader</p>  
  *
  * @author by
   **/
  public static ConfigReader getInstance(String filePath, String zkAddr, String zkConfigParent,
      int zkTimeOut) {

    ConfigReader instance = getInstance(filePath);
    ZkUtils zkUtils = new ZkUtils(zkAddr, zkTimeOut);
    try {
      zkUtils.registerReplaceNode(zkConfigParent, zkConfigParent);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    instance.constantsMap.putAll(zkUtils.getChildrenDetail(zkConfigParent));
    TreeCache treeCache = new TreeCache(zkUtils.getZkclient(), zkConfigParent);
    addListener(treeCache, instance);
    return instance;


  }

  private static void addListener(TreeCache treeCache, ConfigReader instance) {
    TreeCacheListener treeCacheListener = new TreeCacheListener() {

      @Override
      public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        String key = getNode(event);
        if (StringUtils.isEmpty(key)) {
          return;
        }
        String value = getValue(event);
        switch (event.getType()) {
          case NODE_ADDED:
            instance.constantsMap.put(key, value);
            LOG.info(String.format("add config key[%s] value[%s] ", key, value));
            break;
          case NODE_REMOVED:
            break;
          case NODE_UPDATED:
            instance.constantsMap.put(key, value);
            LOG.info(String.format("edit config key[%s] value[%s] ", key, value));
            break;
          default:
            break;

        }
      }

      private String getNode(TreeCacheEvent event) {
        if (event.getData() != null) {

          return ZKPaths.getNodeFromPath(event.getData().getPath());
        }
        return null;
      }

      private String getValue(TreeCacheEvent event) {
        return new String(event.getData().getData(), Charsets.UTF_8);
      }

    };
    treeCache.getListenable().addListener(treeCacheListener);

  }

  public String get(String key) {
    if (constantsMap.containsKey(key)) {
      return trimValue(key);
    } else {
      return "";
    }
  }

  public boolean getBoolean(String key) {
    if (constantsMap.containsKey(key)) {
      return Boolean.valueOf(trimValue(key));
    } else {
      return false;
    }
  }

  private String trimValue(String key) {
    return constantsMap.get(key).trim();
  }

  /**
   * 获取String
   * @param key
   * @param defaultValue
   * @return
   */
  public String getString(String key, String defaultValue) {
    if (constantsMap.containsKey(key)) {
      return trimValue(key);
    } else {
      return defaultValue;
    }
  }

  /**
   * 获取int
   * @param key
   * @param defaultValue
   * @return
   */
  public int getInt(String key, int defaultValue) {
    if (constantsMap.containsKey(key)) {
      return Integer.parseInt((trimValue(key)));
    } else {
      return defaultValue;
    }
  }

  /**
   * 获取float
   * @param key
   * @param defaultValue
   * @return
   */
  public float getFloat(String key, float defaultValue) {
    if (constantsMap.containsKey(key)) {
      return Float.valueOf((trimValue(key)));
    } else {
      return defaultValue;
    }
  }

  /**
   * 获取int
   * @param key
   * @return
   */
  public int getInt(String key) {
    if (constantsMap.containsKey(key)) {
      return Integer.parseInt((trimValue(key)));
    } else {
      return -1;
    }
  }

}
