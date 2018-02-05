package com.company;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Runner extends Thread
{
    protected DataSource dataSource;

    public Runner(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}
