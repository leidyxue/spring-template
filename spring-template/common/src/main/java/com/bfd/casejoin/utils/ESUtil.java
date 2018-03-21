package com.bfd.casejoin.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * es 操作工具类
 * <p>
 *
 * @author : by
 */
public class ESUtil {
  private static final Logger LOG = LoggerFactory.getLogger(ESUtil.class);
  //用static定义全局唯一es链接，多线程同时调用时，es会自动进行并发处理,可以稳定负载1s内500的并发量
  private Client client = null;
  public String index;
  public String type;

  private int scrollSize = 10000;

  public static String HIGH_LIGHT_PREFIX = "<span style=\"color:red\">";
  public static String HIGH_LIGHT_SUFFIX = "</span>";

  /**
   * 使用默认配置创建es客户端
   */
  public ESUtil() {
    String addrs = ConfigHandler.getConfig().get("es.default.addrs");
    String clusterName = ConfigHandler.getConfig().get("es.default.cluster.name");
    index = ConfigHandler.getConfig().get("es.default.index");
    type = ConfigHandler.getConfig().get("es.default.type");
    scrollSize = ConfigHandler.getConfig().getInt("es.scrollSize");
    initClient(addrs, clusterName, index, type);
  }

  public ESUtil(String addrs, String clusterName, String defaultIndex, String defaultType) {
    initClient(addrs, clusterName, defaultIndex, defaultType);
  }

  public Client getClient() {
    return client;
  }

  private Client initClient(String addrs, String clusterName, String defaultIndex,
      String defaultType) {
    if (client == null) {
      this.index = defaultIndex;
      this.type = defaultType;
      client = init(addrs, clusterName);
    }
    return client;
  }

  //创建并返回一个ES客户端，IP和Port从全局配置文件中读取
  private Client init(String addrs, String clusterName) {
    try {
      LOG.info("start to initialize elasticsearch connection to " + clusterName);

      String[] addresss = getTypes(addrs);
      Settings settings = Settings.builder().put("cluster.name", clusterName).build();
      TransportClient transportClient = new PreBuiltTransportClient(settings);
      for (String addr : addresss) {
        String[] split = addr.split(":");
        transportClient.addTransportAddress(new InetSocketTransportAddress(
            InetAddress.getByName(split[0]), Integer.parseInt(split[1])));
      }
      client = transportClient;
    } catch (Exception e) {
      LOG.error("elasticsearch connection exception: ", e);
    }

    LOG.info("initialization of elasticsearch connection completed");
    return client;
  }

  /** 
  * <p>方法名称：queryOneRecord</p>
  * <p>方法描述：根据指定的条件查询一条数据</p>
  *<p> 创建时间：2017年9月5日下午6:20:12</p>
  * <p>@param index
  * <p>@param type
  * <p>@param searchField
  * <p>@param searchValue
  * <p>@return </p>
  ** <p>Map<String,Object></p>  
  *
  * @author by
   **/
  public Map<String, Object> queryOneRecord(String index, String type, String searchField,
      Object searchValue) {
    SearchRequestBuilder searchRequestBuilder =
        new SearchRequestBuilder(client, SearchAction.INSTANCE);
    BoolQueryBuilder query = QueryBuilders.boolQuery();
    query.must(QueryBuilders.termQuery(searchField, searchValue));
    searchRequestBuilder.setQuery(query);
    SearchResponse ret = query(index, type, searchRequestBuilder, 0, 1);
    if (ret != null && ret.getHits().getTotalHits() > 0) {
      return ret.getHits().getHits()[0].getSource();
    }
    return null;

  }

  /** 
  * <p>方法名称：queryOneRecord</p>
  * <p>方法描述：根据指定的条件查询一条数据的某个字段</p>
  *<p> 创建时间：2017年9月5日下午7:05:22</p>
  * <p>@param index
  * <p>@param type
  * <p>@param searchField
  * <p>@param searchValue
  * <p>@param responseField
  * <p>@return </p>
  ** <p>String</p>  
  *
  * @author by
   **/
  public String queryOneRecord(String index, String type, String searchField, Object searchValue,
      String responseField) {
    Map<String, Object> queryOneRecord = queryOneRecord(index, type, searchField, searchValue);
    if (queryOneRecord != null) {
      Object object = queryOneRecord.get(responseField);
      return String.valueOf(object);
    }
    return "";

  }

  /** 
  * <p>方法名称：queryCount</p>
  * <p>方法描述：查询该条件下es总数</p>
  *<p> 创建时间：2017年8月14日下午3:28:00</p>
  * <p>@param index
  * <p>@param type
  * <p>@param searchRequestBuilder
  * <p>@return </p>
  ** <p>Long</p>  
  *
  * @author by
   **/
  public Long queryCount(String index, String type, SearchRequestBuilder searchRequestBuilder) {
    SearchResponse query = query(index, type, searchRequestBuilder, 0, 0);
    if (query != null && query.getHits() != null) {
      return query.getHits().getTotalHits();
    }
    return 0L;
  }

  /**
   * 	根据传入的searchRequestBuilder进行全量查询(searchRequestBuilder中不要放置from/size分页信息)
   *  分页请用含有分页参数的该方法重载方法
   */
  public SearchResponse query(String index, String type,
      SearchRequestBuilder searchRequestBuilder) {
    return query(index, type, searchRequestBuilder, 0, scrollSize);
  }

  /**
   * 根据传入的searchRequestBuilder进行分页查询(不要往searchRequestBuilder中放置分页信息,请使用分页参数)
   * from为起始下标，从0开始
   * size为返回条数
   */
  public SearchResponse query(String index, String type, SearchRequestBuilder searchRequestBuilder,
      int from, int size) {
    SearchResponse response = null;
    try {
      response = searchRequestBuilder.setIndices(index).setTypes(getTypes(type))
          .setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(from).setSize(size).execute()
          .actionGet();
    } catch (Exception e) {
      LOG.error("elasticsearch query exception: ", e);
    }
    return response;
  }

  /**
   * 根据传入的searchRequestBuilder进行滚动查询，
   * 用callback回调方法处理每次返回的数据
   */
  public void queryWithScroll(String index, String type, SearchRequestBuilder searchRequestBuilder,
      Callback<ArrayList<SearchHit>> callback) {
    queryWithScroll(index, type, searchRequestBuilder, 0, Integer.MAX_VALUE, callback);
  }

  /**
   * 根据传入的searchRequestBuilder进行滚动查询，
   * 用callback回调方法处理每次返回的数据,callback需要返回true/false,true代表继续下一次滚动查询,false代表终止滚动
   * from为起始下标,0代表第一条
   * size为限制callback处理的数据条数(即实际返回数据总量),-1表示不限制
   */
  public void queryWithScroll(String index, String type, SearchRequestBuilder searchRequestBuilder,
      long from, long maxNum, Callback<ArrayList<SearchHit>> callback) {
    //记录ES当前返回数据下标,用于限制from
    int currentLineNum = 0;
    //记录已处理条数，用于限制总返回条数
    int handleLinesCount = 0;
    //定义scrollId有效期(每次滚动后有效期不重置，持续衰减，因此有效期时间应大于整个查询所需时间) (待测试验证)
    TimeValue validTime = TimeValue.timeValueMinutes(120);
    //进行初始查询
    String[] types = getTypes(type);

    SearchResponse scrollResponse = searchRequestBuilder.setIndices(index).setTypes(types)
        .setScroll(validTime).setSize(scrollSize) //每次滚动，单个分片返回100条数据,并不是返回总数据条数!
        .execute().actionGet();

    //存储每次滚动返回的数据，通过回调函数进行处理
    ArrayList<SearchHit> oneScrollResponseHits = new ArrayList<SearchHit>();
    loop1: while (true) {
      //遍历数据进行处理
      for (SearchHit hit : scrollResponse.getHits().getHits()) {
        //下标从0开始
        currentLineNum += 1;
        //判断当前返回line是否满足from
        if (currentLineNum < from) {
          handleLinesCount++;
          continue;
        }

        //处理数已达到要求数,不再滚动查询
        if (maxNum >= 0 && handleLinesCount >= maxNum) {
          if (oneScrollResponseHits.size() > 0) {
            callback.handle(oneScrollResponseHits);
          }
          break loop1;
        }

        oneScrollResponseHits.add(hit);
        handleLinesCount++;
      }

      //回调方法处理查询结果
      boolean ifContinue = callback.handle(oneScrollResponseHits);
      if (!ifContinue) {
        break;
      }

      oneScrollResponseHits.clear();

      //获取scrollId
      String scrollId = scrollResponse.getScrollId();
      //进行下一轮查询
      scrollResponse =
          client.prepareSearchScroll(scrollId).setScroll(validTime).execute().actionGet();
      //当查询不再返回结果时停止继续滚动
      if (scrollResponse.getHits().getHits().length == 0) {
        break;
      }
    }
  }

  private String[] getTypes(String type) {
    String[] types = type.split(",");
    return types;
  }

  public void createIndex(String index) {
    client.admin().indices().prepareCreate(index).execute().actionGet();
  }

  /**
   * 动态修改es mapping结构
   * @param fieldMapping
   * @param index
   * @param type
   * @return
   */
  public void alterMappping(String fieldMapping, String index, String type) {
    PutMappingRequest putMappingRequest = new PutMappingRequest(index);
    putMappingRequest.type(type);
    putMappingRequest.source(fieldMapping);
    client.admin().indices().putMapping(putMappingRequest).actionGet();
  }

  /** 
  * <p>方法名称：bulkOper</p>
  * <p>方法描述：批量处理es请求</p>
  *<p> 创建时间：2017年9月5日下午2:43:00</p>
  * <p>@param request
  * <p>@param type
  * <p>@return </p>
  ** <p>boolean</p>  
  *
  * @author by
   **/
  public boolean bulkOper(List<?> request, String type) {
    BulkProcessor bulkProcessor = null;
    final boolean[] flag = new boolean[1];
    boolean flag2 = true;
    flag[0] = true;
    try {
      bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {}

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
          if (response.hasFailures()) {
            flag[0] = false;
          }
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
          flag[0] = false;
          LOG.error(failure.getMessage());
        }
      }).setBulkActions(10000).setFlushInterval(TimeValue.timeValueSeconds(5))
          .setConcurrentRequests(1).build();
      int len = request.size();
      if ("index".equals(type)) {
        for (int i = 0; i < len; i++) {
          bulkProcessor.add((IndexRequest) request.get(i));
        }
      } else if ("delete".equals(type)) {
        for (int i = 0; i < len; i++) {
          bulkProcessor.add((DeleteRequest) request.get(i));
        }
      } else if ("update".equals(type)) {
        for (int i = 0; i < len; i++) {
          bulkProcessor.add((UpdateRequest) request.get(i));
        }
      }

    } catch (Exception e) {
      LOG.error("bulkOper exception: ", e);
    } finally {
      if (bulkProcessor != null) {
        try {
          flag2 = bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
          LOG.error("bulkOper exception: ", e);
          flag2 = false;
        }
      }
    }
    return flag2 && flag[0];
  }



  /** 
  * <p>方法名称：addHighlightedField</p>
  * <p>方法描述：添加需要高亮字段</p>
  *<p> 创建时间：2017年6月14日下午7:43:53</p>
  * <p>@param requestBuilder
  * <p>@param fields void</p>
  *  
  * @author by
   **/
  public void addHighlightedField(SearchRequestBuilder requestBuilder, List<String> fields) {
    HighlightBuilder sb = new HighlightBuilder();

    sb.preTags(HIGH_LIGHT_PREFIX);
    sb.postTags(HIGH_LIGHT_SUFFIX);
    for (String f : fields) {
      sb.field(f);
    }
    requestBuilder.highlighter(sb);
  }

  /** 
  * <p>方法名称：getHighlightContent</p>
  * <p>方法描述：获取指定高亮字段内容</p>
  *<p> 创建时间：2017年6月14日下午7:44:06</p>
  * <p>@param hit
  * <p>@param fields
  * <p>@return String</p>
  *  
  * @author by
   **/
  public static String getHighlightContent(SearchHit hit, String fields) {
    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
    if (highlightFields != null) {
      //从设定的高亮域中取得指定域
      HighlightField briefField = null;
      briefField = highlightFields.get(fields);
      //取得定义的高亮标签
      if (briefField != null) {
        Text[] titleTexts = briefField.fragments();
        //为title串值增加自定义的高亮标签
        StringBuilder name = new StringBuilder("");
        for (Text text : titleTexts) {
          name.append(text);
        }
        return name.toString();
      }

    }
    return null;
  }

  /** 
  * <p>方法名称：getHighlightContent</p>
  * <p>方法描述：获取所有字段的带高亮内容文本集合</p>
  *<p> 创建时间：2017年6月23日下午6:05:43</p>
  * <p>@param hit
  * <p>@return List<String></p>
  *  
  * @author by
   **/
  public static List<String> getHighlightContent(SearchHit hit) {
    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
    ArrayList<String> ret = Lists.newArrayList();
    if (highlightFields != null) {
      highlightFields.forEach((k, v) -> {
        ret.add(getHighlightContent(hit, k));
      });

    }
    return ret;
  }

  /** 
  * <p>方法名称：getHighlightContentMap</p>
  * <p>方法描述：获取带高亮内容的文本集合</p>
  *<p> 创建时间：2017年9月5日下午7:20:23</p>
  * <p>@param hit
  * <p>@return </p>
  ** <p>Map<String,String></p>  
  *
  * @author by
   **/
  public static Map<String, String> getHighlightContentMap(SearchHit hit) {
    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
    HashMap<String, String> retMap = Maps.newHashMap();
    if (highlightFields != null) {
      highlightFields.forEach((k, v) -> {
        retMap.put(k, getHighlightContent(hit, k));
      });

    }
    return retMap;
  }

  /** 
  * <p>方法名称：getHighlightWordsMap</p>
  * <p>方法描述：获取高亮词集合</p>
  *<p> 创建时间：2017年9月5日下午7:20:44</p>
  * <p>@param map
  * <p>@return </p>
  ** <p>Map<String,Set<String>></p>  
  *
  * @author by
   **/
  public static Map<String, Set<String>> getHighlightWordsMap(Map<String, String> map) {
    HashMap<String, Set<String>> retMap = Maps.newHashMap();
    map.forEach((k, v) -> {
      Set<String> ws = getWordsFromHighlightContent(v);
      retMap.put(k, ws);
    });
    return retMap;
  }

  /** 
  * <p>方法名称：getWordsFromHighlightContent</p>
  * <p>方法描述：从带高亮内容的文本中获取高亮词集合</p>
  *<p> 创建时间：2017年9月5日下午7:15:24</p>
  * <p>@param content
  * <p>@return </p>
  ** <p>Set<String></p>  
  *
  * @author by
   **/
  public static Set<String> getWordsFromHighlightContent(String content) {
    Pattern pattern =
        Pattern.compile(".*?" + HIGH_LIGHT_PREFIX + ".*?" + HIGH_LIGHT_SUFFIX + ".*?");

    HashSet<String> ret = Sets.newHashSet();
    if (StringUtils.isEmpty(content)) {
      return ret;
    }
    Matcher matcher = pattern.matcher(content);
    while (matcher.find()) {
      String group = matcher.group();
      int indexOf = group.indexOf(HIGH_LIGHT_PREFIX);
      String a = group.substring(indexOf, group.length());
      int indexOf2 = a.indexOf(HIGH_LIGHT_SUFFIX);
      String b = a.substring(0, indexOf2);
      String c = b.replace(HIGH_LIGHT_PREFIX, "");
      String d = c.replace(HIGH_LIGHT_SUFFIX, "");
      ret.add(d);
    }
    return ret;
  }

  /** 
  * <p>方法名称：getWordsFromHit</p>
  * <p>方法描述：获取指定字段的高亮词集合</p>
  *<p> 创建时间：2017年9月5日下午7:16:08</p>
  * <p>@param hit
  * <p>@param fields
  * <p>@return </p>
  ** <p>Set<String></p>  
  *
  * @author by
   **/
  public static Set<String> getWordsFromHit(SearchHit hit, String fields) {
    String highlightContent = getHighlightContent(hit, fields);
    return getWordsFromHighlightContent(highlightContent);
  }

  /** 
  * <p>方法名称：getWordsFromHit</p>
  * <p>方法描述：获取所有高亮字段的高亮词集合</p>
  *<p> 创建时间：2017年9月5日下午7:16:25</p>
  * <p>@param hit
  * <p>@return </p>
  ** <p>Set<String></p>  
  *
  * @author by
   **/
  public static Set<String> getWordsFromHit(SearchHit hit) {
    HashSet<String> sets = Sets.newHashSet();
    List<String> highlightContent = getHighlightContent(hit);
    highlightContent.forEach(h -> {
      sets.addAll(getWordsFromHighlightContent(h));
    });
    return sets;
  }



  /** 
  * <p>方法名称：saveDoc</p>
  * <p>方法描述：保存文档</p>
  *<p> 创建时间：2017年9月5日下午2:46:15</p>
  * <p>@param index
  * <p>@param type
  * <p>@param id
  * <p>@param doc </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void saveDoc(String index, String type, String id, String doc) {
    client.prepareIndex(index, type, id).setSource(doc).get();
  }

  /** 
  * <p>方法名称：saveDoc</p>
  * <p>方法描述：使用默认库表保存文档</p>
  *<p> 创建时间：2017年9月5日下午2:46:04</p>
  * <p>@param id
  * <p>@param doc </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void saveDoc(String id, String doc) {
    saveDoc(index, type, id, doc);
  }

  /** 
  * <p>方法名称：updateDoc</p>
  * <p>方法描述：更新文档</p>
  *<p> 创建时间：2017年9月5日下午2:45:43</p>
  * <p>@param index
  * <p>@param type
  * <p>@param id
  * <p>@param doc </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void updateDoc(String index, String type, String id, String doc) {
    client.prepareUpdate(index, type, id).setDoc(doc).get();
  }

  /** 
  * <p>方法名称：updateDoc</p>
  * <p>方法描述：使用默认库表更新文档</p>
  *<p> 创建时间：2017年9月5日下午2:45:35</p>
  * <p>@param id
  * <p>@param doc </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void updateDoc(String id, String doc) {
    updateDoc(index, type, id, doc);
  }

  /** 
  * <p>方法名称：findById</p>
  * <p>方法描述：通过主键id查询数据</p>
  *<p> 创建时间：2017年9月5日下午2:45:24</p>
  * <p>@param id
  * <p>@return </p>
  ** <p>Map<String,Object></p>  
  *
  * @author by
   **/
  public Map<String, Object> findById(String id) {
    Map<String, Object> sourceAsMap = client.prepareGet(index, type, id).get().getSourceAsMap();
    return sourceAsMap;
  }

  /** 
  * <p>方法名称：deleteDoc</p>
  * <p>方法描述：删除指定id文档</p>
  *<p> 创建时间：2017年9月5日下午2:45:05</p>
  * <p>@param id </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void deleteDoc(String id) {
    client.prepareDelete(index, type, id).get();
  }

  /** 
  * <p>方法名称：clearCache</p>
  * <p>方法描述：清除es缓存数据，释放内存</p>
  *<p> 创建时间：2017年9月5日下午2:44:30</p>
  * <p>@param index </p>
  ** <p>void</p>  
  *
  * @author by
   **/
  public void clearCache(String index) {
    client.admin().indices().prepareClearCache(index).setFieldDataCache(true).setQueryCache(true)
        .setRequestCache(true).execute().actionGet();
  }

  /** 
  * <p>方法名称：getKeyword</p>
  * <p>方法描述：获取不分词查询字段</p>
  *<p> 创建时间：2017年6月23日上午11:56:52</p>
  * <p>@param key
  * <p>@return String</p>
  *  
  * @author by
   **/
  public String getKeyword(String key) {
    return key + ".keyword";
  }

  public String getWildCardValue(String value) {
    return "*" + value + "*";
  }

  public static void main(String[] args) throws IOException {
    /*System.out.println("---");
    ESUtil u = new ESUtil();
    Client client = u.getClient();
    String index = "changhong_tg1";
    String type = "tag_type";
    //		boolean f =u.alterMappping("{\"properties\":{\"complexTag\":{\"type\":\"nested\",\"properties\":{\"C2000100001\":{\"type\":\"nested\",\"properties\":{\"value\":{\"type\":\"string\",\"index\":\"not_analyzed\"},\"update_date\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}} ,\"second_level_type\": {\"type\":\"string\",\"index\":\"not_analyzed\"}}}}}", index, type);
    String id = "global:accode:D3460032030000041211003C";
    String obj = "{\"complexTag\":[{\"second_level_type\":\"complex\",\"C2000100001\":[{\"value\":\"高富帅\",\"update_date\":\"2015-10-22\"}]}]}";
    //		System.out.println(obj);
    //		u.updateDoc(index, type, id, obj);
    UpdateRequest update1 = new UpdateRequest(index, type,
            "global:accode:D3460032030000041211003C").doc(obj);
    UpdateRequest update2 = new UpdateRequest(index, type,
            "global:accode:D346004069000005061601RA").doc(obj);
    UpdateRequest update3 = new UpdateRequest(index, type,
            "global:accode:D34600320300000409180022").doc(obj);
    UpdateRequest update4 = new UpdateRequest(index, type,
            "global:accode:D3A600316900000408090027").doc(obj);
    List list = new ArrayList();
    list.add(update1);
    list.add(update2);
    list.add(update3);
    list.add(update4);
    u.bulkOper(list, "update");*/

  }

}
