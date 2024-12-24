package com.coldspare.zana;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public class BackupService {

    private static final String BACKUP_FOLDER = "backups";
    private static final int MAX_BACKUP_FILES = 5;

    public static void startBackupTask(Zana plugin, String fileName) {
        File backupDir = new File(plugin.getDataFolder(), BACKUP_FOLDER);
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                createBackup(plugin, fileName);
                cleanOldBackups(plugin, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0L, 20 * 60 * 60); // ticks * sec * min
    }

    private static void createBackup(Zana plugin, String fileName) throws IOException {
        File srcFile = new File(plugin.getDataFolder(), fileName);
        if (!srcFile.exists()) {
            throw new IOException("Source file does not exist.");
        }

        File destFile = new File(plugin.getDataFolder(), BACKUP_FOLDER + "/" + fileName + "-" + System.currentTimeMillis());

        try (InputStream in = new FileInputStream(srcFile);
             OutputStream out = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    private static void cleanOldBackups(Zana plugin, String fileName) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(plugin.getDataFolder().getPath(), BACKUP_FOLDER))) {
            paths
                    .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().startsWith(fileName))
                    .sorted((path1, path2) -> {
                        try {
                            return Files.getLastModifiedTime(path2).compareTo(Files.getLastModifiedTime(path1));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .skip(MAX_BACKUP_FILES)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
