/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/

package com.jjProj.main;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import com.jjProj.service.AtmMachineService;

public class AtmMachineTest {
    @Test
    public void testMain() throws IOException {
        String[] args = new String[] {"-i true"};;
        AtmMachineService atmMachineService = Mockito.mock(AtmMachineService.class);
        Mockito.when(atmMachineService.atmMachineInstance(args)).thenReturn(true);
        AtmMachine.atmMachineService = atmMachineService;
        AtmMachine.main(args);
    }
}
