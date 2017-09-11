/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.common;

/**
 * ATM Engine Transaction Types enum.
 */
public enum TransactionType {

    TRANSACTION_WITHDRAWL("W"),
    TRANSACTION_BALANCE("B"),
    UNKNOWN("");

    private String identifier;

    TransactionType(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

}
