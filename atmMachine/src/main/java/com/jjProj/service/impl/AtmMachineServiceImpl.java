/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.jjProj.atmProcessingEngine.AtmEngineFactory;
import com.jjProj.common.AtmDirectoryActionType;
import com.jjProj.common.AtmEngineConstants;
import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.datamodel.AtmMachineConfig;
import com.jjProj.service.AtmMachineService;

/**
 * ATM Machine Service implementation Class. This class implements AtmMachineService and provides methods to
 * support the processing of an ATM Machine application instance execution.
 *
 * This class is designed to be instantiated with a new AtmMachineServiceImpl() call
 *
 */
public class AtmMachineServiceImpl implements AtmMachineService{

    AtmEngineFactory atmEngineFactory;
    AtmMachineConfig atmMachineConfig;

    public AtmEngineFactory getAtmEngineFactory() {
        return atmEngineFactory;
    }


    public void setAtmEngineFactory(AtmEngineFactory atmEngineFactory) {
        this.atmEngineFactory = atmEngineFactory;
    }


    public AtmMachineConfig getAtmMachineConfig() {
        return atmMachineConfig;
    }


    public void setAtmMachineConfig(AtmMachineConfig atmMachineConfig) {
        this.atmMachineConfig = atmMachineConfig;
    }


    /**
     * This method is used to orchestrate the execution of the ATM Machine instance execution.
     *
     * @param String[] - The commandline argument values to process
     * @return boolean - success or failure of processing
     */
    public boolean atmMachineInstance(String[] args){

        boolean result = true;
        /**
         * define the Spring context XML files here that define the application beans
         */
        String config[] = { "/Spring-Context.xml" };
        /**
         * Process the command line arguments provided by the User
         */
        CommandLine commandLineArgs = this.parseCommandlineoptions(args);
        if (commandLineArgs == null){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to process command line arguments.");
        }
        /**
         * Create the required directories for this ATM Machine instance so that
         * ATM Transaction data files for input/output/processing can be processed.
         * The directories will be based on the current location of the executable jar file.
         */
        Path relativePath = Paths.get("");
        String currentDirectoryLocation = relativePath.toAbsolutePath().toString();
        if (!this.atmDirectoryMaintenance(this.createAtmDirectoryList(currentDirectoryLocation),AtmDirectoryActionType.CREATE_DIRECTORY)){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to create processing directories");
        }
        /**
         * Create the Spring Context and Start the main ATM Machine processing factory
         * that will create a thread per ATM Transaction data file.
         */
        ApplicationContext context = new ClassPathXmlApplicationContext(config);
        if (context == null){
            this.exitAtmMachineInstance("ERROR: ATM MachineFailed to start ATM Machine Spring Context.");
        }
        /**
         * Creates a ThreadPoolTaskExecutor bean wrapped in a factory class
         */
        atmEngineFactory = (AtmEngineFactory) context.getBean("atmEngineFactory");
        if (atmEngineFactory == null){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to start ATM Machine Engine Factory.");
        }
        ThreadPoolTaskExecutor executor = context.getBean("taskExecutor", ThreadPoolTaskExecutor.class);
        if (executor == null){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to start ATM Machine Engine Factory Thread pool.");
        }
        /**
         * Creates a AtmMachineConfig class
         */
        atmMachineConfig = this.createAtmMachineConfigInstance(commandLineArgs, currentDirectoryLocation);
        if (atmMachineConfig == null){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to create ATM Machine configuration.");
        }
        /**
         * Output some usefull information for the user
         *
         * TODO: output this info to a file using Log4j
         */
        this.outputAtmMachineInfo(atmMachineConfig, executor);
        /**
         * Fire a processing thread from the thread pool per input file
         */
        if (!this.fireThreadPerInputFile(atmEngineFactory, atmMachineConfig)){
            this.exitAtmMachineInstance("ERROR: ATM Machine Failed to fire input processing threads.");
        }
        /**
         * Wait for all thread processing to complete before exiting the application
         */
        this.monitorThreadpool(executor);
        /**
         * TODO : Find a better way to clean up the directories we created, this is commented out for the moment
         */
//        atmDirectoryMaintenance(createAtmDirectoryList(currentDirectoryLocation),AtmDirectoryActionType.DELETE_DIRECTORY);


        System.out.println( "INFO: ATM Machine Stopped." );

        return result;
    }


    /**
     * This method is used to create an ATM Engine worker thread from the Spring Threadpool for each
     * ATM transaction input file that is identified by the ATM Machine.
     *
     *  The user can specify a single ATM transaction input file of else the ATM Machine will read in all relevant
     *  ATM transaction input files from the input directory.
     *
     * @param AtmEngineFactory - The ATM Engine factory that will be used to create the engine worker threads
     * @param AtmEngineConfig - ATM Engine Config
     * @return boolean - success or failure of processing
     */
    public boolean fireThreadPerInputFile(AtmEngineFactory atmEngineFactory, AtmMachineConfig atmMachineConfig){
        boolean result = true;
        FilenameFilter atmMachineFileFilter =  getAtmMachineInputFileFilter();
        /**
         * If the user has specified a single ATM transaction file to process
         * then only process that single file and exit the ATM Machine application
         */
        if (atmMachineConfig.getSingleFilePathForProcessing() != null){
            AtmEngineConfig atmEngineConfig = new AtmEngineConfig(atmMachineConfig,atmMachineConfig.getSingleFilePathForProcessing());
            atmEngineFactory.fire(atmEngineConfig);
        } else{
            File atmMachineInputDirectory = new File (atmMachineConfig.getAtmMachineBaseDirectory()+ "/" + AtmEngineConstants.ATM_MACHINE_INPUT_ID_NAME);
            for (File atmTransactionFile : atmMachineInputDirectory.listFiles(atmMachineFileFilter)) {
                if (atmTransactionFile.isFile()) {
                    try {
                        System.out.println("INFO: ATM Machine processing file["+atmTransactionFile.getName()+"]");
                        AtmEngineConfig atmEngineConfig = new AtmEngineConfig(atmMachineConfig,atmTransactionFile.getCanonicalPath());
                        atmEngineFactory.fire(atmEngineConfig);
                    } catch(Exception e){
                        System.out.println("ERROR: ATM Machine Failed to process file["+atmTransactionFile.getName()+"]");
                        /**
                         * TODO: Should we fail and exit the loop here if we fail to start a processing thread ?
                         */
                        //result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method is used to exit the main ATM Machine program when an error is found and print a message to the user.
     *
     * @param String - the message to print
     */
    public void exitAtmMachineInstance(String message){
        System.out.println( message);
        System.exit(-1);
    }

    /**
     * This method is used to create or delete the ATM Machine application directories from the base directory.
     * The ATM Machine application directories creaeted include the following.
     *  - input
     *  - output
     *  - processed
     *
     * @param AtmEngineConfig - ATM Engine Config
     * @return boolean - success or failure of processing
     */
    public boolean atmDirectoryMaintenance(ArrayList<String> atmDirectoryList, AtmDirectoryActionType action){
        boolean result = true;
        for (String atmDirectory:atmDirectoryList){
            File directory = new File(atmDirectory);
            if (AtmDirectoryActionType.CREATE_DIRECTORY.equals(action) && !directory.exists()){
                directory.mkdir();
                directory.setWritable(true);
                directory.setExecutable(true);
            } else if (AtmDirectoryActionType.DELETE_DIRECTORY.equals(action) && directory.exists()){
                directory.delete();
            } else {
                System.out.println("INFO: No action required for ATM Directory Maintenance action[" + action + "] directory[" + atmDirectory + "]");
            }
        }
        /**
         * TODO: detect an error condition in this method
         */
        return result;
    }

    /**
     * This method is used to create a list of strings that identify the directories off the base
     * ATM Machine application directory that will be used for processing.
     *  - input
     *  - output
     *  - processed
     *
     * @param String - the ATM Machine base directory
     * @return ArrayList<String> - the list of directories to be created
     */
    public ArrayList<String> createAtmDirectoryList(String currentDirectoryLocation){
        ArrayList<String> atmDirectoryList = new ArrayList<String>();

        atmDirectoryList.add(currentDirectoryLocation + "/" + AtmEngineConstants.ATM_MACHINE_INPUT_ID_NAME);
        atmDirectoryList.add(currentDirectoryLocation + "/" + AtmEngineConstants.ATM_MACHINE_OUTPUT_ID_NAME);
        atmDirectoryList.add(currentDirectoryLocation + "/" + AtmEngineConstants.ATM_MACHINE_PROCESSED_ID_NAME);

        return atmDirectoryList;
    }


    /**
     * This method is used to make the main application thread wait and monitor the Spring
     * threadpool. When there are no active threads running in the threadpool the ATM Machine
     * application can finish successfully.
     *
     * @param ThreadPoolTaskExecutor - The Spring threadpool used
     * @return boolean - success or failure of processing
     */
    public boolean monitorThreadpool(ThreadPoolTaskExecutor executor){
        boolean result = true;
        for (;;) {
            int threadCount = executor.getActiveCount();
            /**
             * TODO wrap in log4j so that we can control logging
             */
//            System.out.println("INFO: ATM Machine Active Threads count["+threadCount+"]");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                result = false;
                System.out.println("ERROR: ATM Machine Thread pool monitor error.");
                System.out.println(e);
            }
            if (threadCount == 0) {
                executor.shutdown();
                break;
            }
        }
        return result;
    }
    /**
     * This method is used to create a Java FilenameFilter object to help identify ATM MAchine
     * input files in the input directory (i.e. *.data files)
     *
     * @return FilenameFilter - the filter to use to identify ATM Machine input files
     */
    public FilenameFilter getAtmMachineInputFileFilter(){
        FilenameFilter atmMachineFileFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(AtmEngineConstants.ATM_MACHINE_INPUT_FILE_EXTENSION_ID)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        return atmMachineFileFilter;
    }

    /**
     * This method is used to create Apache common CommandLine object to manage the
     * command line arguments going into the ATM Machine.
     *
     * @param String[]  - The ATM Machine input parameters provided by the user
     * @return CommandLine - the ATM Machine command line inputs identified
     */
    public CommandLine parseCommandlineoptions(String[] args){
        /**
         * Process the command line arguments
         */
        Options options = new Options();

        Option input = new Option(AtmEngineConstants.ATM_MACHINE_CLI_INPUT_OPT_ID,
                AtmEngineConstants.ATM_MACHINE_INPUT_ID_NAME, true,
                "Move Input file after processing to the proessed directory");
        input.setRequired(false);
        options.addOption(input);

        Option singleInputFile = new Option(AtmEngineConstants.ATM_MACHINE_CLI_FILE_OPT_ID,
                AtmEngineConstants.ATM_MACHINE_FILE_ID_NAME, true,
                "Specify Path to single file to process");
        singleInputFile.setRequired(false);
        options.addOption(singleInputFile);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd= null;

        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("JJ ATM Machine commandline options:", options);
            cmd = null;
        }
        return cmd;
    }

    /**
     * This method is used to create the AtmMachineConfig object to manage the ATM Machine execution.
     *
     * @param CommandLine  - The ATM Machine input parameters provided by the user
     * @param String -  The ATM MAchine base directory
     * @return AtmMachineConfig - the ATM Machine config object populate
     */
    public AtmMachineConfig createAtmMachineConfigInstance(CommandLine commandLineArgs, String currentDirectoryLocation){
        /**
         * Populate the User commandline line inputs parameters provided by the user
         * for this running ATM Machine instance.
         */
        AtmMachineConfig atmMachineConfig = new AtmMachineConfig(currentDirectoryLocation,
                Boolean.parseBoolean(commandLineArgs.getOptionValue(AtmEngineConstants.ATM_MACHINE_INPUT_ID_NAME, AtmEngineConstants.ATM_MACHINE_DEFAULT_MOVE_INPUT_FILE_OPT_ID)),
                commandLineArgs.getOptionValue(AtmEngineConstants.ATM_MACHINE_FILE_ID_NAME));

        return atmMachineConfig;
    }

    /**
     * This method is output INFO messages to the user on the command line.
     *
     * TODO: We should be using something like log4j for our logging
     *
     * @param AtmMachineConfig - The ATM Machine config
     * @param ThreadPoolTaskExecutor - The ATM Machine Spring Threadpool Executor
     */
    public void outputAtmMachineInfo(AtmMachineConfig atmMachineConfig, ThreadPoolTaskExecutor executor){
        /**
         * Provide some startup information to the user on the command line
         */
        System.out.println( "" );
        System.out.println( "INFO: ===============================================================================" );
        System.out.println( "INFO: ATM Machine Running with configuration....." );
        System.out.println( "INFO: ATM Machine base directory locaiton [" + atmMachineConfig.getAtmMachineBaseDirectory() +"]");
        System.out.println( "INFO: Move input ATM transaction file after procssing [" + atmMachineConfig.isMoveInputAtmTransactionFileToProcessed()+"]");
        System.out.println( "INFO: Path to single file path to process[" + atmMachineConfig.getSingleFilePathForProcessing()+"]");
        System.out.println( "INFO: Max thread pool size[" + executor.getMaxPoolSize()+"]");;
        System.out.println( "INFO: Core Thread pool size[" + executor.getCorePoolSize()+"]");;
        System.out.println( "INFO: Active thread count[" + executor.getActiveCount()+"]");;
        System.out.println( "INFO: Thread pool size[" + executor.getPoolSize()+"]");;
        System.out.println( "INFO: ===============================================================================" );
        System.out.println( "" );
    }
}
