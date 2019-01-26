package com.bjpowernode.p2p.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; /**
 * ClassName:DateUtils
 * package:com.bjpowernode.p2p.util
 * Descrption:
 *
 * @Date:2018/7/16 14:52
 * @Author:guoxin
 */
public class DateUtils {

    public static void main(String[] args) throws ParseException {
//        System.out.println(getDateByAddMonths(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-08"),1));
        System.out.println(Calendar.getInstance().get(Calendar.YEAR));
        System.out.println(getDaysByYear(Calendar.getInstance().get(Calendar.YEAR)));
    }


    /**
     * 通过指定日期添加天数返回日期值
     * @param date
     * @param days
     * @return
     */
    public static Date getDateByAddDays(Date date, Integer days) {

        //创建日期处理类对象
        Calendar calendar = Calendar.getInstance();

        //设置日期处理类对象的时间
        calendar.setTime(date);

        //在日期处理类时间上进行添加天数
        calendar.add(Calendar.DATE,days);

        return calendar.getTime();
    }

    /**
     * 根据指定日期添加月份返回日期
     * @param date
     * @param months
     * @return
     */
    public static Date getDateByAddMonths(Date date, Integer months) {

        //创建一个日期处理类对象
        Calendar calendar = Calendar.getInstance();

        //设置日期处理类对象的时间
        calendar.setTime(date);

        //添加指定的月份
        calendar.add(Calendar.MONTH,months);

        return calendar.getTime();
    }


    public static int getDaysByYear(Integer year) {
        //默认为365天
        int days = 365;

        //闰年:能被4整除且不能被100整除或者能被400整除
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            days = 366;
        }

        return days;
    }


    /**
     * 获取两个日期的天数差
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getInstanceBetweenDate(Date startDate, Date endDate) {

        int distance = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));

        int mod = (int) ((endDate.getTime() - startDate.getTime()) % (24 * 60 * 60 * 1000));

        if (mod > 0) {
            distance = distance + 1;
        }

        return distance;

    }

    /**
     * 获取时间戳
     * @return
     */
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}










