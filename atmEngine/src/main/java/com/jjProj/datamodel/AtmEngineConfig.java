/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.datamodel;

import java.io.File;

/**
 * ATM Engine Config Class. This class extends the ATM Machine config class.
 */
public class AtmEngineConfig extends AtmMachineConfig{

    String atmEngineFilePathToProcess;
    File inputFile;
    File outputFile;
    File ProcessedFile;

    /**
     * Constructor to use for AtmEngineConfig that calls the AtmMachineConfig constructor
     *
     * @param AtmEngineConfig - the config to use in the ATM input file processing
     * @param String - The ATM input file to process
     */
    public AtmEngineConfig(AtmMachineConfig atmMachineConfig, String atmEngineFilePathToProcess) {
        super(atmMachineConfig);
        this.atmEngineFilePathToProcess = atmEngineFilePathToProcess;
    }

    public String getAtmEngineFilePathToProcess() {
        return atmEngineFilePathToProcess;
    }

    public void setAtmEngineFilePathToProcess(String atmEngineFilePathToProcess) {
        this.atmEngineFilePathToProcess = atmEngineFilePathToProcess;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getProcessedFile() {
        return ProcessedFile;
    }

    public void setProcessedFile(File processedFile) {
        ProcessedFile = processedFile;
    }


}
