package com.wanhutong.backend.test;

import com.google.common.collect.Maps;
import com.wanhutong.utils.EnumSignatureProperty;
import com.wanhutong.utils.MD5Util;
import com.wanhutong.utils.MapUtils;
import net.sf.json.JSONObject;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * 数字签名样例
 */
public class WHTPaySignDemo {

    public static void main(String[] args) throws Exception {
        String privateKeyString = "MIIBVwIBADANBgkqhkiG9w0BAQEFAASCAUEwggE9AgEAAkEAt6432jV3Q4jJjdbcGuax6cJBHHDECk9o6ghLTNNVrgNOhNfvzwp82UObEusm9ghVVuVU1Hs2ufC6lPG9LS8RjwIDAQABAkEAmoeXtZ0FmWubJhl9U6SCSYeD/8lfJM1qFnqj3x5tFteCF1x9F3IWGw/0trqQrqixAPSERQgl+mIpNPUEwdbEAQIhAPMUAc4yZp8S/achiKLmmWV+P+K0pA2qoT37SPkQYYZfAiEAwXHiEjc9SNaWtMQlanZHtr9cxFFoaypQXIxxrDvgYtECIQCVPWpiW8Vv0ywS6d7HDUeO7bVxjjDPBzzaHibObAVEhwIhAI4CO3Mh6DSDi+kyIUGRiT3oJ4LbPGuHia0XUQ+eeTAxAiEAogiHRZN7+3Q6+keF3BNT7GmPGi1nMUd41ApriCHnUks=";
        String appid = "whthUX9dMV9M5vheRB";
        HashMap<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("out_trade_no","uhuihu");
        hashMap.put("amount",2300d);
        hashMap.put("custId",22);
        hashMap.put("payer",22);
        hashMap.put("appid",appid);


        String ciphertext = encryption(appid, privateKeyString, hashMap);

        hashMap.put(EnumSignatureProperty.SIGN_CIPHERTEXT.getProperty(), ciphertext);
        JSONObject.fromObject(hashMap);
      //  System.out.println(new Gson().toJson(hashMap));
    }


    public static String encryption(String appid,String privateKeyString,Map<String, Object> hashMap) throws Exception{
        String sign = WHTPaySignDemo.creatSign(hashMap, appid);

        System.out.println("jdk sign："+sign);

        //2.执行签名
        String ciphertext = WHTPaySignDemo.executeEncryption(sign, privateKeyString);
        return ciphertext;
    }


    /**
     *
     * @param sign
     * @param privateKeyString
     * @return
     * @throws Exception
     */
    public static String executeEncryption(String sign, String privateKeyString)throws Exception {
        //2.执行签名
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(sign.getBytes());
        byte[] result = signature.sign();
        String ciphertext = new String(Base64.getEncoder().encode(result));
        System.out.println("jdk rsa ciphertext : " + ciphertext);
        return ciphertext;
    }

    /**
     *
     * @param map
     * @param appid
     * @return
     */
    public static String creatSign(Map<String, Object> map, String appid) {
        SortedMap<String, Object> sortMap = MapUtils.sortMap(map);
        String sign = getSign(sortMap, appid);
        return sign;
    }


    /**
     * 生成签名
     * @param parameters
     * @param appid
     * @return
     */
    public static String getSign(SortedMap<String, Object> parameters, String appid) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
                sb.append(k + "=" + v + "&");
        }
        sb.append("key="+appid);
        return MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
    }


}
