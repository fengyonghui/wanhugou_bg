package com.wanhutong.backend.modules.sys.listener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wanhutong.backend.modules.biz.consumer.OrderPayConsumer;
import com.wanhutong.backend.modules.config.DynamicConfig;
import com.wanhutong.backend.modules.sys.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebContextListener.class);

    /**
     * 定时器
     */
    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                    .setNameFormat(new StringBuilder("ScheduledExecutorService").append("-%1$d").toString()).build());

    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        if (!SystemService.printKeyLoadMessage()) {
            return null;
        }
        /*
         * 项目启动时,运行动态部署
         */
        try {
            scheduledExecutorService.scheduleWithFixedDelay(DynamicConfig.getInstance(), 60, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("scheduledExecutorService error", e);
        }

        /*
         * 订单支付
         */
        try {
            scheduledExecutorService.scheduleWithFixedDelay(OrderPayConsumer.getInstance(), 60, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("OrderPayConsumer execute error", e);
        }

        return super.initWebApplicationContext(servletContext);
    }



    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.warn("contextDestroyed");
        scheduledExecutorService.shutdown();
        super.contextDestroyed(event);
    }
}
