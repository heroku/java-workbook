package foo;
   
import java.sql.SQLException;

public class SchemaCreator {
    public static void main(String[] args) throws SQLException {
        TickDAO tickDAO = new TickDAO();
        TickDAO.createTable();
    }
}
