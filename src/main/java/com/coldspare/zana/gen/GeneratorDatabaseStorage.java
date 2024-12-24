package com.coldspare.zana.gen;

import com.coldspare.zana.Database;
import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GeneratorDatabaseStorage {
    private final Database database;

    public GeneratorDatabaseStorage(Database database) {
        this.database = database;
    }

    public CompletableFuture<Void> saveGenerators(List<Generator> generators) {
        return CompletableFuture.runAsync(() -> {
            Connection conn = null;
            String sql = "INSERT INTO generators (uuid, slot, type, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE slot = VALUES(slot), type = VALUES(type), world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z)";
            try {
                conn = database.getConnection();
                conn.setAutoCommit(false); // Start the transaction
                PreparedStatement stmt = conn.prepareStatement(sql);
                for (Generator generator : generators) {
                    World world = generator.getLocation().getWorld();
                    if (world == null) continue; // Skip saving generator if its world is null
                    stmt.setString(1, generator.getOwnerId().toString());
                    stmt.setInt(2, generator.getSlot());
                    stmt.setString(3, generator.getType().name());
                    stmt.setString(4, world.getName());
                    stmt.setInt(5, generator.getLocation().getBlockX());
                    stmt.setInt(6, generator.getLocation().getBlockY());
                    stmt.setInt(7, generator.getLocation().getBlockZ());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit(); // Commit the transaction
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback(); // If anything went wrong, roll back the transaction
                    } catch (SQLException rollbackException) {
                        rollbackException.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close(); // Always close the connection
                    } catch (SQLException closeException) {
                        closeException.printStackTrace();
                    }
                }
            }
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }

    public void saveGenerators1(List<Generator> generators) {
        try (Connection conn = database.getConnection()) {
            String sql = "INSERT INTO generators (uuid, slot, type, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE slot = VALUES(slot), type = VALUES(type), world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z)";
            conn.setAutoCommit(false); // Start the transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Generator generator : generators) {
                    World world = generator.getLocation().getWorld();
                    if (world == null) continue; // Skip saving generator if its world is null
                    stmt.setString(1, generator.getOwnerId().toString());
                    stmt.setInt(2, generator.getSlot());
                    stmt.setString(3, generator.getType().name());
                    stmt.setString(4, world.getName());
                    stmt.setInt(5, generator.getLocation().getBlockX());
                    stmt.setInt(6, generator.getLocation().getBlockY());
                    stmt.setInt(7, generator.getLocation().getBlockZ());
                    stmt.addBatch();
                }

                stmt.executeBatch();
                conn.commit(); // Commit the transaction
            } catch (SQLException e) {
                conn.rollback(); // If anything went wrong, roll back the transaction
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> saveGeneratorSlots(Map<UUID, Integer> slots) {
        return CompletableFuture.runAsync(() -> {
            String sql = "REPLACE INTO generator_slots (uuid, slot) VALUES (?, ?)";
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                conn.setAutoCommit(false); // Start the transaction
                for (Map.Entry<UUID, Integer> entry : slots.entrySet()) {
                    stmt.setString(1, entry.getKey().toString());
                    stmt.setInt(2, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit(); // Commit the transaction
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }

    public CompletableFuture<Map<UUID, Integer>> loadGeneratorSlots() {
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, Integer> slots = new HashMap<>();
            String sql = "SELECT * FROM generator_slots";
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    slots.put(UUID.fromString(rs.getString("uuid")), rs.getInt("slot"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return slots;
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }

    public CompletableFuture<List<Generator>> loadGeneratorsForOwner(UUID ownerId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM generators WHERE uuid = ?";
            List<Generator> generators = new ArrayList<>();
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ownerId.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int slot = rs.getInt("slot");
                    String type = rs.getString("type");
                    String worldName = rs.getString("world");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    World world = Bukkit.getServer().getWorld(worldName);
                    Location location = new Location(world, x, y, z);
                    Generator generator = new Generator(ownerId, GeneratorType.valueOf(type), location, slot);
                    generators.add(generator);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return generators;
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }

    public CompletableFuture<List<Generator>> loadGeneratorsForOwner1(UUID ownerId) {
        CompletableFuture<List<Generator>> future = new CompletableFuture<>();
        try {
            String sql = "SELECT * FROM generators WHERE uuid = ?";
            List<Generator> generators = new ArrayList<>();
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ownerId.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                int slot = rs.getInt("slot");
                String type = rs.getString("type");
                String worldName = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                World world = Bukkit.getServer().getWorld(worldName);
                Location location = new Location(world, x, y, z);
                Generator generator = new Generator(ownerId, GeneratorType.valueOf(type), location, slot);
                generators.add(generator);
                }
            }
            future.complete(generators);
        } catch (SQLException e) {
            future.completeExceptionally(e);
            e.printStackTrace();
        }
        return future;
    }

    public CompletableFuture<Void> removeGeneratorsForOwner(UUID ownerId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM generators WHERE uuid = ?";
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ownerId.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }

    public CompletableFuture<Void> removeGenerator(Generator generator) {
        String deleteSql = "DELETE FROM generators WHERE x = ? AND y = ? AND z = ? AND world = ? AND uuid = ?";

        return database.getConnectionAsync().thenComposeAsync(conn -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            try {
                conn.setAutoCommit(false); // Start the transaction
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, generator.getLocation().getBlockX());
                    deleteStmt.setInt(2, generator.getLocation().getBlockY());
                    deleteStmt.setInt(3, generator.getLocation().getBlockZ());
                    deleteStmt.setString(4, generator.getLocation().getWorld().getName());
                    deleteStmt.setString(5, generator.getOwnerId().toString());
                    int affectedRows = deleteStmt.executeUpdate();
                    if (affectedRows == 0) {
                        Bukkit.getLogger().info("Generator: " + generator.getOwnerId().toString() + " at location " + generator.getLocation().toString() + " does not exist in the database.");
                    }
                    conn.commit(); // Commit the transaction if successful
                    //Bukkit.getLogger().info("Successfully committed the transaction: removeGenerator");
                } catch (SQLException e) {
                    conn.rollback(); // If anything went wrong, roll back the transaction
                    Bukkit.getLogger().severe("Failed to delete generator: " + generator.getOwnerId().toString() + " at location " + generator.getLocation().toString());
                    e.printStackTrace();
                }
                future.complete(null);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
            return future;
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), r));
    }
}