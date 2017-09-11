/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.datamodel.AtmMachineConfig;


public class AtmEngineServiceImplTest {

    private AtmEngineServiceImpl atmEngineService;
    File mockedFile;
    String basicTestFile;
    String currentCirectory;
    String testInFile;
    String testOutputFile;
    String testProcessedFile;
    String testOutFileNormalised;
    String testProcessedFileNormalised;
    String testInFileNormalised;
    String testInFile_fail;
    String testOutFile;
    File testOutFileObject;
    File testOutFileRemove;
    File testOutputFileObject;
    File testProcessedFileObject;
    File testInFileObject;
    String currentTimeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String extension = ".data";
    String testDummyInputFile = "dummyInoutFile"+currentCirectory +"\\src\\test\\resources\\Atm_data_test_1_not_exist"+extension;

    String testDummyExistingInputFile;
    File testDummyExistingInputFileObject;

    String testDummyExistingProcessedFile;
    File testDummyExistingProcessedFileObject;

    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        basicTestFile = "Atm_data_test_OK_1";
        currentCirectory = System.getProperty("user.dir");
        testInFile_fail = currentCirectory +"\\src\\test\\resources\\Atm_data_test_1_not_exist"+extension;

        testInFile = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        testInFileNormalised = testInFile.replaceAll("(?<!https:)\\/\\/", "/");
        testInFileObject = new File(testInFile);

        testOutFile = currentCirectory +"\\src\\test\\resources\\testOut.data";
        testOutFileObject = new File(testOutFile);

        testOutputFile = currentCirectory + "\\output\\"+basicTestFile+"-"+currentTimeStamp+extension;
        testOutFileNormalised = testOutputFile.replaceAll("(?<!https:)\\/\\/", "/");
        testOutputFileObject = new File (testOutputFile);

        testProcessedFile = currentCirectory + "\\processed\\"+ basicTestFile+"-"+currentTimeStamp+extension;
        testProcessedFileNormalised = testProcessedFile.replaceAll("(?<!https:)\\/\\/", "/");
        testProcessedFileObject = new File (testProcessedFile);

        testOutFileRemove = new File(testOutFile);
        testOutFileRemove.getAbsoluteFile().createNewFile();

        atmEngineService = new AtmEngineServiceImpl();

        bufferedReader = new BufferedReader(new FileReader(testInFile));
        bufferedWriter = new BufferedWriter(new FileWriter(new File(testOutFile)));
        Whitebox.setInternalState(atmEngineService, "bufferedReader", bufferedReader);
        Whitebox.setInternalState(atmEngineService, "bufferedWriter", bufferedWriter);

        /**
         * create a dummy input file for testing clean up operations
         *
         */
        testDummyExistingInputFile = currentCirectory +"\\src\\test\\resources\\Atm_data_test_dummy_1"+extension;
        testDummyExistingInputFileObject = new File(testDummyExistingInputFile);
        testDummyExistingInputFileObject.getAbsoluteFile().createNewFile();

        testDummyExistingProcessedFile = currentCirectory +"\\processed\\Atm_data_test_dummy_1"+extension;
        testDummyExistingProcessedFileObject = new File(testDummyExistingProcessedFile);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void cleanUp() throws Exception {

        if (bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null) {
            bufferedWriter.close();
        }

        testOutFileObject.getAbsoluteFile().delete();
        testOutFileRemove.getAbsoluteFile().delete();
        testDummyExistingInputFileObject.getAbsoluteFile().delete();
        testDummyExistingProcessedFileObject.getAbsoluteFile().delete();

    }

    @Test
    public void testValidateInputFileSuccess() {
        boolean result = atmEngineService.validateInputFile(testInFile);
        assertEquals(result, true);
    }

    @Test
    public void testValidateInputFileFailure() {
        boolean result = atmEngineService.validateInputFile(testInFile_fail);
        assertEquals(result, false);
    }

    @Test
    public void testCreateEngineFilesSuccess() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        try {
            boolean result = atmEngineService.createEngineFiles(atmEngineConfig);
            assertEquals(result, true);
            assertNotNull(atmEngineConfig.getInputFile());
            assertEquals(atmEngineConfig.getInputFile().getAbsolutePath(), testInFileNormalised);

            assertNotNull(atmEngineConfig.getOutputFile());
            assertEquals(atmEngineConfig.getOutputFile().getAbsolutePath(), testOutFileNormalised);

            assertNotNull(atmEngineConfig.getOutputFile());
            assertEquals(atmEngineConfig.getProcessedFile().getAbsolutePath(), testProcessedFileNormalised);

        } catch (Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_OK_1() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        atmEngineConfig.setInputFile(testInFileObject);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFile));
            boolean result = atmEngineService.processInputFile(atmEngineConfig);
            List<String> expectedLines =  Arrays.asList("500", "400", "90","FUNDS_ERR","0");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }


    @Test
    public void testprocessInputFile_ATM_data_test_OK_2() {

        String basicTestFile = "Atm_data_test_OK_2";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);


        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));

            boolean result = atmEngineService.processInputFile(atmEngineConfig);

            List<String> expectedLines =  Arrays.asList("500", "400", "400","400","301","229", "229","100","100","90","0","FUNDS_ERR","FUNDS_ERR","0");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_TX_ERR_1() {

        String basicTestFile = "Atm_data_test_TX_ERR_1";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);

        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));
            boolean result = atmEngineService.processInputFile(atmEngineConfig);
            List<String> expectedLines =  Arrays.asList("6667","6567","6565");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_TX_BAD_INT() {

        String basicTestFile = "Atm_data_test_TX_BAD_INT";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);

        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));
            boolean result = atmEngineService.processInputFile(atmEngineConfig);
            List<String> expectedLines =  Arrays.asList("6667","6567","6565");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_ATM_ERR() {

        String basicTestFile = "Atm_data_test_ATM_ERR";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);


        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));

            boolean result = atmEngineService.processInputFile(atmEngineConfig);

            List<String> expectedLines =  Arrays.asList("500", "400", "ATM_ERR","ATM_ERR","ATM_ERR","ATM_ERR", "ATM_ERR","ATM_ERR","ATM_ERR","ATM_ERR","ATM_ERR","ATM_ERR","ATM_ERR","500","ATM_ERR");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }


    @Test
    public void testprocessInputFile_ATM_data_test_FUNDS_ERR() {

        String basicTestFile = "Atm_data_test_FUNDS_ERR";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);

        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));

            boolean result = atmEngineService.processInputFile(atmEngineConfig);

            List<String> expectedLines =  Arrays.asList("500","400","FUNDS_ERR","300","200","100","FUNDS_ERR","60","50","10","0","0","0","FUNDS_ERR","0","0","0","FUNDS_ERR");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_ACCOUNT_ERR() {

        String basicTestFile = "Atm_data_test_ACCOUNT_ERR";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);

        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));

            boolean result = atmEngineService.processInputFile(atmEngineConfig);

            List<String> expectedLines =  Arrays.asList("ACCOUNT_ERR","100","100","90","0","FUNDS_ERR","FUNDS_ERR","0","ACCOUNT_ERR","100","100","90","0","FUNDS_ERR","FUNDS_ERR","0","ACCOUNT_ERR");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }

    @Test
    public void testprocessInputFile_ATM_data_test_overdraft() {

        String basicTestFile = "Atm_data_test_overdraft";
        String testInFileLocal = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        File testInFileObjectLocal = new File(testInFileLocal);

        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFileLocal);

        atmEngineConfig.setInputFile(testInFileObjectLocal);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testProcessedFileObject);
        try{

            bufferedReader = new BufferedReader(new FileReader(testInFileLocal));

            boolean result = atmEngineService.processInputFile(atmEngineConfig);

            List<String> expectedLines =  Arrays.asList("500","400","FUNDS_ERR","300","200","100","FUNDS_ERR","60","50","10","0","0","0","FUNDS_ERR","0","0","0","FUNDS_ERR","90","FUNDS_ERR","0");
            validateOutputFileAgainstExpected(expectedLines);

            assertEquals(result, true);
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }


    /**
     * TODO: Need to find a better way to test file creation / deletion
     */
    @Test
    public void testAtmEngineCleanUp() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);

        atmEngineConfig.setInputFile(testDummyExistingInputFileObject);
        atmEngineConfig.setOutputFile(testOutFileObject);
        atmEngineConfig.setProcessedFile(testDummyExistingProcessedFileObject);

        boolean result = atmEngineService.atmEngineCleanUp(atmEngineConfig);

        assertEquals(result,true);

    }


    private AtmEngineConfig createAtmEngineConfig(String atmMachineBaseDirectory,
            boolean cleanInputAtmTransactionFile,
            String singleFilePathForProcessing,
            String atmEngineFilePathToProcess){
        AtmMachineConfig atmMachineConfig = new AtmMachineConfig(atmMachineBaseDirectory,
                cleanInputAtmTransactionFile,
                singleFilePathForProcessing);

        AtmEngineConfig atmEngineConfig = new AtmEngineConfig(atmMachineConfig,atmEngineFilePathToProcess);

        return atmEngineConfig;
    }

    private void validateOutputFileAgainstExpected(List<String> expectedLines){
        try{
            FileReader fileReader = new FileReader(testOutFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();

            assertEquals(lines.size(),expectedLines.size());

            int i=0;
            for (String lineRead:lines){
                assertEquals(lineRead,expectedLines.get(i));
                i++;
            }
        } catch(Exception e){
            System.out.print(e);
            Assert.fail();
        }
    }
}



