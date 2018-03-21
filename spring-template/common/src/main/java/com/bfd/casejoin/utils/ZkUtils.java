package com.bfd.casejoin.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * zookeeper工具类
 * <p>
 *
 * @author : by
 */
public class ZkUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ZkUtils.class);

  /**
  * <p>@param address 地址
  * 
  * <p>@param timeout 超时时间
   */
  public ZkUtils(String address, int timeout) {
    try {
      connectZookeeper(address, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param address
   *            地址
   * @param timeout
   *            超时时间
   * @param namespace
   *            命名空间
   * @param group
   *            组
   * @param groupVal
   *            组节点值
   * @param node
   *            注册服务的节点键
   * @param nodeVal
   *            注册服务的节点值
   */
  public ZkUtils(String address, int timeout, String namespace, String group, String groupVal,
      String node, String nodeVal) {
    try {
      connectZookeeper(address, timeout, namespace, group, groupVal, node, nodeVal);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private CuratorFramework zkclient = null;

  public CuratorFramework getZkclient() {
    return zkclient;
  }

  private void setZkclient(CuratorFramework zkclient) {
    this.zkclient = zkclient;
  }

  /** 
  * <p>方法名称：connectZookeeper</p>
  * <p>方法描述：</p>
  *<p> 创建时间：2017年9月5日下午5:38:53</p>
  * <p>@param address
  * <p>@param timeout
  * <p>@throws Exception </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void connectZookeeper(String address, int timeout) throws Exception {
    if (getZkclient() != null) {
      return;
    }
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    builder.connectString(address).connectionTimeoutMs(timeout).sessionTimeoutMs(timeout)
        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 2000));
    setZkclient(builder.build());
    getZkclient().start();
  }

  /**
   * 连接ZK 创建初始
   * 
   * @param address
   *            地址
   * @param timeout
   *            超时时间
   * @param namespace
   *            命名空间
   * @param group
   *            组
   * @param groupVal
   *            组节点值
   * @param node
   *            注册服务的节点键
   * @param nodeVal
   *            注册服务的节点值
   * @throws Exception 
   */
  public void connectZookeeper(String address, int timeout, String namespace, String group,
      String groupVal, String node, String nodeVal) throws Exception {
    if (getZkclient() != null) {
      return;
    }
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    builder.connectString(address).connectionTimeoutMs(timeout).sessionTimeoutMs(timeout)
        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 2000));
    if (!Strings.isNullOrEmpty(namespace)) {
      builder.namespace(namespace);
    }
    setZkclient(builder.build());
    RpcConnectionStateListener listener =
        new RpcConnectionStateListener(group, groupVal, node, nodeVal);
    getZkclient().getConnectionStateListenable().addListener(listener);
    getZkclient().start();
    // 注入
    startRegisterServer(group, groupVal, node, nodeVal);

  }

  private void startRegisterServer(String group, String groupVal, String node, String nodeVal) {
    registerGroup(group, groupVal);
    registerNode(group, node, nodeVal);
  }

  public void registerReplaceNode(String node, String nodeValue) throws Exception {
    createReplaceNode(node, nodeValue, CreateMode.PERSISTENT);// 创建持久的
  }

  public boolean registerGroup(String group, String groupVal) {
    return createNode("/" + group, groupVal, CreateMode.PERSISTENT);// 创建持久的
  }

  public boolean registerNode(String group, String node, String nodeVal) {
    return createNode("/" + group + "/" + node, nodeVal, CreateMode.EPHEMERAL_SEQUENTIAL); // 创建临时的
  }



  /**
   * 创建节点
   * 
   * @param nodeName
   * @param value
   * @param createMode
   * @return
   * @throws Exception
   */
  public void createReplaceNode(String nodeName, String value, CreateMode createMode)
      throws Exception {
    Stat stat = getZkclient().checkExists().forPath(nodeName);
    if (stat == null) {
      String opResult = null;
      // 节点判断值为不为空
      if (Strings.isNullOrEmpty(value)) {
        opResult =
            getZkclient().create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
      } else {
        opResult = getZkclient().create().creatingParentsIfNeeded().withMode(createMode)
            .forPath(nodeName, value.getBytes(Charsets.UTF_8));
      }
    } else {
      if (Strings.isNullOrEmpty(value)) {
        getZkclient().setData().forPath(nodeName);
      } else {
        getZkclient().setData().forPath(nodeName, value.getBytes(Charsets.UTF_8));
      }
    }
  }

  /**
   * 创建节点
   * 
   * @param nodeName
   * @param value
   * @param createMode
   * @return
   * @throws Exception
   */
  public boolean createNode(String nodeName, String value, CreateMode createMode) {
    boolean suc = false;
    if (getZkclient() == null) {
      return suc;
    }
    try {
      Stat stat = getZkclient().checkExists().forPath(nodeName);
      if (stat == null) {
        String opResult = null;
        // 节点判断值为不为空
        if (Strings.isNullOrEmpty(value)) {
          opResult = getZkclient().create().creatingParentsIfNeeded().withMode(createMode)
              .forPath(nodeName);
        } else {
          opResult = getZkclient().create().creatingParentsIfNeeded().withMode(createMode)
              .forPath(nodeName, value.getBytes(Charsets.UTF_8));
        }
        suc = Objects.equal(nodeName, opResult);
      }
    } catch (Exception e) {
      LOG.error("create node fail! path: " + nodeName + "    value: " + value + "  CreateMode: "
          + createMode.name());
      e.printStackTrace();
      return suc;
    }
    return suc;
  }

  public void destory() {
    if (getZkclient() == null) {
      return;
    }
    getZkclient().close();
  }

  /**
   * 删除节点
   * 
   * @param node
   * @return
   */
  public boolean deleteNode(String node) {
    if (getZkclient() == null) {
      return false;
    }
    try {
      Stat stat = getZkclient().checkExists().forPath(node);
      if (stat != null) {
        getZkclient().delete().deletingChildrenIfNeeded().forPath(node);
      }
      return true;
    } catch (Exception e) {
      LOG.error("delete node fail! path: " + node);
      return false;
    }
  }

  /**
   * 获取指定节点下的子节点路径和值
   * @param node
   * @return
   */
  public Map<String, String> getChildrenDetail(String node) {
    if (getZkclient() == null) {
      return null;
    }
    Map<String, String> map = Maps.newHashMap();
    try {
      GetChildrenBuilder childrenBuilder = getZkclient().getChildren();
      List<String> children = childrenBuilder.forPath(node);
      GetDataBuilder dataBuilder = getZkclient().getData();
      if (children != null) {
        for (String child : children) {
          String propPath = ZKPaths.makePath(node, child);
          byte[] forPath = dataBuilder.forPath(propPath);
          if (forPath != null && forPath.length > 0) {
            String value = new String(forPath, Charsets.UTF_8);
            map.put(child, value);
          } else {

            map.put(child, null);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("get node chilren list fail! path:", e);
      return null;
    }
    return map;
  }

  /**
   * 获取指定节点下的子节点路径和值
   * @param node
   * @return
   */
  public String getNodeValue(String node) {
    if (getZkclient() == null) {
      return null;
    }
    try {
      GetDataBuilder dataBuilder = getZkclient().getData();
      byte[] forPath = dataBuilder.forPath(node);
      if (forPath != null && forPath.length > 0) {
        return new String(forPath, Charsets.UTF_8);
      }
    } catch (Exception e) {
      LOG.error("get node chilren list fail! path:", e);
    }
    return null;
  }

  class RpcConnectionStateListener implements ConnectionStateListener {

    private String group;

    @SuppressWarnings("unused")
    private String groupVal;

    private String node;

    private String nodeVal;

    public RpcConnectionStateListener(String group, String groupVal, String node, String nodeVal) {
      this.groupVal = groupVal;
      this.group = group;
      this.node = node;
      this.nodeVal = nodeVal;
    }

    @Override
    public void stateChanged(CuratorFramework cf, ConnectionState state) {
      if (state == ConnectionState.LOST) {
        //重新注册
        while (true) {
          //只需要注册节点，组已经是持久的//
          if (registerNode(group, node, nodeVal)) {
            break;
          }
        }
      }

    }
  }

  //获取本机IP
  public static String getlocalhost(String type) {
    InetAddress addr = null;
    try {
      addr = InetAddress.getLocalHost();
      if ("address".equals(type)) {
        return addr.getHostName().toString();//获得本机名称
      }
      return addr.getHostAddress().toString();//获得本机IP
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return "";
    }
  }


}
