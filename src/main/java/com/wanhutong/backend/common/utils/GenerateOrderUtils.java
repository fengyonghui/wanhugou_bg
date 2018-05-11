package com.wanhutong.backend.common.utils;


import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import org.apache.commons.lang3.ObjectUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * 订单生成工具类
 *
 * @author DreamerCK
 * @date 2017-12-06 22:40
 **/
public class GenerateOrderUtils {

    private final static Integer OFFICE_LEN = 6;
    private final static Integer CENTER_LEN = 3;
    private final static Integer SERIAL_NUMBER_LEN = 2;
    private final static Integer SEND_NUMBER_LEN = 6;

    /**
     * 小于四位数补零操作
     */
    private final static String STR_FORMAT = "0000";
    private final static String SIXTH_FORMAT = "000000";
    private final static DecimalFormat df = new DecimalFormat(STR_FORMAT);
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(SIXTH_FORMAT);

    private static String convertDate(Date date) {
        return new SimpleDateFormat("yyMMdd").format(date);
    }

    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     *
     * @param sourceData 元数据
     * @param length     需补充长度
     * @return 重组后的数据
     */
    @SuppressWarnings("all")
    public static String frontCompWithZore(Integer sourceData, Integer length) {
        String formatLength = new StringBuilder().append("%0").append(length).append("d").toString();
        return String.format(formatLength, sourceData);

    }

    /**
     * 生成订单号
     *
     * @param orderType    订单类型
     * @param centerId     采购中心ID
     * @param officeId     客户ID
     * @param serialNumber 客户订单序号
     * @return 订单号
     */
    @SuppressWarnings("all")
    public static String getOrderNum(OrderTypeEnum  orderType, Integer centerId, Integer officeId, Integer serialNumber) {
        if (!ObjectUtils.allNotNull(orderType, centerId, officeId, serialNumber)
                || officeId < 0 || centerId < 0 || serialNumber <= 0) {
            return null;
        }
        String repairOfficeId = frontCompWithZore(officeId, OFFICE_LEN);
        String repairCenterId = frontCompWithZore(centerId, CENTER_LEN);
        String repairSerialNumber = frontCompWithZore(serialNumber, SERIAL_NUMBER_LEN);
        StringBuilder orderBuilder = new StringBuilder().append(orderType.name()).append(repairOfficeId)
                .append(repairCenterId).append(convertDate(new Date())).append(repairSerialNumber);
        return orderBuilder.toString();
    }

    /**
     * 生成供货单号
     *
     * @param orderType    订单类型
     * @param centerId     采购中心ID
     * @param officeId     客户ID
     * @param serialNumber 客户订单序号
     * @return 供货单号
     */

    public static String getSendNumber(OrderTypeEnum  orderType, Integer centerId, Integer officeId, Integer serialNumber) {
        if (!ObjectUtils.allNotNull(orderType, centerId, officeId, serialNumber)
                || officeId < 0 || centerId < 0 || serialNumber <= 0) {
            return null;
        }
        String repairOfficeId = frontCompWithZore(officeId, OFFICE_LEN);
        String repairCenterId = frontCompWithZore(centerId, CENTER_LEN);
        String repairSerialNumber = frontCompWithZore(serialNumber, SEND_NUMBER_LEN);
        StringBuilder orderBuilder = new StringBuilder().append(orderType.name()).append(repairOfficeId)
                .append(repairCenterId).append(convertDate(new Date())).append(repairSerialNumber);
        return orderBuilder.toString();
    }


    /**
     * 生成交易流水号
     *
     * @param tradeNoTypeEnum 交易流水号类型
     * @param officeId        客户ID
     * @return 交易流水号
     */
    public static String getTradeNum(OutTradeNoTypeEnum tradeNoTypeEnum, Integer officeId) {
        String repairOfficeId;
        if (officeId != null && officeId > 0) {
            repairOfficeId = officeId.toString();
            if (sizeOfInt(officeId) < DIGIT) {
                repairOfficeId = df.format(officeId);
            } else if (sizeOfInt(officeId) > DIGIT) {
                repairOfficeId = repairOfficeId.substring(repairOfficeId.length() - 4, repairOfficeId.length());
            }
            return tradeNoTypeEnum.getTradeNoType() + System.currentTimeMillis() / 1000L + repairOfficeId + getRandomNum();
        }
        return null;
    }

    public static synchronized String getOrderTradeNum(OrderTypeEnum tradeNoTypeEnum, Integer officeId) {
        String repairOfficeId;
        if (officeId != null && officeId > 0) {
            repairOfficeId = officeId.toString();
            if (sizeOfInt(officeId) < DIGIT) {
                repairOfficeId = DECIMAL_FORMAT.format(officeId);
            } else if (sizeOfInt(officeId) > DIGIT) {
                repairOfficeId = repairOfficeId.substring(repairOfficeId.length() - 4, repairOfficeId.length());
            }
            // 取系统当前时间作为订单号变量前半部分，精确到毫秒
            String newDate =new SimpleDateFormat("yyMMdd").format(new Date());
            return tradeNoTypeEnum.name() + newDate + repairOfficeId + getRandomNum3();
        }
        return null;
    }

    /**
     * 判断数字位数的容器
     */
    private final static int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};


    /**
     * 返回输入数字的位数
     */
    private static int sizeOfInt(int x) {
        for (int i = 0; ; i++) {
            if (x <= SIZE_TABLE[i]) {
                return i + 1;
            }

        }
    }

    public static void main(String[] args) {
        String orderNum = getOrderNum(OrderTypeEnum.SO, 44, 26, 3);
        System.out.println(orderNum);
    }

}
