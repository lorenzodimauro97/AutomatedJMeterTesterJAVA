package com.scippo97enterpriseproductions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    static ScheduledExecutorService executorService;
    private static int stoppingTime;
    private static String batchFileLocation;

    public Main() {
    }

    public static void main(String[] args) throws IOException {
        Config config = new Config();
        stoppingTime = Integer.parseInt(config.Parse("stoppingTime"));
        batchFileLocation = config.Parse("jmeterBatchFilePath");
        int delay = Integer.parseInt(config.Parse("delayInMinutes"));
        Runnable task = () -> {
            try {
                LaunchJMeterScript();
            } catch (IOException var1) {
                WriteToConsole("", "Cannot find batch file! Check your config file! Current location is: " + batchFileLocation);
                executorService.shutdown();
            }

        };
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(task, 0L, delay, TimeUnit.MINUTES);
    }

    private static void LaunchJMeterScript() throws IOException {
        WriteToConsole("", "Launching JMeter Script at " + GetDate().toLocalTime());
        Runtime r = Runtime.getRuntime();
        Process pr = r.exec(batchFileLocation);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        String s;
        while((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        try {
            pr.waitFor();
        } catch (InterruptedException var5) {
        }

        WriteToConsole("", "JMeter script finished at " + GetDate().toLocalTime());
        CheckTime();
    }

    private static LocalDateTime GetDate() {
        Date date = new Date();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static void CheckTime() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour >= stoppingTime) {
            executorService.shutdown();
            WriteToConsole("", "Automated JMeter is done for the day! Press ENTER to exit...");
            System.console().readLine();
        }
    }

    public static void WriteToConsole(String color, String message) {
        if (System.console() == null) {
            System.out.println(message);
        } else {
            if (color.isEmpty()) {
                System.console().writer().println(message);
            } else {
                System.console().writer().println(color + message);
            }

        }
    }
}
