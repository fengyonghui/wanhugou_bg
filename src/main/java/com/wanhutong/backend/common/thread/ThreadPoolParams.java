package com.wanhutong.backend.common.thread;

public class ThreadPoolParams {
    private String threadPoolName;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueSize;
    private boolean rejectDiscard;

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        if (keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }
        this.keepAliveTime = keepAliveTime;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isRejectDiscard() {
        return rejectDiscard;
    }

    public void setRejectDiscard(boolean rejectDiscard) {
        this.rejectDiscard = rejectDiscard;
    }
}