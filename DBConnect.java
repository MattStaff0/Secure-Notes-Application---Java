package secure.notes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public static Connection connect(){
        Connection con = null;

        try{
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:notes/src/main/resources/passuser.db");
            
        } catch (ClassNotFoundException | SQLException e) {e.printStackTrace();}

        return con;
    }


}
