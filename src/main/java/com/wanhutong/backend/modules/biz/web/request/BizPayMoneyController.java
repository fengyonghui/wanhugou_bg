package com.wanhutong.backend.modules.biz.web.request;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.ZxingHandler;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.pay.dto.RequestQRCodePayDto;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.OutTradeNoTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.TradeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import com.wanhutong.utils.WhgPaySign;
import net.sf.json.JSONObject;
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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/biz/payMoney")
public class BizPayMoneyController extends BaseController {
    @Resource
    private BizRequestHeaderService bizRequestHeaderService;
    @Resource
    private OfficeService officeService;
    @Resource
    private BizPayRecordService bizPayRecordService;
    @Resource
    private SystemService systemService;




    @RequestMapping(value = "payMoneyIng")
    public String payMoneyIng(@RequestBody Map<String, Object> map){
        String jsonstr = JSONObject.fromObject(map).toString();
        logger.info("payMoneyIng======"+jsonstr);
        BizPayRecord bizPayRecord =new BizPayRecord();
        Integer reqId=0;
        Double amount=0.0;
        String params =null;
        String photoName=null;
        if(map.containsKey("attach")) {
             params = (String) map.get("attach");

        }else {
            String payNum= (String) map.get("out_trade_no");
            BizPayRecord record= bizPayRecordService.findBizPayRecord(payNum);
            String outTradeNo = (String) map.get("trade_no");
            bizPayRecord.setPayNum(payNum);
            bizPayRecord.setOutTradeNo(outTradeNo);
            bizPayRecord.setOrderNum(record.getOrderNum());

            bizPayRecord.setPayer(record.getPayer());
            bizPayRecord.setCustomer(record.getCustomer());
            String resultCode= (String) map.get("trade_status");
            Double receiptAmount = Double.parseDouble((String) map.get("receipt_amount"));
         //   amount = (Double) map.get("buyer_pay_amount");
            amount=Double.parseDouble((String) map.get("buyer_pay_amount"));
            bizPayRecord.setPayMoney(amount);
            if("TRADE_SUCCESS".equalsIgnoreCase(resultCode)&& receiptAmount.equals(amount)){
                bizPayRecord.setBizStatus(1);
            }else {
                bizPayRecord.setBizStatus(0);
            }
            String buyerId= (String) map.get("buyer_id");
            String toAccount= (String) map.get("seller_id");
            bizPayRecord.setAccount(buyerId);
            bizPayRecord.setToAccount(toAccount);
            bizPayRecord.setRecordType(TradeTypeEnum.REQUEST_PAY_TYPE.getCode());
            bizPayRecord.setRecordTypeName(TradeTypeEnum.REQUEST_PAY_TYPE.getTradeNoType());
            bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getCode());
            bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_ALIPAY_NO_TYPE.getMessage());
            User user= systemService.getUser(record.getPayer());
            bizPayRecord.setCreateBy(user);
            bizPayRecord.setUpdateBy(user);
            bizPayRecordService.save(bizPayRecord);

            reqId = record.getReqId();
            photoName= user.getCompany().getId()+record.getOrderNum()+payNum+".png";
        }
          if(params!=null) {

              Map paramsMap = (Map) JSONUtils.parse(params);
              Integer centerId = (Integer) paramsMap.get("centerId");
              amount = (Double) paramsMap.get("amount");
              Integer payMethod = (Integer) paramsMap.get("payMethod");
              String orderNum = (String) paramsMap.get("orderNum");
              Integer payer = (Integer) paramsMap.get("payer");
              User user= systemService.getUser(payer);
              reqId = (Integer) paramsMap.get("reqId");

          if(1==payMethod){
            String payNum= (String) map.get("out_trade_no");
            String outTradeNo = (String) map.get("transaction_id");
              bizPayRecord.setPayNum(payNum);
              bizPayRecord.setOutTradeNo(outTradeNo);
              bizPayRecord.setOrderNum(orderNum);
              bizPayRecord.setPayMoney(amount);
              bizPayRecord.setPayer(payer);
              bizPayRecord.setCustomer(officeService.get(centerId));
              String resultCode= (String) map.get("result_code");
              String returnCode= (String) map.get("return_code");
              if("SUCCESS".equalsIgnoreCase(returnCode) && "SUCCESS".equalsIgnoreCase(resultCode)){
                   bizPayRecord.setBizStatus(1);
              }else {
                  bizPayRecord.setBizStatus(0);
              }
              String openid= (String) map.get("openid");
              String toAccount= (String) map.get("mch_id");

              bizPayRecord.setAccount(openid);
              bizPayRecord.setToAccount(toAccount);
              bizPayRecord.setRecordType(TradeTypeEnum.REQUEST_PAY_TYPE.getCode());
              bizPayRecord.setRecordTypeName(TradeTypeEnum.REQUEST_PAY_TYPE.getTradeNoType());
              bizPayRecord.setPayType(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getCode());
              bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.PLATFORM_WX_NO_TYPE.getMessage());
              bizPayRecord.setCreateBy(user);
              bizPayRecord.setUpdateBy(user);
              bizPayRecordService.save(bizPayRecord);

              photoName= user.getCompany().getId()+orderNum+payNum+".png";


          }
        }
        if(bizPayRecord.getBizStatus()==1 && reqId!=0){
            BizRequestHeader bizRequestHeader=bizRequestHeaderService.get(reqId);
            bizRequestHeader.setRecvTotal(bizRequestHeader.getRecvTotal()+amount);
            if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState())) && bizRequestHeader.getTotalDetail().equals(bizRequestHeader.getRecvTotal())){
                bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ALL_PAY.getState());
            }else if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState()))  && bizRequestHeader.getTotalDetail() > bizRequestHeader.getRecvTotal()){
                bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.INITIAL_PAY.getState());
            }else if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState())) && bizRequestHeader.getTotalDetail() < bizRequestHeader.getRecvTotal()){
                bizRequestHeader.setBizStatus(-1);
            }
            bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
        }

        String pathFile = Global.getUserfilesBaseDir()+"/upload/" +photoName ;
        File file =new File(pathFile);
        if(file.exists()){
            file.delete();
        }

        return "redirect:"+ Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
    }
}
