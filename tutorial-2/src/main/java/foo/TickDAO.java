package foo;
   
import java.sql.*;
      
public class TickDAO {

    static String dbUrl;

    public TickDAO() {
        if (dbUrl == null) {
            dbUrl = System.getenv("DATABASE_URL").replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
            dbUpdate("CREATE TABLE ticks (tick timestamp)");
        }
    }

    public void insertTick() {
        dbUpdate("INSERT INTO ticks VALUES (now())");
    }

    private void dbUpdate(String sql) {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbConn.close();
            }
            catch (SQLException ignore) {
            }
        }
    }

    public int getTickCount() throws SQLException {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM ticks");
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            dbConn.close();
        }

        return -1;
    }
 
}
