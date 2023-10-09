package cc.bearvalley.badminton.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化的工具类
 */
public class DateUtil {
    private DateUtil() {
    }

    /**
     * 获取Date的日期字符串
     *
     * @param date 日期对象
     * @return 该Date对应的日期字符串
     */
    public static String getDateStr(Date date) {
        return dateFormat.format(date);
    }

    /**
     * 将格式为yyyy-MM-dd的字符串格式化为日期对象
     *
     * @param str 要格式化的字符串
     * @return 格式化后的日期对象，如果错误返回<code>null</code>
     */
    public static Date getDateFromStr(String str) {
        try {
            return dateFormat.parse(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Date的时间字符串
     *
     * @param date 日期对象
     * @return 该Date对应的时间字符串
     */
    public static String getTimeStr(Date date) {
        return timeFormat.format(date);
    }

    /**
     * 获取Date按yyyyMMdd格式的数值
     *
     * @param date 日期对象
     * @return 按按yyyyMMdd格式化后的int值
     */
    public static int getDayInt(Date date) {
        return Integer.parseInt(numberDateFormat.format(date));
    }

    public static String getDateTimeStr(Date date) {
        return dateAndTimeFormat.format(date);
    }

    /**
     * 获取页面显示的活动时间
     *
     * @param startTime 活动的开始时间
     * @param endTime   活动的结束时间
     * @return 页面显示的活动时间
     */
    public static String getPageTimeStr(Date startTime, Date endTime) {
        return chDateFormat.format(startTime) + " " + timeFormat.format(startTime) + "-" + timeFormat.format(endTime);
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat numberDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat chDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static final DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
}
