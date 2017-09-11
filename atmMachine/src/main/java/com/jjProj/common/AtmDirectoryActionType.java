/*********************************************************************************************************************************
* Copyright (c) 2017. martin.doody@gmail.com All rights reserved.
*
* This software is put together for investigation purposes. Use at your own risk.
********************************************************************************************************************************/
package com.jjProj.common;

/**
 * ATM Directory Action Enum.
 */
public enum AtmDirectoryActionType {

    CREATE_DIRECTORY("create"),
    DELETE_DIRECTORY("delete"),
    UNKNOWN("");

    private String action;

    AtmDirectoryActionType(String action) {
        this.action = action;
    }

    public String action() {
        return action;
    }

}
