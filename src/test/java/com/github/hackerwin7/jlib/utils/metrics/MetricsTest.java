package com.github.hackerwin7.jlib.utils.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/25
 * Time: 4:05 PM
 * Desc: metrics lib test
 * Tips:
 */
public class MetricsTest {

    private final MetricRegistry metrics = new MetricRegistry();
    private final Meter requests = metrics.meter("requests");

    public static void main(String[] args) {
        MetricsTest mt = new MetricsTest();
        mt.jmxReporterTest();
    }

    public void getStartedTest() {
        startReport();
        Meter requests = metrics.meter("requests");
        requests.mark();
        waitSeconds();
    }

    public void getStartedTest1() {
        startReport();
        Meter requests = metrics.meter("requests");
        requests.mark();
        waitSeconds();
    }

    public void jmxReporterTest() {
        startJmxReporter();
        Meter requests = metrics.meter("requests");
        requests.mark();
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("reporting to jmx......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    private void startJmxReporter() {
        JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();
    }

    private void waitSeconds() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitSeconds(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void meter() {
        requests.mark();
    }

    private void consoleReporter() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }
}
