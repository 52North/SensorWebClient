package org.n52.web.v1.ctrl;

import static java.lang.System.currentTimeMillis;

import java.text.NumberFormat;

public class Stopwatch {
    
    private final NumberFormat secondsFormatter = NumberFormat.getInstance();
    
    private long start = currentTimeMillis();
    
    public Stopwatch() {
        secondsFormatter.setMinimumFractionDigits(3);
    }
    
    public long stopInMillis() {
        return currentTimeMillis() - start;
    }
    
    public String stopInSeconds() {
        return secondsFormatter.format(stopInMillis() / 1000d);
    }
    
    public static Stopwatch startStopwatch() {
        return new Stopwatch();
    }

}
