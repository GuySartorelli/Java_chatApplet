package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatDB {
    
    private static Connection connect() throws SQLException {
        String databaseUser = "sartorguy";
        String databaseUserPass = "password";           
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+databaseUser+"_jdbc";
        Connection connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
        return connection;
    }
    
    private static ResultSet query(String query) throws SQLException {
        Connection db = connect();
        if (db == null) return null;
        ResultSet rs = db.createStatement().executeQuery(query);
        db.close();
        return rs;
    }
    
    private static int update(String update) throws SQLException {
        Connection db = connect();
        if (db == null) return -1;
        int result = db.createStatement().executeUpdate(update);
        db.close();
        return result;
    }
    
    public static int checkUniqueName(String name) {
        int res = 1;
        try {
            ResultSet rs = query(String.format("SELECT * FROM users WHERE name = '%s'", name));
            if(rs == null) return -1;
            if(rs.next()) res = 0;
        } catch (SQLException e) {
            return -1;
        }
        return res;
    }
    
    public static int signup(String name, String password) {
        try {
            int result = update(String.format("INSERT INTO users VALUES ('%s', '%s')", name, password));
            return result;
        } catch (SQLException e) {
            return -1;
        }
    }
    
    public static int login(String name, String password)
    {
        int res = 1;
        try {
            ResultSet rs = query(String.format("SELECT * FROM users WHERE name = '%s' AND password = '%s'", name, password));
            if(rs == null) return -1;
            if(!rs.next()) res = 0;
        } catch (SQLException e) {
            return -1;
        }
        return res;
    }
}
