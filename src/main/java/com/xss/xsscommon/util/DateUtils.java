package com.xss.xsscommon.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具類
 * @author xss
 * @version 1.0.0
 * @date 2020-09-18 18:00
 */
@Slf4j
public class DateUtils {

    public static final String YYYYMMDD_T_HHMMSS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YYYY_MM_dd = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String DATE_MM_dd = "MM-dd";
    public static final String DATE_mm_dd = "MM月dd";

    /**
     * 日期新增年数
     *
     * @param date 時間
     * @param num  年
     */
    public static Date addYear(Date date, int num) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.YEAR, ca.get(Calendar.YEAR) + num);
        return new Date(ca.getTimeInMillis());
    }

    /**
     * 日期新增月数
     *
     * @param date 時間
     * @param num  月
     */
    public static Date addMonth(Date date, int num) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) + num);
        return new Date(ca.getTimeInMillis());
    }

    /**
     * 日期新增天数
     *
     * @param date 時間
     * @param num  天
     */
    public static Date addDay(Date date, int num) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_YEAR, ca.get(Calendar.DAY_OF_YEAR) + num);
        return new Date(ca.getTimeInMillis());
    }

    /**
     * 日期新增小时数
     *
     * @param date 時間
     * @param hour 小時
     */
    public static Date addHour(Date date, int hour) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) + hour);
        Date newDate = ca.getTime();
        String dateTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(newDate);
        return newDate;
    }

    //日期新增分鐘数
    public static Date addMinute(Date date, int minute) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.MINUTE, ca.get(Calendar.MINUTE) + minute);
        Date newDate = ca.getTime();
        String dateTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(newDate);
        return newDate;
    }

    /**
     * 獲取星期几
     */
    public static String getWeekString(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 时间戳转时间
     *
     * @param timeStamp 时间戳
     * @param format    需要转换的时间格式
     */
    public static String currentTimeToDate(Long timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);// 这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp)))); // 时间戳转换成时间/
        return sd;
    }

    /**
     * 比较两个日期大小
     *
     * @return d1大于d2 返回true
     */
    public static boolean compareDate(Date d1, Date d2) {
        return d1.getTime() > d2.getTime();
    }

    private static SimpleDateFormat getSimpleDateFormat(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat;
    }

    /**
     * 转换为格式化字符串
     *
     * @param time   時間
     * @param format 時間格式
     */
    public static String dateFormat(Date time, String format) {
        Date date = new Date(time.getTime());
        return getSimpleDateFormat(format).format(date);
    }

    /**
     * 將字符串時間轉換成Date類型
     *
     * @param dateStr 時間字符串
     * @param format  轉換格式
     */
    public static Date timeStamp2DateTime(String dateStr, String format) throws ParseException {
        SimpleDateFormat sdf = getSimpleDateFormat(format);
        return sdf.parse(dateStr);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数/
     */
    public static int daysBetween(Date smdate, Date bdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            smdate = sdf.parse(sdf.format(smdate));

            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();//
            long between_days = (time2 - time1) / (1000 * 3600 * 24);

            return Integer.parseInt(String.valueOf(between_days));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判斷日期(yyyy/MM/dd,   yyyy-MM-dd)是否合法
     */
    public static boolean isValidDate(String date) {
        String datePattern1 = "\\d{4}/\\d{2}/\\d{2}";
        String datePattern2 = "\\d{4}-\\d{2}-\\d{2}";
        String datePattern3 = "^((\\d{2}(([02468][048])|([13579][26]))"
                + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
                + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
                + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

        if ((date != null)) {
            if (MatcherUtils.matcherStr(datePattern1, date) || MatcherUtils.matcherStr(datePattern2, date)) {
                return MatcherUtils.matcherStr(datePattern3, date);
            } else {
                return false;
            }
        }
        return false;
    }


    /**
     * 判断输入的字符串是否满足时间格式 ： yyyy-MM-dd HH:mm:ss
     *
     * @param time 需要验证的字符串
     * @return 合法返回 true ; 不合法返回false
     */
    public static boolean isTimeLegal(String time) {
        return MatcherUtils.matcherStr(MatcherUtils.TIME, time);
    }

    /**
     * 判断输入的字符串是否满足时间格式 ： yyyy/MM/dd HH:mm:ss
     *
     * @param time 需要验证的字符串
     * @return 合法返回 true ; 不合法返回false
     */
    public static boolean isTime(String time) {
        return MatcherUtils.matcherStr(MatcherUtils.TIME_2, time);
    }

    /**
     * 获取零点时间
     */
    public static String getDateZero(Date date) {
        return dateFormat(date, DATE_YYYY_MM_dd) + " 00:00:00";
    }

    /**
     * 获取零点时间
     */
    public static String getDateEnd(Date date) {
        return dateFormat(date, DATE_YYYY_MM_dd) + " 23:59:59";
    }


}
