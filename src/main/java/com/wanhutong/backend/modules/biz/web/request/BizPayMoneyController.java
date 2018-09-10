package com.wanhutong.backend.modules.biz.web.request;

import com.alibaba.druid.support.json.JSONUtils;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

@Controller
@RequestMapping(value = "/biz/payMoney")
public class BizPayMoneyController extends BaseController {

    private static final Logger PAY_LOGGER = LoggerFactory.getLogger("requestPay");

    @Resource
    private BizRequestHeaderService bizRequestHeaderService;
    @Resource
    private OfficeService officeService;
    @Resource
    private BizPayRecordService bizPayRecordService;
    @Resource
    private SystemService systemService;

    @Resource
    private BizOrderStatusService bizOrderStatusService;

    //0支付宝支付；1微信支付


    @RequestMapping(value = "payMoneyIng")
    public String payMoneyIng(@RequestBody Map<String, Object> map){
        String jsonstr = JSONObject.fromObject(map).toString();
        PAY_LOGGER.info("请求返回参数--------------"+jsonstr);
        BizPayRecord bizPayRecord =new BizPayRecord();
        Integer reqId=0;
        double amount=0.0;
        String params =null;
        String photoName=null;
        if(map.containsKey("attach")) {
             params = (String) map.get("attach");
             PAY_LOGGER.info("微信返回参数--------"+params);
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
            Double receiptAmount = Double.parseDouble(map.get("receipt_amount").toString());
            PAY_LOGGER.info("支付宝返回参数-------------"+receiptAmount);
            amount= Double.parseDouble(map.get("buyer_pay_amount").toString());
            bizPayRecord.setPayMoney(Double.valueOf(amount));
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
              amount =  Double.parseDouble(paramsMap.get("amount").toString());
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
            Integer bizStatus = bizRequestHeader.getBizStatus();
            if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState())) && bizRequestHeader.getTotalDetail().equals(bizRequestHeader.getRecvTotal())){
                bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ALL_PAY.getState());
                PAY_LOGGER.info("更新备货清单状态-------"+ReqHeaderStatusEnum.ALL_PAY.getState());
            }else if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState()))  && bizRequestHeader.getTotalDetail() > bizRequestHeader.getRecvTotal()){
                bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.INITIAL_PAY.getState());
                PAY_LOGGER.info("更新备货清单状态-------"+ReqHeaderStatusEnum.INITIAL_PAY.getState());
            }else if((bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.UNREVIEWED.getState()) || bizRequestHeader.getBizStatus().equals(ReqHeaderStatusEnum.INITIAL_PAY.getState())) && bizRequestHeader.getTotalDetail() < bizRequestHeader.getRecvTotal()){
                bizRequestHeader.setBizStatus(-1);
                PAY_LOGGER.info("更新状态出问题");
            }
            bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
            if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
            }
        }

        String pathFile = Global.getUserfilesBaseDir()+"/upload/" +photoName ;
        File file =new File(pathFile);
        if(file.exists()){
            file.delete();
            PAY_LOGGER.info("删除生成的二维码");
        }

        return "redirect:"+ Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
    }


    @RequestMapping(value = "payNotify")
    @ResponseBody
    public String payNotify(String orderNum, String orderType){
        PAY_LOGGER.warn("[pay notify] orderNum:[{}] orderType[{}]", orderNum, orderType);

        Pair<Boolean, String> result = orderPayHandler(orderNum, orderType);
        if (result == null) {
            return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "系统内部错误!", null);
        }
        if (result.getLeft()) {
            return JsonUtil.generateData(result.getRight(), null);
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), null);
    }

    private Pair<Boolean, String> orderPayHandler(String orderNum, String orderType) {
        // 取订单


        // 取当前支付比例


        // 取当前审核状态


        // 取审核状态payCode

        // 如果有, 更新当前状态
        // 前一状态备注:订单支付比例更新自动通过


        return null;
    }
}
