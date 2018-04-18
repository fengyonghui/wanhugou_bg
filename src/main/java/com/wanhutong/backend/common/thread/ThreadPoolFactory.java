package com.wanhutong.backend.common.thread;

import net.sf.ehcache.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolFactory.class);

    public static ThreadPoolExecutor createThreadPool(ThreadPoolParams threadPoolParams) {

        BlockingQueue<Runnable> queue;
        if (threadPoolParams.getQueueSize() == 0) {
            queue = new SynchronousQueue<Runnable>();
        } else {
            queue = new ArrayBlockingQueue<Runnable>(threadPoolParams.getQueueSize());
        }

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadPoolParams.getCorePoolSize(), threadPoolParams.getMaximumPoolSize(), threadPoolParams.getKeepAliveTime(),TimeUnit.SECONDS, queue,
                new NamedThreadFactory(threadPoolParams.getThreadPoolName()));
        if (threadPoolParams.isRejectDiscard()) {
			threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() {
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
					LOGGER.warn("ThreadPoolExecutor rejected,threadPoolName:{} ,Task:{} ,activeCount: {}",
							threadPoolParams.getThreadPoolName(), r.toString(),
							e.getActiveCount());
				}
			});
        }
        return threadPool;
    }
}
