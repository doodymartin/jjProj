/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.atmProcessingEngine;


import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.datamodel.AtmMachineConfig;
import com.jjProj.service.AtmEngineService;

public class AtmEngineTest {

    AtmEngine atmEngine;
    ApplicationContext context;
    String basicTestFile;
    String currentCirectory;
    String extension = ".data";
    String testInFile;
    String testInFileNormalised;
    File testInFileObject;
    AtmEngineService atmEngineService;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        atmEngine = new AtmEngine();
        basicTestFile = "Atm_data_test_OK_1";
        currentCirectory = System.getProperty("user.dir");
        testInFile = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        testInFileNormalised = testInFile.replaceAll("(?<!https:)\\/\\/", "/");
        testInFileObject = new File(testInFile);

        atmEngineService = Mockito.mock(AtmEngineService.class);
        Mockito.when(atmEngineService.processInputFile(Mockito.any(AtmEngineConfig.class))).thenReturn(true);
        Mockito.when(atmEngineService.atmEngineCleanUp(Mockito.any(AtmEngineConfig.class))).thenReturn(true);
        Mockito.when(atmEngineService.createEngineFiles(Mockito.any(AtmEngineConfig.class))).thenReturn(true);
        Mockito.when(atmEngineService.validateInputFile(Mockito.any(String.class))).thenReturn(true);
        Whitebox.setInternalState(atmEngine, "atmEngineService", atmEngineService);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void testProcessAtmTransactions() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        boolean result = atmEngine.processAtmTransactions(atmEngineConfig);
        assertEquals(result, true);
    }

    @Test
    public void testProcessAtmTransactions_tailure_case_1() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        Mockito.when(atmEngineService.validateInputFile(Mockito.any(String.class))).thenReturn(false);

        boolean result = atmEngine.processAtmTransactions(atmEngineConfig);
        assertEquals(result, false);
    }

    @Test
    public void testProcessAtmTransactions_tailure_case_2() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        Mockito.when(atmEngineService.processInputFile(Mockito.any(AtmEngineConfig.class))).thenReturn(false);

        boolean result = atmEngine.processAtmTransactions(atmEngineConfig);
        assertEquals(result, false);
    }


    @Test
    public void testProcessAtmTransactions_tailure_case_3() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        Mockito.when(atmEngineService.createEngineFiles(Mockito.any(AtmEngineConfig.class))).thenReturn(false);

        boolean result = atmEngine.processAtmTransactions(atmEngineConfig);
        assertEquals(result, false);
    }


    @Test
    public void testProcessAtmTransactions_tailure_case_4() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        Mockito.when(atmEngineService.atmEngineCleanUp(Mockito.any(AtmEngineConfig.class))).thenReturn(false);

        boolean result = atmEngine.processAtmTransactions(atmEngineConfig);
        /**
         * TODO: We ignore the return result for atmEngineCleanUp
         */
        assertEquals(result, true);
    }


    private AtmEngineConfig createAtmEngineConfig(String atmMachineBaseDirectory,
            boolean cleanInputAtmTransactionFile,
            String singleFilePathForProcessing,
            String atmEngineFilePathToProcess){
        AtmMachineConfig atmMachineConfig = new AtmMachineConfig(atmMachineBaseDirectory,
                cleanInputAtmTransactionFile,
                singleFilePathForProcessing);

        AtmEngineConfig atmEngineConfig = new AtmEngineConfig(atmMachineConfig,atmEngineFilePathToProcess);
        atmEngineConfig.setInputFile(new File(atmEngineConfig.getAtmEngineFilePathToProcess()));

        return atmEngineConfig;
    }

}