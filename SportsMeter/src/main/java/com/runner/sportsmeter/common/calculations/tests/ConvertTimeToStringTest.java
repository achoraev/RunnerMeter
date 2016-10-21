package com.runner.sportsmeter.common.calculations.tests;

import com.runner.sportsmeter.common.Calculations;

/**
 * Created on 17-Oct-16
 */
public class ConvertTimeToStringTest {

    public static void main(String[] args) {

        // 1441  24 m 01 s
        // 69  1 m 09 s
        // 2960 49 m 20 s
        // 4061 67 m 41 s
        // 4081 68 m 01 s
        // 59 0 m 59
        // 3599 59 m 59 s
        // 3601 1 h 00 m 01 s
        long test1 = 1441;
        long test2 = 69;
        long test3 = 2960;
        long test4 = 4061;
        long test5 = 4081;
        long test6 = 59;
        long test7 = 3599;
        long test8 = 3601;

        String result1 = Calculations.convertTimeToStringFromSeconds(test1);
        String result2 = Calculations.convertTimeToStringFromSeconds(test2);
        String result3 = Calculations.convertTimeToStringFromSeconds(test3);
        String result4 = Calculations.convertTimeToStringFromSeconds(test4);
        String result5 = Calculations.convertTimeToStringFromSeconds(test5);
        String result6 = Calculations.convertTimeToStringFromSeconds(test6);
        String result7 = Calculations.convertTimeToStringFromSeconds(test7);
        String result8 = Calculations.convertTimeToStringFromSeconds(test8);

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);
        System.out.println(result5);
        System.out.println(result6);
        System.out.println(result7);
        System.out.println(result8);
    }

}
