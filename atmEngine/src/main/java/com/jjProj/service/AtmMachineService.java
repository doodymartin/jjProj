/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.service;

import java.io.FilenameFilter;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.jjProj.atmProcessingEngine.AtmEngineFactory;
import com.jjProj.common.AtmDirectoryActionType;
import com.jjProj.datamodel.AtmMachineConfig;

/**
 * ATM Machine interface Class.
 */
public interface AtmMachineService {

    public boolean atmMachineInstance(String[] args);
    public boolean fireThreadPerInputFile(AtmEngineFactory atmEngineFactory, AtmMachineConfig atmMachineConfig);
    public void exitAtmMachineInstance(String message);
    public boolean atmDirectoryMaintenance(ArrayList<String> atmDirectoryList, AtmDirectoryActionType action);
    public ArrayList<String> createAtmDirectoryList(String currentDirectoryLocation);
    public boolean monitorThreadpool(ThreadPoolTaskExecutor executor);
    public FilenameFilter getAtmMachineInputFileFilter();
    public CommandLine parseCommandlineoptions(String[] args);
    public AtmMachineConfig createAtmMachineConfigInstance(CommandLine commandLineArgs, String currentDirectoryLocation);
    public void outputAtmMachineInfo(AtmMachineConfig atmMachineConfig, ThreadPoolTaskExecutor executor);

}
