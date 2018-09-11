package com.wanhutong.backend.modules.sys.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务管理
 * 
 * @author Ma.Qiang
 *
 */
public class KernelServiceBusImpl implements KernelServiceBus {
    private static final Logger logger = LoggerFactory.getLogger(KernelServiceBusImpl.class);
    private final ServiceManager.Listener listener = new ServiceManager.Listener() {
        @Override
        public void failure(Service service) {
            if (service instanceof KernelService) {
                logger.error("KernelService [{}] start failure, system will be exit 1.", service.getClass());
                System.exit(1);
            } else {
                logger.error("Service [{}] start failure.", service.getClass());
            }
        }
    };
    private final List<ServiceManager> serviceCluster = new ArrayList<>();

    @Override
    public void awaitStarted() {
        if (!serviceCluster.isEmpty()) {
            for (final ServiceManager serviceManager : serviceCluster) {
                awaitStartedServiceManager(serviceManager);
            }
        }
    }

    @Override
    public void awaitStopped() {
        if (!serviceCluster.isEmpty()) {
            for (int i = serviceCluster.size() - 1; i > -1; i--) {
                final ServiceManager serviceManager = serviceCluster.get(i);
                if (logger.isInfoEnabled()) {
                    logger.info("===[Shutdown]=== {}", serviceManager.toString());
                }
                serviceManager.stopAsync();
                serviceManager.awaitStopped();
                if (logger.isInfoEnabled()) {
                    logger.info("===[Shutdown]=== {}", serviceManager.toString());
                }
            }
            serviceCluster.clear();
        }
    }

    @Override
    public KernelServiceBus start(final Service... services) {
        addServiceGroup(false, services);
        return this;
    }

    @Override
    public KernelServiceBus then(Service... services) {
        addServiceGroup(false, services);
        return this;
    }

    @Override
    public KernelServiceBus awaitServiceGroupStarted(final Service... services) {
        addServiceGroup(true, services);
        return this;
    }

    private void addServiceGroup(final boolean awaitStarted, final Service... services) {
        final ServiceManager serviceManager = new ServiceManager(ImmutableList.copyOf(services));
        serviceManager.addListener(listener);
        serviceCluster.add(serviceManager);
        if (awaitStarted) {
            awaitStartedServiceManager(serviceManager);
        }
    }

    private void awaitStartedServiceManager(final ServiceManager serviceManager) {
        if (!serviceManager.isHealthy()) {
            if (logger.isInfoEnabled()) {
                logger.info("===[Startup]=== {}", serviceManager.toString());
            }
            serviceManager.startAsync();
            serviceManager.awaitHealthy();
            if (logger.isInfoEnabled()) {
                logger.info("===[Startup]=== {}", serviceManager.toString());
            }
        }
    }
}
