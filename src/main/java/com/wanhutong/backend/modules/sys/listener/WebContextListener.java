package com.wanhutong.backend.modules.sys.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wanhutong.backend.modules.config.DynamicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import com.wanhutong.backend.modules.sys.service.SystemService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	protected static Logger LOGGER = LoggerFactory.getLogger(WebContextListener.class);

	/**
	 * 热发布定时器
	 */
	private ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
					.setNameFormat(new StringBuilder("DynamicConfig").append("-%1$d").toString()).build());

	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()){
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

		return super.initWebApplicationContext(servletContext);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOGGER.warn("contextDestroyed");
		scheduledExecutorService.shutdown();
		super.contextDestroyed(event);
	}
}
