/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.atmProcessingEngine;

import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.service.AtmEngineService;
import com.jjProj.service.impl.AtmEngineServiceImpl;

/**
 * ATM Engine Worker Class. An ATM Engine will be created per input file to be processed.
 */
public class AtmEngine implements Runnable {

    private AtmEngineConfig atmEngineConfig;
    /**
     * We cannot use @Autowired here in a Runnable as we are calling the new and not Spring !!
     * So we setup the AtmEngineService in the constructor
     */
//    @Autowired
    private AtmEngineService atmEngineService;

    /**
     * Constructor for AtmEngine
     */
    public AtmEngine() {
        this.setAtmEngineService(new AtmEngineServiceImpl());
    }
    /**
     * Constructor for AtmEngine
     * @param AtmEngineConfig - Define the ATM Machine input data file to be processed
     */
    public AtmEngine(AtmEngineConfig atmEngineConfig){
        this.setAtmEngineConfig(new AtmEngineConfig(atmEngineConfig,atmEngineConfig.getAtmEngineFilePathToProcess()));
        this.setAtmEngineService(new AtmEngineServiceImpl());
    }


    /**
     * Run method for this worker thread.
     *
     */
    public void run() {
        processAtmTransactions(this.getAtmEngineConfig());
    }

    /**
     * This method can be used as a runnable method to process a single ATM Machine input data file.
     *
     * @param AtmEngineConfig - Define the ATM Machine input data file to be processed
     * @return boolean - success or failure of processing
     */
    public boolean processAtmTransactions(AtmEngineConfig atmEngineConfig) {
        boolean processedInputFilesuccess = true;
        long threadId = Thread.currentThread().getId();
        try {
            if (atmEngineService.validateInputFile(atmEngineConfig.getAtmEngineFilePathToProcess())){
                if(atmEngineService.createEngineFiles(atmEngineConfig)){
                    System.out.println( "INFO: Starting Thread["+threadId+ "] processing[" + atmEngineConfig.getAtmEngineFilePathToProcess()+"]");
                    if (atmEngineService.processInputFile(atmEngineConfig)){
                    } else{
                        System.out.println( "ERROR: Failed to process [" + atmEngineConfig.getInputFile().getName()+"]");
                        processedInputFilesuccess = false;
                    }
                } else {
                    System.out.println( "ERROR: Failed to create processing file for [" + atmEngineConfig.getInputFile().getName()+"]");
                    processedInputFilesuccess = false;
                }
            } else {
                System.out.println("ERROR: Failed to Validate file["+atmEngineConfig.getAtmEngineFilePathToProcess()+"]");
                processedInputFilesuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            processedInputFilesuccess = false;
        } finally{
            if (!atmEngineService.atmEngineCleanUp(atmEngineConfig)){
                System.out.println( "ERROR: Failed to fully clean up Engine Files for["+atmEngineConfig.getAtmEngineFilePathToProcess()+"]");
            }
            System.out.println( "INFO: Exiting Thread["+threadId+ "] processing[" + atmEngineConfig.getAtmEngineFilePathToProcess()+"]");
        }
        return processedInputFilesuccess;
    }

    public AtmEngineConfig getAtmEngineConfig() {
        return atmEngineConfig;
    }
    public void setAtmEngineConfig(AtmEngineConfig atmEngineConfig) {
        this.atmEngineConfig = atmEngineConfig;
    }
    public AtmEngineService getAtmEngineService() {
        return atmEngineService;
    }
    public void setAtmEngineService(AtmEngineService atmEngineService) {
        this.atmEngineService = atmEngineService;
    }

}
