package foo;

import java.sql.*;

public class TickDAO {
    private static String dbUrl;

    static {
        dbUrl = System.getenv("DATABASE_URL");
        dbUrl = dbUrl.replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
    }

    public int getTickCount() throws SQLException {
        return getTickcountFromDb();
    }

    public static int getScalarValue(String sql) throws SQLException {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            System.out.println("read from database");
            return rs.getInt(1);
        } finally {
           if (dbConn!= null) dbConn.close(); 
        }
    }

    private static void dbUpdate(String sql) throws SQLException {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);
        } finally {
            if (dbConn!= null) dbConn.close();
        }
    }

    private int getTickcountFromDb() throws SQLException {
        return getScalarValue("SELECT count(*) FROM ticks");
    }

    public static void createTable() throws SQLException {
        System.out.println("Creating ticks table.");
        dbUpdate("CREATE TABLE ticks (tick timestamp)");
    }

    public void insertTick() throws SQLException {
        dbUpdate("INSERT INTO ticks VALUES (now())");
    }
}
