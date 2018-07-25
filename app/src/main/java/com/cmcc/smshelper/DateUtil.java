package com.cmcc.smshelper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 *
 * @author qieyixuan
 * @created at 2016年03月28
 */
public final class DateUtil {

    private static DateUtil instance = null;
    public static final String yyyyMMdd = "yyyy-MM-dd";
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMddHHmmssNotSplit = "yyyyMMddHHmmss";
    public static final String yyyyMMddTHHmmss = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String yyyyMMddTHHmmssWithNoSplit = "yyyyMMdd'T'HHmmss";
    public static final String yyyyMMddTHHmmssSSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String yyyyMMddTHHmmssSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static Map<String, DateFormat> formats;//

    private DateUtil() {

        resetFormats();
    }

    public static synchronized DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
        }

        return instance;
    }

    private static void resetFormats() {

        formats = new HashMap<String, DateFormat>();
        formats.put(yyyyMMddHHmm,new SimpleDateFormat(yyyyMMddHHmm));
        formats.put(yyyyMMddTHHmmss, new SimpleDateFormat(yyyyMMddTHHmmss));
        formats.put(yyyyMMddHHmmssNotSplit, new SimpleDateFormat(
                yyyyMMddHHmmssNotSplit));
        formats.put(yyyyMMddTHHmmssWithNoSplit, new SimpleDateFormat(
                yyyyMMddTHHmmssWithNoSplit));
        formats.put(yyyyMMdd, new SimpleDateFormat(yyyyMMdd));
        formats.put(yyyyMMddHHmmss, new SimpleDateFormat(yyyyMMddHHmmss));
        formats.put(yyyyMMddTHHmmssSSSZ, new SimpleDateFormat(
                yyyyMMddTHHmmssSSSZ));
        formats.put(yyyyMMddTHHmmssSSS,new SimpleDateFormat(yyyyMMddTHHmmssSSS));

        formats.put("MM/dd/yyyy HH:mm:ss a", new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss a"));
        formats.put("yyyy-MM-dd HH:mm:ss a", new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss a"));
        formats.put("yyyy-MM-dd'T'HH:mm:ss.SSSz", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSz"));
        formats.put("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        formats.put("yyyy-MM-dd'T'HH:mm:ss'Z'", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'"));
        formats.put("yyyy-MM-dd'T'HH:mm:ssZ", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ"));
        formats.put("yyyy-MM-dd'T'HH:mm:ssz", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssz"));

    }

    public String format(Date date) {

        return format(date, yyyyMMddHHmmss);
    }

    public String format(Date date, String pattern) {

        DateFormat format = formats.get(pattern);
        if (format == null)
            format = new SimpleDateFormat(pattern);

        return format.format(date);
    }
    public static Calendar getCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public String format(long ms) {
        Date date = new Date(ms);
        return format(date);
    }

    public String format(long ms, String pattern) {
        Date date = new Date(ms);
        return format(date, pattern);
    }

    public static Date getDateFromCalendar(Calendar c) {
        return new Date(c.getTimeInMillis());
    }
    public Date parse(String date, String pattern) throws ParseException {
        DateFormat format = formats.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern);
        }
        return format.parse(date);
    }

    public String formatGMT(Date date) {
        return formatGMT(date, DateUtil.yyyyMMddTHHmmssSSSZ);
    }

    public String formatGMT(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    public Date parseGMT(String date) throws ParseException {
        return parseGMT(date, DateUtil.yyyyMMddTHHmmssSSSZ);

    }

    public Date parseGMT(String date, String pattern) throws ParseException {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.parse(date);

    }

    public Date parseSimple(String date) throws ParseException {
        return parse(date, yyyyMMdd);
    }

    public Date parse(String dateString) {

        Iterator<DateFormat> iter = formats.values().iterator();

        while (iter.hasNext()) {

            try {

                return (iter.next()).parse(dateString);

            } catch (ParseException e) {

                // do nothing

            }

        }

        return null;

    }

    public Date parse(String dateStr, boolean isThrowException)
            throws ParseException {

        Date date = parse(dateStr);

        if (date == null && isThrowException) {
            throw new ParseException("Date Format Error:" + dateStr, 0);
        }

        return date;

    }
    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;
    public static final long WEEK = DAY * 7;
    public static final long YEAR = DAY * 365;

    /**
     * 获取当前时间戳
     * @return
     */
    public static Timestamp getCurrentTimestamp() {

        return new Timestamp(System.currentTimeMillis());

    }

    public static Timestamp getTimestampExpiredYear(int offsetYear) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, offsetYear);
        return new Timestamp(now.getTime().getTime());
    }

    public static Timestamp getCurrentTimestampExpiredMonth(int offsetMonth) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, offsetMonth);
        return new Timestamp(now.getTime().getTime());
    }

    public static String getCurrentTimeBefore(int offsetDay) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -offsetDay);
        return DateUtil.getInstance().format(now.getTime().getTime(),
                yyyyMMddTHHmmssSSS);
    }

    public static String getDayBeforeMonth(int month){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, -month);
        return DateUtil.getInstance().format(now.getTime().getTime(),
                yyyyMMdd);
    }


    public static Timestamp getCurrentTimestampExpiredDay(int offsetDay) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, offsetDay);
        return new Timestamp(now.getTime().getTime());
    }

    public static Timestamp getCurrentTimestampExpiredSecond(int offsetSecond) {

        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, offsetSecond);
        return new Timestamp(now.getTime().getTime());
    }

    public static Timestamp getTimestampExpiredDay(Date givenDate, int offsetDay) {

        Calendar date = Calendar.getInstance();
        date.setTime(givenDate);
        date.add(Calendar.DATE, offsetDay);
        return new Timestamp(date.getTime().getTime());
    }

    public static Timestamp getTimestampExpiredMonth(Date givenDate,
                                                     int offsetMonth) {

        Calendar date = Calendar.getInstance();
        date.setTime(givenDate);
        date.add(Calendar.MONTH, offsetMonth);
        return new Timestamp(date.getTime().getTime());
    }

    public static Timestamp getTimestampExpiredSecond(Date givenDate,
                                                      int offsetSecond) {

        Calendar date = Calendar.getInstance();
        date.setTime(givenDate);
        date.add(Calendar.SECOND, offsetSecond);
        return new Timestamp(date.getTime().getTime());
    }

    public static Timestamp getTimestampExpiredHour(Date givenDate,
                                                    int offsetHour) {

        Calendar date = Calendar.getInstance();
        date.setTime(givenDate);
        date.add(Calendar.HOUR, offsetHour);
        return new Timestamp(date.getTime().getTime());
    }

    public static String getCurrentDay() {

        return DateUtil.getInstance().format(new Date(), yyyyMMdd);

    }

    public static String getTomorrow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        return DateUtil.getInstance().format(now.getTime(), "yyyy-MM-dd");
    }

    public static String getNowTime() {
        return DateUtil.getInstance().format(new Date(), yyyyMMddHHmmss);
    }

    public static Date getMonthFirstDay(Date givenDate) throws ParseException {

        Date date = DateUtil.getInstance().parse(
                DateUtil.getInstance().format(givenDate, "yyyy-MM"), "yyyy-MM");

        return date;

    }

    public static Date getMonthLastDay(Date givenDate) throws ParseException {
        Date firstDay = getMonthFirstDay(givenDate);
        Date lastMonthFirstDay = getTimestampExpiredMonth(firstDay, 1);
        Date lastDay = getTimestampExpiredDay(lastMonthFirstDay, -1);
        return lastDay;

    }

    public static String getCommonFormatDate(String date, String format) {
        Date d = new Date();
        try {
            d = DateUtil.getInstance().parse(date, DateUtil.yyyyMMddHHmmss);
        } catch (ParseException e) {
            return "~~~~";
        }
        return DateUtil.getInstance().format(d, format);
    }

    /**
     * 将WS返回的“yyyyMMTddHHmmss”格式日期,转换成“yyyy-MM-dd HH:mm”格式日期字符串
     *
     * @param date
     *            WS返回的“yyyyMMddHHmmss”格式日期
     * @param format
     *            格式化字符串,如：yyyyMMddHHmm,具体参考DateUtil定义类型
     * @return
     */
    public static String getCommonFormatDate2(String date, String format) {
        Date d = new Date();
        if (isFormatWithSSS(date)) {
            try {
                d = DateUtil.getInstance().parse(date,
                        DateUtil.yyyyMMddTHHmmssSSS);
            } catch (ParseException e) {
                d = new Date();
            }
        } else if (isFormatNoSSS(date)) {
            try {
                d = DateUtil.getInstance().parse(date, yyyyMMddTHHmmss);
            } catch (ParseException e) {
                d = new Date();
            }
        }
        return DateUtil.getInstance().format(d, format);
    }

    public static String getCommonFormatDate3(String date, String format) {
        Date d = new Date();
        if (isFormatWithSSS(date)) {
            try {
                d = DateUtil.getInstance().parse(date,
                        DateUtil.yyyyMMddTHHmmssSSS);
            } catch (ParseException e) {
                d = new Date();
            }
        } else if (isFormatNoSSS(date)) {
            try {
                d = DateUtil.getInstance().parse(date, yyyyMMdd);
            } catch (ParseException e) {
                d = new Date();
            }
        }else if (isFormatNoSplit(date)) {
            try {
                d = DateUtil.getInstance().parse(date, yyyyMMddTHHmmssWithNoSplit);
            } catch (ParseException e) {
                d = new Date();
            }
        }
        return DateUtil.getInstance().format(d, format);
    }

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS格式正则表达式
     *
     * @param date
     * @return
     */
    public static boolean isFormatWithSSS(String date) {
        String strPattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{1,3}$";
        Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    /**
     * yyyy-MM-dd'T'HH:mm:ss格式正则表达式
     *
     * @param date
     * @return
     */
    public static boolean isFormatNoSSS(String date) {
        String strPattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";
        Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    /**
     * yyyyMMdd'T'HHmmss格式正则表达式
     *
     * @param date
     * @return
     */
    public static boolean isFormatNoSplit(String date) {
        String strPattern = "^\\d{4}\\d{2}\\d{2}T\\d{2}\\d{2}\\d{2}$";
        Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    public static String getCurrentTime() {

        return DateUtil.getInstance().format(new Date(), DateUtil.yyyyMMddHHmmssNotSplit);

    }

}
