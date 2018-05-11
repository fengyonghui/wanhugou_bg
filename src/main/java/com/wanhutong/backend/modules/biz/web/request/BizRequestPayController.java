package com.wanhutong.backend.modules.biz.web.request;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.dto.RequestQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestPay")
public class BizRequestPayController extends BaseController {
    @Resource
    private BizRequestHeaderService bizRequestHeaderService;


    @RequestMapping(value = "genPayQRCode")
    public void genPayQRCode(Integer payMoney,Integer reqId){

       BizRequestHeader bizRequestHeader= bizRequestHeaderService.get(reqId);

        String postUrl="http://api.payment.wanhutong.com/payment/alipay/qrPay/";

        RequestQRCodePayDto requestQRCodePayDto = new RequestQRCodePayDto();

        requestQRCodePayDto.setAmount(payMoney);

        requestQRCodePayDto.setBody("订单支付");

        requestQRCodePayDto.setOut_trade_no(bizRequestHeader.getReqNo());

        requestQRCodePayDto.setNotifyUrl("http://192.168.1.105:8080/a/biz/request/bizRequestPay/payMoneyIng");

        CloseableHttpClient httpClient= CloseableHttpClientUtil.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(postUrl);




        CloseableHttpResponse httpResponse = null;
        String result=null;
        try {
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");


            String jsonstr = JSONObject.fromObject(requestQRCodePayDto).toString();

            StringEntity se = new StringEntity(jsonstr);

            se.setContentType("text/json");

            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            httpPost.setEntity(se);

            httpResponse = httpClient.execute(httpPost);

            result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            Object obj = com.alibaba.druid.support.json.JSONUtils.parse(result);
            logger.info("result========================"+result);
            logger.info("obj========================"+obj);
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
