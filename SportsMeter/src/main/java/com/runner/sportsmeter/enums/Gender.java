package com.runner.sportsmeter.enums;

/**
 * Created by Angel Raev on 26-Jan-16
 */
public enum Gender {
    MALE(0, "male"),
    FEMALE(1, "female");

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
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }
}
