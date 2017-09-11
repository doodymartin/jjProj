/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.datamodel;

import com.jjProj.common.TransactionType;

/**
 * ATM Transaction Class. This class defines the data associated with a transaction session read from the input.
 */
public class AtmTransaction {

    TransactionType type;
    String accountNumner;
    String pin;
    String enteredPin;
    int balanceAmount;
    int overdraftAmount;

    public String getEnteredPin() {
        return enteredPin;
    }
    public void setEnteredPin(String enteredPin) {
        this.enteredPin = enteredPin;
    }
    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }
    public String getAccountNumner() {
        return accountNumner;
    }
    public void setAccountNumner(String accountNumner) {
        this.accountNumner = accountNumner;
    }
    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
    public int getBalanceAmount() {
        return balanceAmount;
    }
    public void setBalanceAmount(int balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    public int getOverdraftAmount() {
        return overdraftAmount;
    }
    public void setOverdraftAmount(int overdraftAmount) {
        this.overdraftAmount = overdraftAmount;
    }



}
