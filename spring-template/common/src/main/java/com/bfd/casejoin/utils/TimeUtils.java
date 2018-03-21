package com.bfd.casejoin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.DateTime;

/**
 * 时间操作工具类
 * <p>
 *
 * @author : 拜力文
 * @date : 2017-7-6
 */
public class TimeUtils {

  private static final Logger logger = LoggerFactory
      .getLogger(TimeUtils.class);

  /**
   * 年月日   yyyy-MM-dd
   */
  public final static String FORMAT_DATE = "yyyy-MM-dd";

  /**
   * 时分  HH:mm
   */
  public final static String FORMAT_TIME = "HH:mm";

  /**
   * 年月日 时分  yyyy-MM-dd HH:mm
   */
  public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
  /**
   * 年月日 时  yyyy-MM-dd HH
   */
  public final static String FORMAT_DATE_HOUR = "yyyy-MM-dd HH";

  /**
   * MM月dd日 HH:mm
   */
  public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";

  /**
   * yyyy-MM-dd HH:mm:ss
   */
  public final static String FORMAT_FULL_DATE_TIME_WITH_SYMBOL = "yyyy-MM-dd HH:mm:ss";

  /**
   * yyyyMMddHHmmss
   */
  public final static String FORMAT_FULL_DATE_TIME_NO_SYMBOL = "yyyyMMddHHmmss";

  /**
   * yyyyMMddHHmmssSSS
   */
  public final static String FORMAT_FULL_DATE_TIME_WITH_MILLS_NO_SYMBOL = "yyyyMMddHHmmssSSS";
  /**
   * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
   */
  public final static String FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  /**
   * yyyy-MM-dd'T'HH:mm:ssXXX
   */
  public final static String FORMAT_ISO_1 = "yyyy-MM-dd'T'HH:mm:ssXXX";

  /**
   * Description：比较俩日期相差秒数
   *
   * @param date1
   * @param date2
   * @return
   * @return int
   * @author name：
   */
  public static int compareDateToSecond(Date date1, Date date2) {
    long days = date1.getTime() - date2.getTime();
    int time = (int) (days / 1000);
    return time;
  }

  /**
   * Description： 比较俩日期相差天数
   *
   * @param date1
   * @param date2
   * @return
   * @return int
   * @author name：
   */
  public static int compareDateToday(Date date1, Date date2) {
    long days = date1.getTime() - date2.getTime();
    int time = (int) (days / (1000 * 60 * 60 * 24));
    return time;
  }

  /**
   * Description：字符串转为日期
   *  string to date
   * @param str
   * @return Date
   * @author name：
   * <pre>
   * 关于参数 format
   * @see #FORMAT_DATE
   * @see #FORMAT_DATE_TIME
   * @see #FORMAT_FULL_DATE_TIME_NO_SYMBOL
   * @see #FORMAT_FULL_DATE_TIME_WITH_MILLS_NO_SYMBOL
   * @see #FORMAT_FULL_DATE_TIME_WITH_SYMBOL
   * @see #FORMAT_MONTH_DAY_TIME
   * @see #FORMAT_TIME
   * </pre>
   */
  public static Date parseToDate(String str, String formats) {

    SimpleDateFormat format = new SimpleDateFormat(formats);
    Date date = null;
    try {
      date = format.parse(str);
    } catch (ParseException e) {
      logger.error("parseToDate 发生错误", e);
    }
    return date;
  }

  /**
   * Description：将长时间格式时间转换为字符串
   *  date to String
   * @param format
   * @param date
   * @return String
   * @author name：
   * <pre>
   * 关于参数 format
   * @see #FORMAT_DATE
   * @see #FORMAT_DATE_TIME
   * @see #FORMAT_FULL_DATE_TIME_NO_SYMBOL
   * @see #FORMAT_FULL_DATE_TIME_WITH_MILLS_NO_SYMBOL
   * @see #FORMAT_FULL_DATE_TIME_WITH_SYMBOL
   * @see #FORMAT_MONTH_DAY_TIME
   * @see #FORMAT_TIME
   * </pre>
   */
  public static String parseToString(String format, Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    String dateString = formatter.format(date);
    return dateString;
  }

  /**
   * Description：
   *   获取当前时间
   * @param format 时间格式
   *   例如：yyyyMMddHHmmss
   * @return String
   * @author name：
   **/
  public static String getTimes(String format) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    String dateString = formatter.format(new Date());
    return dateString;
  }

  /**
   * Description：将长时间格式时间转换为字符串
   *
   * @param format
   * @param date
   * @return
   * @return String
   * @author name：
   */
  public static String dateFormat(String format, Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    String dateString = formatter.format(date);
    return dateString;
  }

  /**
   * <p>方法名称：LastTime</p>
   * <p>方法描述：取指定日期的最后时间</p>
   *<p> 创建时间：2017年2月8日上午10:41:42</p>
   * <p>@param date
   * <p>@return Date</p>
   *
   * @author 拜力文
   **/
  public static Date lastTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.FORMAT_DATE);
    String a = sdf.format(date) + " 23:59:59";
    return parseToDate(a, TimeUtils.FORMAT_FULL_DATE_TIME_WITH_SYMBOL);
  }

  /**
   * <p>方法名称：startTime</p>
   * <p>方法描述：获取指定时间的当天开始时间</p>
   *<p> 创建时间：2017年6月19日下午5:31:07</p>
   * <p>@param date
   * <p>@return Date</p>
   *
   * @author by
   **/
  public static Date startTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.FORMAT_DATE);
    String a = sdf.format(date);
    return parseToDate(a, TimeUtils.FORMAT_DATE);
  }

  /**
   * <p>方法名称：previousHour</p>
   * <p>方法描述：获取指定时间上一个整点小时</p>
   *<p> 创建时间：2017年6月19日下午5:12:46</p>
   * <p>@param date
   * <p>@return Date</p>
   *
   * @author by
   **/
  public static Date previousHour(Date date) {
    Date currentHour = currentHour(date);
    Calendar instance = Calendar.getInstance();
    instance.setTime(currentHour);
    instance.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY) - 1);
    return instance.getTime();
  }

  /**
   * <p>方法名称：currentHour</p>
   * <p>方法描述：获取当前时间的整点时间</p>
   *<p> 创建时间：2017年6月19日下午5:18:32</p>
   * <p>@param date
   * <p>@return Date</p>
   *
   * @author by
   **/
  public static Date currentHour(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.FORMAT_DATE_HOUR);
    String a = sdf.format(date);
    return parseToDate(a, TimeUtils.FORMAT_DATE_HOUR);
  }

  /**
   * <p>方法名称：getSecond</p>
   * <p>方法描述：获取以秒为单位的时间</p>
   *<p> 创建时间：2017年7月5日下午7:16:57</p>
   * <p>@param date
   * <p>@return Long</p>
   *
   * @author by
   **/
  public static Long getSecond(Date date) {
    return date.getTime() / 1000;
  }

  /**
   * 消除日期中的时间部分，将时间置0
   * <p>
   *
   * @param date 输入日期
   * @return 时间置0后的日期
   */
  public static Date eliminateTime(Date date) {
    DateTime dateTime = new DateTime(date);
    DateTime day = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 0, 0);

    return new Date(day.getMillis());
  }

  /**
   * 返回某时间后n天的时间
   * <p>
   *
   * @param current 当前时间
   * @param next 天数
   * @return 后n天的时间
   */
  public static Date nextDays(Date current, int next) {
    DateTime dateTime = new DateTime(current);
    DateTime day = dateTime.plusDays(next);

    return new Date(day.getMillis());
  }

}
