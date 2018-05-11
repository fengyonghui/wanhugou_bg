package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.mapper.adapters.MapConvertor;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.ZxingHandler;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.dto.RequestQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.pay.dto.ResponseQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestPay")
public class BizRequestPayController extends BaseController {
    @Resource
    private BizRequestHeaderService bizRequestHeaderService;


    @RequestMapping(value = "genPayQRCode")
    public void genPayQRCode(Double payMoney,Integer reqId,Integer payMethod){

        User user=UserUtils.getUser();

        BizRequestHeader bizRequestHeader= bizRequestHeaderService.get(reqId);
        String postUrl="http://api.payment.wanhutong.com/payment/alipay/qrPay/";
        //String postUrl="http://dreamerck.ngrok.xiaomiqiu.cn/payment/alipay/qrPay/";
        if(payMethod==1){
            postUrl="http://api.payment.wanhutong.com/payment/wx/appPay";
          //  postUrl="http://dreamerck.ngrok.xiaomiqiu.cn/payment/wx/appPay";
        }
        //String postUrl="http://dreamerck.ngrok.xiaomiqiu.cn/payment/alipay/qrPay/";
        RequestQRCodePayDto requestQRCodePayDto = new RequestQRCodePayDto();
        HashMap<String, Object> map = Maps.newHashMap();
        CloseableHttpClient httpClient= CloseableHttpClientUtil.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(postUrl);
        CloseableHttpResponse httpResponse = null;
        String result=null;
        try {

            HashMap<String, Object> ownParams = Maps.newHashMap();
            ownParams.put("centerId", user.getCompany().getId());
            ownParams.put("payer", user.getId());
            ownParams.put("amount", payMoney.doubleValue());
            ownParams.put("payMethod",payMethod);

            String ownCallbackStr=JSONObject.fromObject(ownParams).toString();

            map.put("appid", "whtFWQ0NXyXHdnKmIS");
            map.put("out_trade_no",UUID.randomUUID().toString().replaceAll("-",""));
            map.put("amount",payMoney.doubleValue());
            map.put("notifyUrl","http://124.204.36.166:88/biz/payMoney/payMoneyIng");
            map.put("body","测试");
            if(payMethod==0){
                map.put("passback_params", ownCallbackStr);
            }
            if(payMethod==1){
                BigDecimal bigDecimal =new BigDecimal(payMoney.toString());
                BigDecimal bd=  bigDecimal.multiply(new BigDecimal("100"));
                map.put("payType", "NATIVE");
                map.put("amount", bd.intValue());
                map.put("attach", ownCallbackStr);
            }

            String sign = WhgPaySign.creatSign(map, "whtFWQ0NXyXHdnKmIS");
            String ciphertext = WhgPaySign.executeEncryption(sign, requestQRCodePayDto.getSecureKey());

            map.put("ciphertext",ciphertext);

            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");


            String jsonstr = JSONObject.fromObject(map).toString();

            StringEntity se = new StringEntity(jsonstr);

            se.setContentType("text/json");

            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8"));

            httpPost.setEntity(se);

            httpResponse = httpClient.execute(httpPost);

            result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            Map map1 = (Map) com.alibaba.druid.support.json.JSONUtils.parse(result);

            logger.info("result========================"+result);


            String qrCodeUrl="";
            if(payMethod==0){
                qrCodeUrl=  (String) ((Map)map1.get("data")).get("qr_code");
            }else if(payMethod==1){
                qrCodeUrl=(String) ((Map)map1.get("data")).get("code_url");
            }

            String imgPath="d://zifubao.png";
            if(payMethod==1){
              imgPath="d://weixin.png";
            }
            ZxingHandler.encode2(qrCodeUrl,300,300,imgPath);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
