package com.coldspare.zana;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final String url, username, password;
    private Connection connection;

    public Database(String host, String dbName, String username, String password) {
        this.url = "jdbc:mysql://" + host + "/" + dbName;
        this.username = username;
        this.password = password;
    }

    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
            createTables();
        }
        return connection;
    }

    private void createTables() throws SQLException {
        String sqlCreateGenerators = "CREATE TABLE IF NOT EXISTS generators "
                + "(uuid VARCHAR(36) NOT NULL, "
                + "slot INT NOT NULL, "
                + "type VARCHAR(255) NOT NULL, "
                + "world VARCHAR(255) NOT NULL, "
                + "x INT NOT NULL, "
                + "y INT NOT NULL, "
                + "z INT NOT NULL, "
                + "PRIMARY KEY (uuid, slot));";

        String sqlCreateGeneratorSlots = "CREATE TABLE IF NOT EXISTS generator_slots "
                + "(uuid VARCHAR(36) NOT NULL, "
                + "slot INT NOT NULL, "
                + "PRIMARY KEY (uuid));";

        try (PreparedStatement stmt = connection.prepareStatement(sqlCreateGenerators)) {
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(sqlCreateGeneratorSlots)) {
            stmt.executeUpdate();
        }
    }

    public CompletableFuture<Connection> getConnectionAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return connect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("url", "user", "password");
    }
}

/*
CREATE TABLE generators (
  uuid VARCHAR(36) NOT NULL,
  slot INT NOT NULL,
  type VARCHAR(255) NOT NULL,
  location VARCHAR(255) NOT NULL,
  PRIMARY KEY (uuid, slot)
);

CREATE TABLE generator_slots (
  uuid VARCHAR(36) NOT NULL,
  slot INT NOT NULL,
  PRIMARY KEY (uuid)
);
 */

