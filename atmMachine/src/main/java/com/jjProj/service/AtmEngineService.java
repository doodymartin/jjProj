/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.service;

import com.jjProj.datamodel.AtmEngineConfig;

/**
 * ATM Engine interface Class.
 */
public interface AtmEngineService {

    public boolean validateInputFile(String atmEngineFilePathToProcess);
    public boolean processInputFile(AtmEngineConfig atmEngineConfig);
    public boolean atmEngineCleanUp(AtmEngineConfig atmEngineConfig);
    public boolean createEngineFiles(AtmEngineConfig atmEngineConfig);

}
