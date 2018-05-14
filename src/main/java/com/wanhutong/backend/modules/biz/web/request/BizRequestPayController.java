package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.mapper.adapters.MapConvertor;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.ZxingHandler;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.pay.dto.RequestQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.pay.dto.ResponseQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.OutTradeNoTypeEnum;
import com.wanhutong.backend.modules.enums.TradeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestPay")
public class BizRequestPayController extends BaseController {
    @Resource
    private BizRequestHeaderService bizRequestHeaderService;

    @Resource
    private BizPayRecordService bizPayRecordService;


    @RequestMapping(value = "genPayQRCode")
    @ResponseBody
    public String genPayQRCode(Double payMoney,Integer reqId,Integer payMethod){

        User user=UserUtils.getUser();
        BizRequestHeader bizRequestHeader= bizRequestHeaderService.get(reqId);
        String postUrl=DsConfig.getAlipayPostUrl();
        if(payMethod==1){
            postUrl=DsConfig.getWxPostUrl();

        }

        BizPayRecord bizPayRecord =new BizPayRecord();
        HashMap<String, Object> map = Maps.newHashMap();
        CloseableHttpClient httpClient= CloseableHttpClientUtil.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(postUrl);
        CloseableHttpResponse httpResponse = null;
        String result=null;
        String photoName=null;
        try {

            HashMap<String, Object> ownParams = Maps.newHashMap();
            ownParams.put("centerId", user.getCompany().getId());
            ownParams.put("payer", user.getId());
            ownParams.put("amount", payMoney);
            ownParams.put("payMethod",payMethod);
            ownParams.put("orderNum",bizRequestHeader.getReqNo());
            ownParams.put("reqId",bizRequestHeader.getId());

            String ownCallbackStr=JSONObject.fromObject(ownParams).toString();

            map.put("appid", DsConfig.getAppid());
           String aplipayNo= GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE,user.getCompany().getId());
           String wxNo=  GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE,user.getCompany().getId());

            if(payMethod==0){
                map.put("out_trade_no",aplipayNo);
                map.put("amount",payMoney);
            }
            if(payMethod==1){
                BigDecimal bigDecimal =new BigDecimal(payMoney.toString());
                BigDecimal bd=  bigDecimal.multiply(new BigDecimal("100"));
                map.put("payType", "NATIVE");
                map.put("amount", bd.intValue());
                map.put("attach", ownCallbackStr);
                map.put("out_trade_no",wxNo );
            }

            map.put("notifyUrl",DsConfig.getNotifyUrl());
            map.put("body","备货清单支付");

            String sign = WhgPaySign.creatSign(map, DsConfig.getAppid());
            String ciphertext = WhgPaySign.executeEncryption(sign, DsConfig.getSsecureKey());
            map.put("ciphertext",ciphertext);

            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            String jsonstr = JSONObject.fromObject(map).toString();

            httpPost.setEntity(new StringEntity(jsonstr, Charset.forName("UTF-8")));



            if(payMethod==0){
                bizPayRecord.setPayNum(aplipayNo);
                bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getCode());
                bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getMessage());
                photoName= user.getCompany().getId()+bizRequestHeader.getReqNo()+aplipayNo+".png";

            }else if(payMethod==1){
                bizPayRecord.setPayNum(wxNo);
                bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getCode());
                bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getMessage());

                photoName= user.getCompany().getId()+bizRequestHeader.getReqNo()+wxNo+".png";
            }

            bizPayRecord.setOrderNum(bizRequestHeader.getReqNo());
            bizPayRecord.setPayMoney(payMoney);
            bizPayRecord.setPayer(user.getId());
            bizPayRecord.setCustomer(user.getCompany());
            bizPayRecord.setRecordType(TradeTypeEnum.REQUEST_PAY_TYPE.getCode());
            bizPayRecord.setRecordTypeName(TradeTypeEnum.REQUEST_PAY_TYPE.getTradeNoType());

            bizPayRecord.setCreateBy(user);
            bizPayRecord.setUpdateBy(user);
            bizPayRecordService.save(bizPayRecord);

            String pathFile = Global.getUserfilesBaseDir()+"/upload/" +photoName ;


            httpResponse = httpClient.execute(httpPost);

            result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            Map map1 = (Map) com.alibaba.druid.support.json.JSONUtils.parse(result);


            logger.info("result========================"+result);


            String qrCodeUrl="";
            if("1".equalsIgnoreCase(map1.get("status").toString())&& payMethod==0){
                qrCodeUrl=  (String) ((Map)map1.get("data")).get("qr_code");

            }else if("1".equalsIgnoreCase(map1.get("status").toString())&&payMethod==1){
                qrCodeUrl=(String) ((Map)map1.get("data")).get("code_url");

            }

            ZxingHandler.encode2(qrCodeUrl,300,300,pathFile);


        } catch (Exception e) {
            e.printStackTrace();
            bizPayRecord.setBizStatus(0);
            bizPayRecordService.save(bizPayRecord);
        }finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "/upload/" +photoName;
    }

}
