package com.wanhutong.backend.modules.biz.consumer;

import com.alibaba.fastjson.JSONObject;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.ServiceHelper;
import com.wanhutong.backend.modules.biz.entity.order.OrderPayConsumerEntity;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderPayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;

@Repository("orderPayCallback")
public class OrderPayCallback implements MqttCallback {

    @Autowired
    private BizOrderPayService bizOrderHeaderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPayCallback.class);

    public OrderPayCallback() {
//        bizOrderHeaderService = ServiceHelper.getService("bizOrderHeaderService");
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        LOGGER.error("OrderPayCallback connection lost, time:{}", new Date());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.warn("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        String messageDetail = new String(message.getPayload());
        LOGGER.info("OrderPayCallback get message : [{}]",messageDetail);
        if (StringUtils.isBlank(messageDetail)) {
            return;
        }

        try {
            OrderPayConsumerEntity entity = JsonUtil.parse(messageDetail, OrderPayConsumerEntity.class);
            String orderNum = entity.getContent().getOrderNum();
            String orderType = entity.getContent().getOrderType();
            Pair<Boolean, String> result = bizOrderHeaderService.orderPayHandler(orderNum, orderType);
            if (result != null && result.getLeft()) {
                LOGGER.warn("OrderPayCallback orderPayHandler SUCCESS : [{}]", result.getRight());
                return;
            }
            LOGGER.warn("OrderPayCallback orderPayHandler FAILED :[{}]", result == null ? StringUtils.EMPTY : result.getRight());
        }catch (Exception e) {
            LOGGER.error("OrderPayCallback orderPayHandler", e);
        }
    }
}
