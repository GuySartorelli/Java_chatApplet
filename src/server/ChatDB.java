package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static messages.Protocol.SUCCESS;
import static messages.Protocol.FAIL;
import static messages.Protocol.ERROR;

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
    
    private static String update(String update) throws SQLException {
        Connection db = connect();
        if (db == null) return ERROR;
        int result = db.createStatement().executeUpdate(update);
        db.close();
        return Integer.toString(result);
    }
    
    public static String checkUniqueName(String name) {
        String res = SUCCESS;
        try {
            ResultSet rs = query(String.format("SELECT * FROM users WHERE name = '%s'", name));
            if(rs == null) return ERROR;
            if(rs.next()) res = FAIL;
        } catch (SQLException e) {
            return ERROR;
        }
        return res;
    }
    
    public static String signup(String name, String password) {
        try {
            String result = update(String.format("INSERT INTO users VALUES ('%s', '%s')", name, password));
            return result;
        } catch (SQLException e) {
            return ERROR;
        }
    }
    
    public static String login(String name, String password)
    {
        String res = SUCCESS;
        try {
            ResultSet rs = query(String.format("SELECT * FROM users WHERE name = '%s' AND password = '%s'", name, password));
            if(rs == null) return ERROR;
            if(!rs.next()) res = FAIL;
        } catch (SQLException e) {
            return ERROR;
        }
        return res;
    }
}
