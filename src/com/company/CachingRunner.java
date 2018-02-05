package com.company;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class CachingRunner extends Runner
{
    Connection connection;
    PreparedStatement pstmt;

    public CachingRunner(DataSource dataSource)
            throws SQLException
    {
        super(dataSource);
        connection = dataSource.getConnection();
        pstmt = connection.prepareStatement("SELECT id, message FROM fortune");
    }

    public void run()
    {
        System.out.println("Start thread");
        try {
            while (true)
            {
                ArrayList<Fortune> results = new ArrayList<Fortune>();
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                    results.add(new Fortune(rs.getInt(1), rs.getString(2)));
                }
                if (results.size() != 12)
                    throw new Exception("Unexpected number of results! Expected 12 got " + results.size());
                Main.Counter.incrementAndGet();
                if (!Main.IsRunning.get())
                {
                    System.out.println("Thread interrupted");
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }
}
