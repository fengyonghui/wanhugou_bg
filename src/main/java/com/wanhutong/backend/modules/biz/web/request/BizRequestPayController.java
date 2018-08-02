package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.constant.WechatPayTradeType;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.QRCodeKit;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.OutTradeNoTypeEnum;
import com.wanhutong.backend.modules.enums.TradeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.MD5Util;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    public Map<String,String> genPayQRCode(Double payMoney, Integer reqId, Integer payMethod){

        User user=UserUtils.getUser();
        Map<String,String> reMap =new HashMap<>();
        BizRequestHeader bizRequestHeader= bizRequestHeaderService.get(reqId);

        BizPayRecord bizPayRecord =new BizPayRecord();
        String photoName=null;
        try {
           String aplipayNo= GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE,user.getCompany().getId());
           String wxNo=  GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE,user.getCompany().getId());

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


            Map<String, Object> map1 = unifiedOrder(payMoney, reqId, payMethod, WechatPayTradeType.NATIVE.getType(), null);


            String pathFile = Global.getUserfilesBaseDir()+"/upload/" +photoName ;
            payLogger.info("二维码路径----------"+pathFile);



            String qrCodeUrl="";
            if("1".equalsIgnoreCase(map1.get("status").toString())&& payMethod==0){
                qrCodeUrl=  (String) ((Map)map1.get("data")).get("qr_code");

            }else if("1".equalsIgnoreCase(map1.get("status").toString())&&payMethod==1){
                qrCodeUrl=(String) ((Map)map1.get("data")).get("code_url");

            }
            payLogger.info("二维码地址-------------"+qrCodeUrl);

            String basePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");;
            payLogger.info("获取logo图片地址---------"+basePath);
            BufferedImage image = QRCodeKit.createQRCodeWithLogo(qrCodeUrl,260,260, new File(basePath+"/static/images/whtLogo.png"));
            ImageIO.write(image, "png", new File(pathFile));


        } catch (Exception e) {
            e.printStackTrace();
            bizPayRecord.setBizStatus(0);
            bizPayRecordService.save(bizPayRecord);
        }finally {
        }
        reMap.put("imgUrl","/upload/" +photoName);
        reMap.put("payNum",bizPayRecord.getPayNum());
        return reMap;
    }

    @RequestMapping(value = "wechatPay4JSAPI")
    @ResponseBody
    public String wechatPay4JSAPI(Double payMoney, Integer reqId, String openid){
        Map<String, Object> response = unifiedOrder(payMoney, reqId, 1, WechatPayTradeType.JSAPI.getType(), openid);
        Map<String, String> payParamForJSAPI = getPayParamForJSAPI(response);
        return JsonUtil.generateData(payParamForJSAPI, null);

    }

    /**
     * 统一下单
     * @param payMoney
     * @param reqId
     * @param payMethod
     * @return
     */
    private Map<String, Object> unifiedOrder (Double payMoney, Integer reqId, Integer payMethod, String payType, String openid) {
        User user = UserUtils.getUser();
        BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(reqId);
        String postUrl = DsConfig.getAlipayPostUrl();
        if (payMethod == 1) {
            postUrl = DsConfig.getWxPostUrl();
        }
        payLogger.info("请求URL=====" + postUrl);

        BizPayRecord bizPayRecord = new BizPayRecord();
        HashMap<String, Object> map = Maps.newHashMap();
        CloseableHttpClient httpClient = CloseableHttpClientUtil.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(postUrl);
        CloseableHttpResponse httpResponse = null;
        String result = null;
        try {

            HashMap<String, Object> ownParams = Maps.newHashMap();
            ownParams.put("centerId", user.getCompany().getId());
            ownParams.put("payer", user.getId());
            ownParams.put("amount", payMoney);
            ownParams.put("payMethod", payMethod);
            ownParams.put("orderNum", bizRequestHeader.getReqNo());
            ownParams.put("reqId", bizRequestHeader.getId());

            String ownCallbackStr = JSONObject.fromObject(ownParams).toString();

            payLogger.info("微信请求自带参数-----------" + ownCallbackStr);

            map.put("appid", DsConfig.getAppid());
            String aplipayNo = GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE, user.getCompany().getId());
            String wxNo = GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE, user.getCompany().getId());

            if (payMethod == 0) {
                map.put("out_trade_no", aplipayNo);
                map.put("amount", payMoney.toString());
            }
            if (payMethod == 1) {
                BigDecimal bigDecimal = new BigDecimal(payMoney.toString());
                BigDecimal bd = bigDecimal.multiply(new BigDecimal("100"));
                map.put("payType", payType);
                if (StringUtils.isNotBlank(openid)) {
                    map.put("openid", openid);
                }
                map.put("amount", bd.intValue());
                map.put("attach", ownCallbackStr);
                map.put("out_trade_no", wxNo);
            }

            map.put("notifyUrl", DsConfig.getNotifyUrl());
            map.put("body", "备货清单支付");

            String sign = WhgPaySign.creatSign(map, DsConfig.getAppid());
            String ciphertext = WhgPaySign.executeEncryption(sign, DsConfig.getSsecureKey());
            map.put("ciphertext", ciphertext);

            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            String jsonstr = JSONObject.fromObject(map).toString();

            payLogger.info("请求参数-------------" + new StringEntity(jsonstr, Charset.forName("UTF-8")));

            httpPost.setEntity(new StringEntity(jsonstr, Charset.forName("UTF-8")));


            if (payMethod == 0) {
                bizPayRecord.setPayNum(aplipayNo);
                bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getCode());
                bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getMessage());

            } else if (payMethod == 1) {
                bizPayRecord.setPayNum(wxNo);
                bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getCode());
                bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getMessage());
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

            payLogger.info("支付请求时添加支付记录----------" + bizPayRecord);


            httpResponse = httpClient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            payLogger.info("返回结果result=================" + result);

            return (Map) com.alibaba.druid.support.json.JSONUtils.parse(result);
        } catch (Exception e) {
            e.printStackTrace();
            bizPayRecord.setBizStatus(0);
            bizPayRecordService.save(bizPayRecord);
        }
        return null;
    }


    /**
     * 生成JSAPI支付页面需要的数据
     *
     * @param response
     * @return
     */
    private Map<String, String> getPayParamForJSAPI(Map<String, Object> response) {
        if (response == null || response.get("data") == null){
            return Maps.newHashMap();
        }
        Map<String, Object> data = (Map)response.get("data");

        String prepay_id = String.valueOf(data.get("prepay_id"));// 预支付ID

        // 拼接参数,签名
        Map<String, String> paramMap = new TreeMap<>();
        paramMap.put("appId", String.valueOf(data.get("appid")));
        paramMap.put("timeStamp", System.currentTimeMillis() + "");
        paramMap.put("nonceStr", String.valueOf(data.get("nonce_str")));
        paramMap.put("package", "prepay_id=" + prepay_id);
        paramMap.put("signType", "MD5");
        String stringA = jointParam(paramMap);
        String stringSignTemp = stringA + "key=" + DsConfig.getWechatMchSecret();
        String sign = MD5Util.MD5Encode(stringSignTemp, "UTF-8").toUpperCase();
        paramMap.put("paySign", sign);
        return paramMap;
    }

    private String jointParam(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        for (String key : paramMap.keySet()) {
            sb.append(key).append("=").append(paramMap.get(key));
            sb.append("&");
        }
        return sb.toString();
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
