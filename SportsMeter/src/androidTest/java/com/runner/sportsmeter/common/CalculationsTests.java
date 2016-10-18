package com.runner.sportsmeter.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created on 17-Oct-16
 */
public class CalculationsTests {

    @Test
    public void convertTimeToString() throws Exception {

        // 1441  24 m 01 s
        // 69  1 m 09 s
        // 2960 49 m 20 s
        // 4061 67 m 41 s
        long test1 = 1441;
        long test2 = 69;
        long test3 = 2960;
        long test4 = 4061;

        String result = Calculations.convertTimeToStringFromSeconds(test1);
        Assert.assertEquals("00h:24m:01s", result);

    }

}