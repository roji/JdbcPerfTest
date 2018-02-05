package com.company;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class NonCachingRunner extends Runner
{
    public NonCachingRunner(DataSource dataSource)
    {
        super(dataSource);
    }

    public void run()
    {
        try {
            while (true)
            {
                Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement("SELECT id, message FROM fortune");

                ArrayList<Fortune> results = new ArrayList<Fortune>();
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                    results.add(new Fortune(rs.getInt(1), rs.getString(2)));
                if (results.size() != 12)
                    throw new Exception("Unexpected number of results! Expected 12 got " + results.size());
                connection.close();

                Main.Counter.incrementAndGet();
                if (!Main.IsRunning.get())
                    return;
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }
}
