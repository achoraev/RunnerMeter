package com.runner.sportsmeter.enums;

import com.runner.sportsmeter.interfaces.UserMetricsInterface;
import com.runner.sportsmeter.models.Imperial;
import com.runner.sportsmeter.models.Metrics;

/**
 * Created by Angel Raev on 22-Jan-16
 */
public enum UserMetrics {
    IMPERIAL(0, "imperial"),
    METRIC(1, "metric");

    private int val;
    private String strVal;

    UserMetrics(int val, String strVal) {
        this.val = val;
        this.strVal = strVal;
    }

    public int getIntValue(String val){
        switch (val.toLowerCase()) {
            case "imperial":
                return IMPERIAL.val;
            case "metric":
                return METRIC.val;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }

    public UserMetrics getUserMetricValue(int val){
        switch (val) {
            case 0:
                return IMPERIAL;
            case 1:
                return METRIC;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }

    public UserMetricsInterface getUsersUnits(UserMetrics um){
        switch (um.toString()){
            case "imperial":
                return new Imperial();
            case "metric":
                return new Metrics();
        }
        throw new RuntimeException("User Metrics value not supported " + um.toString());
    }
}
