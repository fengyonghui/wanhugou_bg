package com.wanhutong.backend.modules.biz.consumer;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.ServiceHelper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class OrderPayConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPayConsumer.class);

    private static final String HOST = Global.getConfig("mqtt.host", "tcp://192.168.1.213:1883");

    private static final String TOPIC = "EMQ_order_pay_server";
    private static final String CLIENT_ID = "OrderPayConsumer";
    private static final MqttClient CLIENT = getClient();

    private static class SingletonHolder {
        private static final OrderPayConsumer INSTANCE = new OrderPayConsumer();
    }

    public static OrderPayConsumer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private OrderPayConsumer() {

    }

    private static MqttClient getClient() {
        // 1.设置mqtt连接属性
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        MqttClient client = null;
        try {
            // 2.实例化mqtt客户端
            InetAddress addr = InetAddress.getLocalHost();
            String hostName = addr.getHostName();
            client = new MqttClient(HOST, CLIENT_ID + hostName);
            // 3.连接
            client.connect(options);
            OrderPayCallback orderPayCallback = ServiceHelper.getService("orderPayCallback");
            client.setCallback(orderPayCallback);
        } catch (Exception e) {
            LOGGER.error("OrderPayConsumer client init error", e);
        }
        return client;
    }


    @Override
    public void run() {
        try {
            CLIENT.subscribe(TOPIC);
        } catch (Exception e) {
            LOGGER.error("OrderPayConsumer client subscribe error", e);
        }
    }
}