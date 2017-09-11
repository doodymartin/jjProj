/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.common;

/**
 * ATM Machine Errors enum.
 */
public enum AtmMachineError {

    ACCOUNT_ERR("ACCOUNT_ERR "),
    ATM_ERR("ATM_ERR"),
    FUNDS_ERR("FUNDS_ERR"),
    UNKNOWN("");

    private String error;

    AtmMachineError(String error) {
        this.error = error;
    }

    public String error() {
        return error;
    }

}
