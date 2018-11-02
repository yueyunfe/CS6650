import java.sql.*;

public class mySQL {


    final String password = "Paul1303821";

    public Connection connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String jdbcUrl = "jdbc:mysql://" + "assignment2.cvfuii3cjrow.us-west-2.rds.amazonaws.com" + ":" + 3306 + "/" + "yueyunfe" + "?user=" + "yueyunfe" + "&password=" + password;

            Connection connection = DriverManager.getConnection(jdbcUrl);

            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
 }
