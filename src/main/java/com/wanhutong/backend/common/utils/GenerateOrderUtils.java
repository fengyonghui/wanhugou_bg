package com.wanhutong.backend.common.utils;


import com.wanhutong.backend.modules.enums.OrderTypeEnum;

import java.text.DecimalFormat;


public class GenerateOrderUtils {

    private final static Integer DIGIT = 4;       //对比数字位数标记
    private final static String STR_FORMAT = "0000";  //小于四位数补零操作
    private final static DecimalFormat df = new DecimalFormat(STR_FORMAT);
//    private static long nowTime = System.currentTimeMillis()/1000L;  //系统当前时间

    //判断数字位数的容器
    private final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };
    //返回输入数字的位数
    private static int sizeOfInt(int x) {
        for (int i = 0;; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    //随机生成四位随机数
    private static String getRandomNum(){
        Integer mark = (int)(Math.random()*10000);
        if(sizeOfInt(mark) < DIGIT){
            return df.format(mark);
        }
        return mark.toString();
    }

    /**
     *  生成订单
     * @param orderType 订单类型
     * @param officeId 客户ID
     * @return 订单号
     */
    public static String getOrderNum(OrderTypeEnum orderType, Integer officeId){
        String repairOfficeId;
        if(officeId != null && officeId > 0){
            repairOfficeId = officeId.toString();
            if(sizeOfInt(officeId) < DIGIT){
                repairOfficeId = df.format(officeId);
            }else if(sizeOfInt(officeId) > DIGIT){
                repairOfficeId = repairOfficeId.substring(repairOfficeId.length()-4,repairOfficeId.length());
            }
            return orderType.name() + System.currentTimeMillis()/1000L+ repairOfficeId+getRandomNum();
        }
        return null;
    }



    public static void main(String[] args) {
        String so = getOrderNum(OrderTypeEnum.SO, 1);
        System.out.println(so);
        System.out.println(so.length());

    }

}
