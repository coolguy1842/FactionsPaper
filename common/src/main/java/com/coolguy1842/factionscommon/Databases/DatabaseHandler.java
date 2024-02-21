package com.coolguy1842.factionscommon.Databases;

import com.coolguy1842.factionscommon.Classes.Database;

public interface DatabaseHandler {
    String getName();
    Database getDatabase();
    
    void initTables();
    void close();
}
