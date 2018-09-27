package com.wanhutong.backend.modules.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * 日期转换cron表达式
 *
 * @author liangxi
 * @date 2018-09-21 11:12
 **/
public class CronUtils {
    /**
     * 日期转化为cron表达式
     * @param date
     * @return
     */
    public static String getCron(java.util.Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return  CronUtils.fmtDateToStr(date, dateFormat);
    }

    /**
     * cron表达式转为日期
     * @param cron
     * @return
     */
    public static Date getCronToDate(String cron) {
        String dateFormat="ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * Description:格式化日期,String字符串转化为Date
     *
     * @param date
     * @param dtFormat
     *            例如:yyyy-MM-dd HH:mm:ss yyyyMMdd
     * @return
     */
    public static String fmtDateToStr(Date date, String dtFormat) {
        if(date == null)
        {
            return "";
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}

