package foo;

import java.sql.*;

public class TickDAO {

    private Connection getConn() throws SQLException {
        String dbUrl = System.getenv("DATABASE_URL");
        dbUrl = dbUrl.replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
        return DriverManager.getConnection(dbUrl);
    }

    private void createTable() {
        try {
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate("CREATE TABLE ticks (tick timestamp)");
        }
        catch (SQLException e) {
        }
    }

    public int getTickCount() {
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM ticks");
            rs.next();
            return rs.getInt(1);
           }
        catch (SQLException e) {
        }
        return -1;
    }

    public void insertTick() {
        try {
            createTable();
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        }
        catch (SQLException e) {
        }
    }
}
