/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.main;

import com.jjProj.service.AtmMachineService;
import com.jjProj.service.impl.AtmMachineServiceImpl;

/**
 * JJ Project ATM Machine Main class
 */
public class AtmMachine
{
    /**
     * Create an instance of the AtmMachine Service to use
     */
    public static AtmMachineService atmMachineService = new AtmMachineServiceImpl();

    public static void main( String[] args )
    {
        /**
         * call the main orchestrating method for the AtmMachine Service
         */
        if(!atmMachineService.atmMachineInstance(args)){
            System.out.println( "ERROR: ATM Machine Failed to process correctly.");
            System.exit(-1);
        }

    }
}
