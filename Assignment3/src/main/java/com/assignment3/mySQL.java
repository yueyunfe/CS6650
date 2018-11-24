package com.assignment3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mySQL {


    final String password = "Abc123456";

    public Connection connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String jdbcUrl = "jdbc:mysql://" + "35.236.8.102" + "/" + "yueyunfe" + "?user=" + "root" + "&password=" + password;

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
