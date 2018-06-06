package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.ZxingHandler;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.OutTradeNoTypeEnum;
import com.wanhutong.backend.modules.enums.TradeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestPay")
public class BizRequestPayController extends BaseController {

    private static Logger payLogger = LoggerFactory.getLogger("requestPay");

    @Resource
    private BizRequestHeaderService bizRequestHeaderService;

    @Resource
    private BizPayRecordService bizPayRecordService;


    @RequestMapping(value = "genPayQRCode")
    @ResponseBody
    public Map<String,String> genPayQRCode(Double payMoney,Integer reqId,Integer payMethod){

        User user=UserUtils.getUser();
        Map<String,String> reMap =new HashMap<>();
        BizRequestHeader bizRequestHeader= bizRequestHeaderService.get(reqId);
        String postUrl=DsConfig.getAlipayPostUrl();
        if(payMethod==1){
            postUrl=DsConfig.getWxPostUrl();
        }
        payLogger.info("请求URL====="+postUrl);

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

            payLogger.info("微信请求自带参数-----------"+ownCallbackStr);

            map.put("appid", DsConfig.getAppid());
           String aplipayNo= GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE,user.getCompany().getId());
           String wxNo=  GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE,user.getCompany().getId());

            if(payMethod==0){
                map.put("out_trade_no",aplipayNo);
                map.put("amount",payMoney.toString());
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

            payLogger.info("请求参数-------------"+new StringEntity(jsonstr, Charset.forName("UTF-8")));

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

            payLogger.info("支付请求时添加支付记录----------"+bizPayRecord);

            String pathFile = Global.getUserfilesBaseDir()+"/upload/" +photoName ;

            payLogger.info("二维码路径----------"+pathFile);

            httpResponse = httpClient.execute(httpPost);

            result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            Map map1 = (Map) com.alibaba.druid.support.json.JSONUtils.parse(result);


            payLogger.info("返回结果result================="+result);


            String qrCodeUrl="";
            if("1".equalsIgnoreCase(map1.get("status").toString())&& payMethod==0){
                qrCodeUrl=  (String) ((Map)map1.get("data")).get("qr_code");

            }else if("1".equalsIgnoreCase(map1.get("status").toString())&&payMethod==1){
                qrCodeUrl=(String) ((Map)map1.get("data")).get("code_url");

            }
            payLogger.info("二维码地址-------------"+qrCodeUrl);

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
        reMap.put("imgUrl","/upload/" +photoName);
        reMap.put("payNum",bizPayRecord.getPayNum());
        return reMap;
    }

    @RequestMapping(value = "checkCondition")
    @ResponseBody
    public String checkCondition(String payNum){
        BizPayRecord bizPayRecord =new BizPayRecord();
        bizPayRecord.setPayNum(payNum);
        bizPayRecord.setBizStatus(1);
         List<BizPayRecord> list= bizPayRecordService.findList(bizPayRecord);
         if(list.size()==1){
             return "ok";
         }
         return "error";


    }

}
