package ru.otus.basic.yampolskiy.domain.repositories;

import ru.otus.basic.yampolskiy.webserver.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionsManager extends ConfigLoader{
    private static final String DATABASE_URL = getProperty("database.url");
    private static final String POSTGRES_USER = getProperty("database.user");
    private static final String POSTGRES_PASSWORD = getProperty("database.password");
    private static ConnectionsManager connectionsManager;

    private ConnectionsManager(){
    }

    public static ConnectionsManager getConnectionsManager() {
        if(connectionsManager == null) {
            connectionsManager = new ConnectionsManager();
        }
        return connectionsManager;
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DATABASE_URL, POSTGRES_USER, POSTGRES_PASSWORD);

        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}
