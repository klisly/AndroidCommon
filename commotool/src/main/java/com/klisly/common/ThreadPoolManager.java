/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

    /**
     * The Constant manager.
     */
    private static final ThreadPoolManager MANAGER = new ThreadPoolManager();
    /** The service. */
    private ExecutorService service;
    
    /**
     * Instantiates a new thread pool manager.
     */
    private ThreadPoolManager() {
        int num = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(num * 2);
    }
    
    /**
     * Gets the single instance of ThreadPoolManager.
     *
     * @return single instance of ThreadPoolManager
     */
    public static ThreadPoolManager getInstance() {
        return MANAGER;
    }
    
    /**
     * Adds the task.
     *
     * @param runnable the runnable
     */
    public void addTask(Runnable runnable) {
        service.execute(runnable);
    }
}
