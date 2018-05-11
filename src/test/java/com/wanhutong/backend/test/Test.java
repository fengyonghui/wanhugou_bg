package com.wanhutong.backend.test;

import com.wanhutong.utils.EnumSignatureProperty;
import net.sf.json.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        String privateKeyString = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAxa4Myt3XjZkjlpEMnX2cy/qMq4QLpQ6e6/XRNwb7Pmc1bNaeM7mtl/6Y9eZh24RQOE8VArhKWzmghLB+HIuWFQIDAQABAkB5JaWpXjOQD94DlHlKu4SwbahwJMiOK1ux+EBznM+0+a4HomW9gaNpkzH7O+bLLUJ0Bt6D0VCBgMapncWwRa2xAiEA+60zEUFakEoVnbdAfMwR19vZ5v9gRIYhNbuuylvGdlMCIQDJE2OFzwNmwwUHfhgghYq0uepqzXkWna+AkY0dpe1k9wIhAItQxvIIUU+KEwkpBaOPe1kYNDjwqKOF9CBAcnNF9khNAiEAgBNpgQGrGPl/apZWp+BnyVXiisT2LAGkmDAdwpS98WECIQD4aJctBaLyMgd22YNaZYx2y8cTmpacG/4BuGI3GOG/EA==";
        String publicKeyString = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMWuDMrd142ZI5aRDJ19nMv6jKuEC6UOnuv10TcG+z5nNWzWnjO5rZf+mPXmYduEUDhPFQK4Sls5oISwfhyLlhUCAwEAAQ==";
        Map<String, Object> map = new HashMap<>();
        map.put("appid", "whtFWQ0NXyXHdnKmIS");
        map.put("out_trade_no","RE00072124018042804");
        map.put("amount",1);
        map.put("notifyUrl","http://192.168.1.105:8080/a/biz/request/bizRequestPay/payMoneyIng");
        map.put("body","test");//        map.put(EnumSignatureProperty.SIGN_TOTAL_FEE.getProperty(), "200");
        String sign = WHTPaySignDemo.creatSign(map, "whtFWQ0NXyXHdnKmIS");
        System.out.println("jdk sign：" + sign);

        //2.执行签名
        String ciphertext = WHTPaySignDemo.executeEncryption(sign, privateKeyString);

        map.put(EnumSignatureProperty.SIGN_CIPHERTEXT.getProperty(), ciphertext);
        System.out.println("发送报文：" + JSONUtils.valueToString(map));
        //3.验证签名
        verifySign((String) map.get(EnumSignatureProperty.SIGN_CIPHERTEXT.getProperty()), sign, publicKeyString);
    }

    private static boolean verifySign(String ciphertext, String sign, String appSecret) throws Exception {
        //3.验证签名
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(appSecret));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(sign.getBytes());
        byte[] bytes = Base64.decodeBase64(ciphertext);
        boolean bool = signature.verify(bytes);
        System.out.println("jdk rsa verify : " + bool);
        return bool;
    }
}
