/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.datamodel;

/**
 * ATM Machine config Class.
 */
public class AtmMachineConfig {

    String atmMachineBaseDirectory;
    String singleFilePathForProcessing;
    boolean moveInputAtmTransactionFileToProcessed;

    /**
     * Constructor to use for AtmMachineConfig
     *
     * @param String - ATM Machine base directory
     * @param boolean - Move Input ATM Transaction File to processed directory (default yes)
     * @param String - Single File Path For Processing
     */
    public AtmMachineConfig(String atmMachineBaseDirectory, boolean moveInputAtmTransactionFileToProcessed, String singleFilePathForProcessing){
        this.atmMachineBaseDirectory = atmMachineBaseDirectory;
        this.moveInputAtmTransactionFileToProcessed = moveInputAtmTransactionFileToProcessed;
        this.singleFilePathForProcessing = singleFilePathForProcessing;
    }
    /**
     * Constructor to use for AtmMachineConfig
     *
     * @param AtmEngineConfig - the config to use in the ATM input file processing
     */
    AtmMachineConfig(AtmMachineConfig atmMachineConfig){
        this.atmMachineBaseDirectory = atmMachineConfig.atmMachineBaseDirectory;
        this.moveInputAtmTransactionFileToProcessed = atmMachineConfig.moveInputAtmTransactionFileToProcessed;
        this.singleFilePathForProcessing = atmMachineConfig.singleFilePathForProcessing;
    }

    public boolean isMoveInputAtmTransactionFileToProcessed() {
        return moveInputAtmTransactionFileToProcessed;
    }
    public void setMoveInputAtmTransactionFileToProcessed(
            boolean moveInputAtmTransactionFileToProcessed) {
        this.moveInputAtmTransactionFileToProcessed = moveInputAtmTransactionFileToProcessed;
    }
    public String getAtmMachineBaseDirectory() {
        return atmMachineBaseDirectory;
    }

    public void setAtmMachineBaseDirectory(String atmMachineBaseDirectory) {
        this.atmMachineBaseDirectory = atmMachineBaseDirectory;
    }


    public String getSingleFilePathForProcessing() {
        return singleFilePathForProcessing;
    }

    public void setSingleFilePathForProcessing(String singleFilePathForProcessing) {
        this.singleFilePathForProcessing = singleFilePathForProcessing;
    }


}
