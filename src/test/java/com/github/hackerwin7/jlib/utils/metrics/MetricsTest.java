package com.github.hackerwin7.jlib.utils.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.github.hackerwin7.jlib.utils.commons.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/25
 * Time: 4:05 PM
 * Desc:    metrics lib test
 *          VM Options :    -Dcom.sun.management.jmxremote.port=9999
 *                          -Dcom.sun.management.jmxremote.authenticate=false
 *                          -Dcom.sun.management.jmxremote.ssl=false
 * Tips:
 */
public class MetricsTest {

    private final MetricRegistry metrics = new MetricRegistry();
    private final Meter requests = metrics.meter("requests");

    public static void main(String[] args) {
        MetricsTest mt = new MetricsTest();
        mt.jmxMultipleGaugeReporterTest();
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
        waiting();
    }

    public void jmxGaugeReporterTest() {
        startJmxReporter();
        metrics.register(MetricRegistry.name(MetricsTest.class, "gauge", "test"), new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return true;
            }
        });
        waiting();
    }

    public void jmxGaugeReporterTest1() {
        startJmxReporter();
        metrics.register(MetricRegistry.name(MetricsTest.class, "gauge", "test"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", true);
                map.put("cake", "chocolate");
                return (HashMap<String, Object>) map;
            }
        });
        waiting();
    }

    public void jmxGaugeReporterTest2() {
        startJmxReporter();
        metrics.register(MetricRegistry.name("fuck", "gauge", "test"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", true);
                map.put("cake", "fuck");
                return (HashMap<String, Object>) map;
            }
        });
        waiting();
    }

    public void jmxGaugeReporterTest3() {
        startJmxReporter();
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", "running");
        metrics.register(MetricRegistry.name("continue"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                return (HashMap<String, Object>) map;
            }
        });
        CommonUtils.delay(5000);
        map.put("status", "switching");
        CommonUtils.delay(5000);
        map.put("status", "started");

        final Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("kiko", "okik");
        metrics.register(MetricRegistry.name("kiko"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                return (HashMap<String, Object>) map1;
            }
        });


        waiting();
    }

    public void jmxMultipleGaugeReporterTest() {
        startJmxReporter();
        final Map<String, Object> mapStatus = new HashMap<String, Object>();
        mapStatus.put("status", "running");
        metrics.register(MetricRegistry.name("status"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                return (HashMap<String, Object>) mapStatus;
            }
        });
        final Map<String, Object> mapServer = new HashMap<String, Object>();
        mapServer.put("server", "running");
        metrics.register(MetricRegistry.name("server"), new Gauge<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> getValue() {
                mapStatus.put("status", "server in trigger");
                return (HashMap<String, Object>) mapServer;
            }
        });
        waiting();
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

    private void waiting() {
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("reporting to jmx......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
