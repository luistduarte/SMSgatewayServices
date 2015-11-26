package Utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis Duarte
 */
public class Database {
     private Connection conn;

    public Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsgw", "root", "root");
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    
    public ResultSet executeQuery(String query, String param1) {
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, param1);
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    public ResultSet executeQuery(String query, String param1,String param2) {
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, param1);
            stmt.setString(2, param2);
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    
    public CallableStatement executeCall(String procedure) {
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(procedure);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return cs;
    }
        
    public int executeUpdate(String query) {
        int rs = 0;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            rs = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    public int executeUpdate(String query ,String param1) {
        int rs = 0;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, param1);
            rs = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    public int executeUpdate(String query ,String param1, String param2) {
        int rs = 0;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, param1);
            stmt.setString(2, param2);
            rs = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    public int executeUpdate(String query ,String param1, String param2, String param3) {
        int rs = 0;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, param1);
            stmt.setString(2, param2);
            stmt.setString(3, param3);
            rs = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return rs;
    }
    
    public Connection getConnection(){
        return conn;
    }
    
    public void close(){
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
