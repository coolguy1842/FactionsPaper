package com.coolguy1842.factionscommon.Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.coolguy1842.factionscommon.FactionsCommon;

public class Database {
    String path;
    Connection con;

    private void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:" + this.path;
            // create a connection to the database
            con = DriverManager.getConnection(url);
            
            FactionsCommon.LOGGER.info("Connection to SQLite has been established.");
        } 
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if(con != null) {
            try {
                con.close();
                FactionsCommon.LOGGER.info("Connection to SQLite has been closed.");
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Database(String path) {
        this.path = path;
        this.con = null;
        
        this.connect();
    }

    public void execute(String sql, Object... args) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(sql);

        for(int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }

        stmt.executeUpdate();
    }
    
    public CachedRowSet query(String sql, Object... args) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(sql);

        for(int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }

        ResultSet rs = stmt.executeQuery();
        CachedRowSet cRS = RowSetProvider.newFactory().createCachedRowSet();
        
        if(rs != null) {
            cRS.populate(rs);
            rs.close();
        }
    
        return cRS;
    }
}
