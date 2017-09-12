/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.atmProcessingEngine;

import org.springframework.core.task.TaskExecutor;
import com.jjProj.datamodel.AtmEngineConfig;

/**
 * ATM Engine Factory Class. An ATM Engine factory will be used to wrap the Spring Thread pool Executor.
 */
public class AtmEngineFactory {

    private TaskExecutor taskExecutor;
    private String atmFactoryName;
    private AtmEngine atmEngine;

    /**
     * Constructor for AtmEngineFactory
     *
     * @param TaskExecutor - the thread pool wired in from Spring
     * @param AtmEngine - the ATM Engine class wired in from Spring
     * @param String - the name of the ATM Machine wired in from properties
     */
    public AtmEngineFactory(TaskExecutor taskExecutor, AtmEngine atmEngine, String atmFactoryName) {
         this.taskExecutor = taskExecutor;
         this.atmEngine = atmEngine;
         this.atmFactoryName = atmFactoryName;
    }
    /**
     * Fire a thread from the thread pool and create a runnable to execute the
     * input file process in the ATM Engine method
     *
     * @param AtmEngineConfig - the config to use in the ATM input file processing
     */
    public void fire(final AtmEngineConfig atmEngineConfig) {
        /*
         * Create a new AtmEngine object and set the Configuration into it that
         * contains the ATM Machine config as well as the specific file that
         * this thread needs to process.
         */
        AtmEngine atmEngine = new AtmEngine(atmEngineConfig);
        taskExecutor.execute(atmEngine);
    }

    public AtmEngine getAtmEngine() {
        return atmEngine;
    }
    public void setAtmEngine(AtmEngine atmEngine) {
        this.atmEngine = atmEngine;
    }
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    public String getAtmFactoryName() {
        return atmFactoryName;
    }
    public void setAtmFactoryName(String atmFactoryName) {
        this.atmFactoryName = atmFactoryName;
    }
    public String getFactoryName(){
        return this.atmFactoryName;
    }
}
