/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/

package com.jjProj.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jjProj.atmProcessingEngine.AtmEngineFactory;
import com.jjProj.datamodel.AtmMachineConfig;
import com.jjProj.service.AtmMachineService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ({"classpath:Spring-Context.xml"})
public class AtmMachineServiceImplTest {


    AtmMachineService atmMachineService;
    String basicTestFile;
    String currentCirectory;
    String testInFile;
    String testOutputFile;
    String testProcessedFile;
    String testInFileNormalised;
    File testInFileObject;
    String extension = ".data";
    ThreadPoolTaskExecutor executor;
    AtmEngineFactory atmEngineFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String config[] = { "/Spring-Context.xml" };

        atmMachineService = new AtmMachineServiceImpl();

        basicTestFile = "Atm_data_test_OK_1";
        currentCirectory = System.getProperty("user.dir");

        testInFile = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        testInFileNormalised = testInFile.replaceAll("(?<!https:)\\/\\/", "/");
        testInFileObject = new File(testInFile);

        ApplicationContext context = new ClassPathXmlApplicationContext(config);
        assertNotNull(context);

        atmEngineFactory = (AtmEngineFactory) context.getBean("atmEngineFactory");
        assertNotNull(atmEngineFactory);

        executor = context.getBean("taskExecutor", ThreadPoolTaskExecutor.class);
        assertNotNull(executor);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void cleanUp() throws Exception {


    }

    @Test
    public void testOutputAtmMachineInfo() {
        AtmMachineConfig atmMachineConfig = createAtmMachineConfig(currentCirectory,
                false,
                null,
                testInFile);

        ThreadPoolTaskExecutor executor = new  ThreadPoolTaskExecutor();
        atmMachineService.outputAtmMachineInfo(atmMachineConfig,executor);
    }


    @Test
    public void testCreateAtmMachineConfigInstance() {
        String[] args = new String[] {"-i true"};
        CommandLine commandLineArgs = atmMachineService.parseCommandlineoptions(args);
        assertNotNull(commandLineArgs);
        assertEquals(commandLineArgs.getArgList().size(),0);
        AtmMachineConfig myAtmMachineConfig = atmMachineService.createAtmMachineConfigInstance(commandLineArgs,currentCirectory);
        assertNotNull(myAtmMachineConfig);
    }


    @Test
    public void testGetAtmMachineInputFileFilter() {

        FilenameFilter filenameFilter = atmMachineService.getAtmMachineInputFileFilter();
        assertNotNull(filenameFilter);
    }

    @Test
    public void testCreateAtmDirectoryList() {

        ArrayList<String> listOfDirs = atmMachineService.createAtmDirectoryList(currentCirectory);
        assertNotNull(listOfDirs);
        assertEquals(listOfDirs.size(),3);
    }

    @Test
    public void testMonitorThreadpool() {

        boolean result = atmMachineService.monitorThreadpool(executor);
        assertEquals(result,true);
    }

/*
 * TODO: need to finish this test off
 *
    @Test
    public void testFireThreadPerInputFile() {

        AtmMachineConfig atmMachineConfig = createAtmMachineConfig(currentCirectory,
                false,
                null,
                testInFile);

        AtmEngineFactory atmEngineFactory = Mockito.mock(AtmEngineFactory.class);
        Whitebox.setInternalState(atmMachineService, "atmEngineFactory", atmEngineFactory);

        boolean result = atmMachineService.fireThreadPerInputFile(atmEngineFactory,atmMachineConfig);
        assertEquals(result,true);
    }
*/


    private AtmMachineConfig createAtmMachineConfig(String atmMachineBaseDirectory,
            boolean cleanInputAtmTransactionFile,
            String singleFilePathForProcessing,
            String atmEngineFilePathToProcess){
        AtmMachineConfig atmMachineConfig = new AtmMachineConfig(atmMachineBaseDirectory,
                cleanInputAtmTransactionFile,
                singleFilePathForProcessing);

        return atmMachineConfig;
    }


/* TODO: Need to cover the following classes
 *
    public boolean atmMachineInstance(String[] args);
    public boolean fireThreadPerInputFile(AtmEngineFactory atmEngineFactory, AtmMachineConfig atmMachineConfig);
    public boolean atmDirectoryMaintenance(ArrayList<String> atmDirectoryList, AtmDirectoryActionType action);
    public boolean monitorThreadpool(ThreadPoolTaskExecutor executor);

*/
}



