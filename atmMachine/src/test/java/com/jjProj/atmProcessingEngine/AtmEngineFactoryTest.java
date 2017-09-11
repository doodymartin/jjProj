/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.atmProcessingEngine;


import static org.junit.Assert.assertNotNull;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.datamodel.AtmMachineConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ({"classpath:Spring-Context.xml"})
public class AtmEngineFactoryTest {

    AtmEngineFactory atmEngineFactory;
    ApplicationContext context;
    String basicTestFile;
    String currentCirectory;
    String extension = ".data";
    String testInFile;
    String testInFileNormalised;
    File testInFileObject;
    ThreadPoolTaskExecutor executor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String config[] = { "/Spring-Context.xml" };

        ApplicationContext context = new ClassPathXmlApplicationContext(config);
        assertNotNull(context);
        atmEngineFactory = (AtmEngineFactory) context.getBean("atmEngineFactory");
        assertNotNull(atmEngineFactory);

        executor = context.getBean("taskExecutor", ThreadPoolTaskExecutor.class);
        assertNotNull(executor);

        basicTestFile = "Atm_data_test_OK_1";
        currentCirectory = System.getProperty("user.dir");
        testInFile = currentCirectory +"\\src\\test\\resources\\"+basicTestFile+extension;
        testInFileNormalised = testInFile.replaceAll("(?<!https:)\\/\\/", "/");
        testInFileObject = new File(testInFile);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void testFireThread() {
        AtmEngineConfig atmEngineConfig = createAtmEngineConfig(currentCirectory,
                false,
                null,
                testInFile);
        /**
         * We are just testing the ATMEngineFactory Class so we are not running to actually fire a thread so we mock it out
         */
        AtmEngine atmEngine = Mockito.mock(AtmEngine.class);
        Whitebox.setInternalState(atmEngineFactory, "atmEngine", atmEngine);
        atmEngineFactory.fire(atmEngineConfig);
    }
    @Test
    public void testCoverGetterAndSetters() {
        AtmEngine atmEngine = Mockito.mock(AtmEngine.class);

        atmEngineFactory.setAtmEngine(atmEngine);
        atmEngineFactory.setAtmFactoryName("test name");
//        atmEngineFactory.setTaskExecutor(taskExecutor);
        assertNotNull(atmEngineFactory.getAtmEngine());
        assertNotNull(atmEngineFactory.getAtmFactoryName());
        assertNotNull(atmEngineFactory.getFactoryName());
        assertNotNull(atmEngineFactory.getTaskExecutor());
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

}



