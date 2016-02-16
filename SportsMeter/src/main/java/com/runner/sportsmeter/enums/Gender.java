package com.runner.sportsmeter.enums;

/**
 * Created by Angel Raev on 26-Jan-16
 */
public enum Gender {
    MALE(0, "male"),
    FEMALE(1, "female"),
    NOT_SET(2, "not_set");

    private final int val;
    private String strVal;

    Gender(int val, String strVal) {
        this.val = val;
        this.strVal = strVal;
    }

    public Gender getGenderValue(int val){
        switch (val) {
            case 0:
                return MALE;
            case 1:
                return FEMALE;
            case 2:
                return NOT_SET;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }

    public int getIntValue(String val){
        switch (val) {
            case "MALE":
                return MALE.val;
            case "FEMALE":
                return FEMALE.val;
            case "NOT_SET":
                return NOT_SET.val;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }
}
