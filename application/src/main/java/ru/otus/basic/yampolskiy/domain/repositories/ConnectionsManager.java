package ru.otus.basic.yampolskiy.domain.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionsManager {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/UserService";
    private static final String POSTGRES_USER = "User";
    private static final String POSTGRES_PASSWORD = "Password";
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
