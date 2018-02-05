package com.company;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main
{
    final static int WarmupTimeSeconds = 3;

    public static AtomicBoolean IsRunning = new AtomicBoolean(true);
    public static AtomicInteger Counter = new AtomicInteger();

    public static void main(String[] args)
        throws Exception
    {
        if (args.length != 3)
            throw new Exception("java Main <PG JDBC URL> <no threads> <no seconds>");

        Class.forName("org.postgresql.Driver");
        String url = args[0];
        int threadCount = Integer.parseInt(args[1]);
        int seconds = Integer.parseInt(args[2]);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(threadCount);
        HikariDataSource dataSource = new HikariDataSource(config);

        Runner[] runners = new NonCachingRunner[threadCount];
        for (int i = 0; i < threadCount; i++)
            runners[i] = new NonCachingRunner(dataSource);
        for (Runner runner : runners)
            runner.start();

        System.out.println("Warming up...");
        Thread.sleep(WarmupTimeSeconds * 1000);

        System.out.println("Starting benchmark...");
        Counter.set(0);
        Thread.sleep(seconds * 1000);

        IsRunning.set(false);
        int transactions = Counter.get();

        for (Runner runner : runners)
            runner.join();

        System.out.println("Ran " + transactions + " in " + seconds + " seconds");
        System.out.println("Transactions per second: " + transactions / seconds);
    }
}
