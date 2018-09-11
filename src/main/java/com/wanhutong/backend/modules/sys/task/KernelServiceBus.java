package com.wanhutong.backend.modules.sys.task;

import com.google.common.util.concurrent.Service;

/**
 * 任务管理
 * 
 * @author Ma.Qiang
 *
 */
public interface KernelServiceBus {
    void awaitStarted();

    void awaitStopped();

    KernelServiceBus start(Service... services);

    KernelServiceBus then(Service... services);

    KernelServiceBus awaitServiceGroupStarted(Service... services);

    class Builder {
        public static Builder newBuilder() {
            return new Builder();
        }

        public KernelServiceBus build() {
            return new KernelServiceBusImpl();
        }
    }
}
