package com.haystaxs.ui.util;

import java.text.DecimalFormat;

/**
 * Created by adnan on 12/21/15.
 */
public class MathUtil {
    public static double roundTo2DecimalPlaces(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }
}
