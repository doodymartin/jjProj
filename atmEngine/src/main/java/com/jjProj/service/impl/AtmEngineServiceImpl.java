/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import com.jjProj.common.AtmEngineConstants;
import com.jjProj.common.AtmMachineError;
import com.jjProj.datamodel.AtmEngineConfig;
import com.jjProj.datamodel.AtmTransaction;
import com.jjProj.service.AtmEngineService;

/**
 * ATM Engine Service implementation Class. This class implements AtmEngineService and provides methods to
 * support the processing of an input file.
 *
 * We annotate this class with @Service so that Spring will pick it up with it's scan on Spring context init.
 */
@Service
public class AtmEngineServiceImpl implements AtmEngineService{

    private FileReader fileReader = null;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private FileWriter fileWriter = null;

    /**
     * This method is used to validate the ATM Machine Input File and create a Java File object for it.
     *
     * @param String - Define the ATM Machine input data file to be processed
     * @return boolean - success or failure of processing
     */
    public boolean validateInputFile(String atmEngineFilePathToProcess) {
        boolean validateInputFileSuccess = false;
        if(atmEngineFilePathToProcess != null && !atmEngineFilePathToProcess.isEmpty()){
            File newAtmEngineFilePathToProcess = new File(atmEngineFilePathToProcess);
            if (newAtmEngineFilePathToProcess.isFile()) {
                validateInputFileSuccess = true;
            }
        }
        return validateInputFileSuccess;
    }

    /**
     * This method is used to process and entire ATM Machine Input File. This method with open
     * the input file and create the output file in the output directory
     * as well as move the input file into the processed directory location
     * once processing is complete.
     *
     * @param AtmEngineConfig - Define the ATM Machine config for processing
     * @return boolean - success or failure of processing
     */
    public boolean processInputFile(AtmEngineConfig atmEngineConfig) {
        String atmEngineFilePathToProcess = atmEngineConfig.getAtmEngineFilePathToProcess();
        boolean processInputFileSuccess = false;
        boolean readFirstLine = false;
        int atmBalance = 0;
        int linesRead = 0;
        int customerTransProcessed = 0;
        String line = null;

        List<String> transactionLines = new ArrayList<String>();
        try {

            if (!atmEngineConfig.getOutputFile().getAbsoluteFile().exists()) {
                /**
                 * TODO: Be carefull how output filenames are constructed as there is
                 * a bug with the size of the filename with createNewFile().
                 * We need to add additional checks around file name size!
                 *
                 * The Exception is as below
                 * java.io.IOException: The filename, directory name, or volume label syntax is incorrect
                 *
                 * The bug is explained in
                 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4812991
                 */
                atmEngineConfig.getOutputFile().createNewFile();
            }
            this.setFileReader(new FileReader(atmEngineFilePathToProcess));
            this.setBufferedReader(new BufferedReader(fileReader));
            /*
             *  TODO: If an an output file already exists we are appending output to it
             * file which may need more attention !
             */
            this.setFileWriter(new FileWriter(atmEngineConfig.getOutputFile().getAbsoluteFile(), true));
            this.setBufferedWriter(new BufferedWriter(fileWriter));

            /**
             * Validate that the first line is a number that can be used to init the ATM balance
             */
            String firstLine = this.getBufferedReader().readLine();
            try {
                atmBalance = Integer.parseInt(firstLine.trim());
                readFirstLine = true;
                linesRead++;
            } catch(NumberFormatException nfe) {
                System.out.println( "ERROR: Failed to convert first line to an ATM balance for["+atmEngineFilePathToProcess+"]");
            }

            if (readFirstLine){
                /**
                 * Now read each customer transaction in the file and process that action
                 */
                while ((line = this.getBufferedReader().readLine()) != null) {
                    if (line.isEmpty() || line == null){
                        /**
                         * process the customer transaction lines that we have just read in
                         */
                        if(transactionLines.size() > 0) {
                            if(processCustomerAtmTransaction(this.getBufferedWriter(), atmBalance, transactionLines)){
                                customerTransProcessed++;
                            } else {
                                System.out.println("ERROR: Failed to process customer transaction in["+atmEngineFilePathToProcess+"]");
                            }
                        }
                        /**
                         * Clear out he read lines so we are ready for the next transaction
                         */
                        transactionLines.clear();
                    } else {
                        transactionLines.add(line);
                    }
                    linesRead++;
                }
                if(transactionLines.size() > 0) {
                    if(processCustomerAtmTransaction(this.getBufferedWriter(), atmBalance, transactionLines)){
                        customerTransProcessed++;
                    } else {
                        System.out.println("ERROR: Failed to process customer transaction in["+atmEngineFilePathToProcess+"]");
                    }
                    transactionLines.clear();
                }
                /**
                 * TODO wrap in log4j so that we can control logging
                 */
//                System.out.println("INFO: Customer Transaction Processed["+customerTransProcessed+"] in ["+atmEngineFilePathToProcess+"]");

                processInputFileSuccess = true;
            }

        } catch(Exception e){
            System.out.println( e);
            System.out.println( "ERROR: File processing error in ["+atmEngineFilePathToProcess+"] at Line["+linesRead+"]");
        } finally{
            /**
             * TODO wrap in log4j so that we can control logging
             */
//            System.out.println("INFO: Closing Customer Transaction Processing files.");
            try {
                if (fileReader != null){
                    fileReader.close();
                }
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch(Exception e){
                System.out.println( "ERROR: Failed to close file ["+atmEngineFilePathToProcess+"] at Line["+linesRead+"]");
            }
        }
        return processInputFileSuccess;
    }

    /**
     * This method is used to process a set of customer transactions read in from the input file and write the
     * appropriate output to the output file. A set of transaction could be a series of balance/withdrawal
     * transactions for a particular customer.
     *
     * @param BufferedWriter - The output file writer to use for output while processging the transaction
     * @param int - The ATM balance read in from the input file
     * @param List<String> - The lines read in from input file for this customer transaction
     * @return boolean - success or failure of processing
     */
    private boolean processCustomerAtmTransaction(BufferedWriter bufferedWriter, int atmBalance, List<String> transactionLines){

        boolean processCustomerAtmTransactionSuccess = true;
        AtmTransaction atmTransaction = new AtmTransaction();

        String[] customerIdentification = transactionLines.get(0).split(" ");

        if (customerIdentification.length == 3){
            /**
             * Validate Customer Identification
             */
            atmTransaction.setAccountNumner(customerIdentification[0]);
            atmTransaction.setPin(customerIdentification[1]);
            atmTransaction.setEnteredPin(customerIdentification[2]);

            if (atmTransaction.getPin().equals(atmTransaction.getEnteredPin())){
                String[] customerBalance = transactionLines.get(1).split(" ");

                if (customerBalance.length == 2){
                    /**
                     * Validate Customer Balance and overdraft
                     */
                    int balance =0;
                    int overDraft =0;
                    if (((balance = validateAmount(customerBalance[0])) != -1) &&
                            ((overDraft = validateAmount(customerBalance[1])) != -1)) {

                        /**
                         * Set the parsed balance and overdraft amounts in the transaction object
                         */
                        atmTransaction.setBalanceAmount(balance);
                        atmTransaction.setOverdraftAmount(overDraft);

                        /**
                         * Remove the first 2 lines that we read for this transaction
                         * so we are just left with the Customer transaction operations
                         * that we need to process, i.e. Balance/Withdrawals
                         */
                        transactionLines.remove(0);
                        transactionLines.remove(0);

                        for (String line:transactionLines){
                            String[] transaction = line.split(" ");
                            if (transaction.length == 1){
                                if (AtmEngineConstants.ATM_CUSTOMER_BALANCE_TRANSACTION_ID.equals(transaction[0])){
                                    writeToOutputFile(bufferedWriter, String.valueOf(atmTransaction.getBalanceAmount()));
                                } else {
                                    System.out.println( "ERROR: Customer transction should be a Balance but is not["+transaction[0]+"]");
                                }
                            } else if(transaction.length == 2){
                                if (AtmEngineConstants.ATM_CUSTOMER_WITHDRAWL_TRANSACTION_ID.equals(transaction[0])){

                                    int withdrawal =0;
                                    int customerAvailableBalance = atmTransaction.getBalanceAmount() + atmTransaction.getOverdraftAmount();
                                    if((withdrawal = validateAmount(transaction[1])) != -1){
                                        if(((atmBalance - withdrawal) >=0) && atmBalance != 0){
                                            atmBalance = (atmBalance-withdrawal);
                                            if((customerAvailableBalance - withdrawal) >=0 ){
                                                int debitOverdraftAmount = (atmTransaction.getBalanceAmount() - withdrawal);
                                                if (debitOverdraftAmount < 0){
                                                    debitOverdraftAmount = -debitOverdraftAmount;
                                                    atmTransaction.setOverdraftAmount((atmTransaction.getOverdraftAmount() - debitOverdraftAmount));
                                                    atmTransaction.setBalanceAmount(0);
                                                } else {
                                                    atmTransaction.setBalanceAmount((atmTransaction.getBalanceAmount() - withdrawal));
                                                    /**
                                                     * just in case ensure that the ATM balance never goes below zero!!
                                                     */
                                                    if (atmTransaction.getBalanceAmount() <0){
                                                        atmTransaction.setBalanceAmount(0);
                                                    }
                                                }
                                                writeToOutputFile(bufferedWriter, String.valueOf(atmTransaction.getBalanceAmount()));
                                            } else {
                                                writeToOutputFile(bufferedWriter, AtmMachineError.FUNDS_ERR.toString());
//                                                System.out.println( "ERROR: Customer withdrawal Insufficient funds");
                                            }
                                        } else {
                                            writeToOutputFile(bufferedWriter, AtmMachineError.ATM_ERR.toString());
//                                            System.out.println( "ERROR: ATM Machine Insufficient funds");
                                        }
                                    } else {
                                        System.out.println( "ERROR: Invalid withdrawal amount in transaction");
                                    }
                                } else {
                                    System.out.println( "ERROR: Customer transction should be a Withdrawal but is not ["+transaction[0]+"]");
                                }
                            }  else {
                                System.out.println( "ERROR: Invalid input.");
                                processCustomerAtmTransactionSuccess = false;
                                break;
                            }
                        }
                    } else {
                        System.out.println( "ERROR: Invalid Customer Balance amounts in transaction");
                        processCustomerAtmTransactionSuccess = false;
                    }
                } else {
                    System.out.println( "ERROR: Invalid Customer Balance values["+customerBalance.length+"] in transaction");
                    processCustomerAtmTransactionSuccess = false;
                }
            } else {
                System.out.println( "ERROR: Customer PIN did not match");
                writeToOutputFile(bufferedWriter, AtmMachineError.ACCOUNT_ERR.toString());
            }
        } else {
            System.out.println( "ERROR: Invalid Customer Identification values in transaction");
            writeToOutputFile(bufferedWriter, AtmMachineError.ACCOUNT_ERR.toString());
        }
        return processCustomerAtmTransactionSuccess;
    }

    /**
     * This method is used to convert a String to an Integer.
     *
     * @param String - The String input amount to be converted to an int
     * @return int - the amount converted from a string
     */
    private int validateAmount(String inputAmount){
        int amount = -1;
        try {
            /**
             * TODO: We should be using something like a double instead of on int to handle fractions in our customer account.
             */
            amount = Integer.parseInt(inputAmount.trim());
        } catch(Exception e) {
            System.out.println( "ERROR: Failed to validate Customer Amount Balance");
            amount = -1;
        }
        return amount;
    }

    /**
     * TODO: We need to find a better solution to move and delete file as some of these Java operations
     * are dependent on how the underlying Virtual machine implements the operations on the operating system.
     *
     * This method is used to cleanup the files after processing and also to copy the input file
     * over to the processed directory.
     *
     * @param AtmEngineConfig - ATM Engine Config
     * @return boolean - success or failure of processing
     */
    public boolean atmEngineCleanUp(AtmEngineConfig atmEngineConfig) {
        boolean atmEngineCleanUpSuccess = true;
        /**
         * Copy/Move the input file to the processed directory using the Java renameTo() method.
         *
         * Rename the input file to the processed file in the processed directory,
         * then delete the file in the input directory if required.
         *
         * TODO: we need to handle this copy to the processed directory in a better way.
         * If the file already exists in the processed directory then the renameTo()
         * call will return false and the file will not be copied over.
         */
        if (atmEngineConfig.getInputFile().isFile() && atmEngineConfig.isMoveInputAtmTransactionFileToProcessed()) {
            if (!atmEngineConfig.getInputFile().renameTo(atmEngineConfig.getProcessedFile())){
                System.out.println( "INFO: File already exists in the processed directory ["+atmEngineConfig.getProcessedFile().getAbsolutePath()+"]");
            }
        }
        return atmEngineCleanUpSuccess;
    }

    /**
     * This method is used to create the File objects for the ATM Machine engine. The
     * file objects will be used to create the processing files in the directories below off the base
     * ATM Machine application directory.
     *  - input (files are placed in here by the user/ other application)
     *  - output (files are generated in here by the ATM Machine application)
     *  - processed (files are placed in here by the ATM Machine application after being processed)
     *
     * @param AtmEngineConfig - ATM Engine Config
     * @return boolean - success or failure of processing
     */
    public boolean createEngineFiles(AtmEngineConfig atmEngineConfig) {
        boolean result = true;
        File newFile = new File(atmEngineConfig.getAtmEngineFilePathToProcess());
        if (newFile != null) {
            String processingFileName = newFile.getName().substring(0, newFile.getName().lastIndexOf("."));
            /**
             * TODO: Ideally we would put a Date & Time stamp on the file name
             */
    //        String currentTimeStamp = new SimpleDateFormat("yyyyMMdd-HH:mm").format(new Date());
            String currentTimeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

            File outputFileName = new File(atmEngineConfig.getAtmMachineBaseDirectory() +
                    "/"+ AtmEngineConstants.ATM_MACHINE_OUTPUT_ID_NAME +
                    "/"+ processingFileName+"-"+currentTimeStamp+
                    AtmEngineConstants.ATM_MACHINE_INPUT_FILE_EXTENSION_ID);
            outputFileName.setWritable(true);

            File processedFileName = new File(atmEngineConfig.getAtmMachineBaseDirectory() +
                    "/"+ AtmEngineConstants.ATM_MACHINE_PROCESSED_ID_NAME +
                    "/"+ processingFileName+"-"+currentTimeStamp+
                    AtmEngineConstants.ATM_MACHINE_INPUT_FILE_EXTENSION_ID);
            processedFileName.setWritable(true);

            atmEngineConfig.setInputFile(new File(atmEngineConfig.getAtmEngineFilePathToProcess()));
            atmEngineConfig.setOutputFile(outputFileName);
            atmEngineConfig.setProcessedFile(processedFileName);
        } else{
            result = false;
        }
        return result;
    }

    /**
     * This method is used to write output strings to the output buffer defined.
     *
     * @param BufferedWriter - The output file writer to use for output
     * @param String - The string to write to the BufferedWriter
     * @return boolean - success or failure of processing
     */
    private boolean writeToOutputFile(BufferedWriter bufferedWriter, String outputContent){
        try{
            if (bufferedWriter != null){
                bufferedWriter.write(outputContent+"\n");
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            System.out.println( "ERROR: Failed to write to output file");
            return false;
        }
        return true;
    }


    public FileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

}
