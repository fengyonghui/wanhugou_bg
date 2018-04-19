package com.wanhutong.backend.common.thread;

import com.wanhutong.backend.common.utils.ServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池管理
 */
public class ThreadPoolManager {

	@Autowired
	private static final ThreadPoolParams DEFAULT_THREAD_POOL_PARAMS = (ThreadPoolParams) ServiceHelper
			.getService("defaultThreadPoolParams");

	private static final ThreadPoolExecutor DEFAULT_THREAD_POOL = ThreadPoolFactory.createThreadPool(DEFAULT_THREAD_POOL_PARAMS);


	public static ThreadPoolExecutor getDefaultThreadPool() {
		return DEFAULT_THREAD_POOL;
	}
}
