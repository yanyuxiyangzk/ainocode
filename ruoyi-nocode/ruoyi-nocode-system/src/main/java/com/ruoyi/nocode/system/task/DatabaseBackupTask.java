package com.ruoyi.nocode.system.task;

import com.ruoyi.nocode.system.config.BackupProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled Database Backup Task
 */
@Component
public class DatabaseBackupTask {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackupTask.class);

    @Autowired
    private BackupProperties backupProperties;

    /**
     * Execute full database backup
     */
    public boolean executeBackup() {
        if (!backupProperties.isEnabled()) {
            log.info("Backup is disabled, skipping...");
            return true;
        }

        log.info("Starting scheduled database backup...");
        long startTime = System.currentTimeMillis();

        try {
            // Ensure backup directory exists
            Path backupDir = Paths.get(backupProperties.getBackupDir());
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            // Create date-based subdirectory
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path dateDir = backupDir.resolve(dateStr);
            if (!Files.exists(dateDir)) {
                Files.createDirectories(dateDir);
            }

            // Generate backup filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = "full_backup_" + timestamp + ".sql";
            if ("gzip".equalsIgnoreCase(backupProperties.getCompression())) {
                backupName += ".gz";
            }
            Path backupFile = dateDir.resolve(backupName);

            // Build pg_dump command
            String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "127.0.0.1";
            String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
            String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "cost";
            String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "ods";
            String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "Test@123";

            // Execute pg_dump
            ProcessBuilder pb;
            if ("gzip".equalsIgnoreCase(backupProperties.getCompression())) {
                pb = new ProcessBuilder(
                    "pg_dump",
                    "-h", dbHost,
                    "-p", dbPort,
                    "-U", dbUser,
                    "-d", dbName,
                    "--no-owner",
                    "--no-acl",
                    "-Fc"
                );
            } else {
                pb = new ProcessBuilder(
                    "pg_dump",
                    "-h", dbHost,
                    "-p", dbPort,
                    "-U", dbUser,
                    "-d", dbName,
                    "--no-owner",
                    "--no-acl",
                    "-Fc",
                    "-f", backupFile.toString()
                );
            }
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.environment().put("PATH", System.getenv("PATH"));

            Process process;
            if ("gzip".equalsIgnoreCase(backupProperties.getCompression())) {
                // Pipe to gzip
                Process pgDump = pb.start();
                pb = new ProcessBuilder(
                    "gzip",
                    "-c"
                );
                Process gzip = pb.start();

                // Write pg_dump output to gzip input
                try {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = pgDump.getInputStream().read(buffer)) > 0) {
                        gzip.getOutputStream().write(buffer, 0, len);
                    }
                    gzip.getOutputStream().close();

                    // Write gzip output to file
                    byte[] gzipBuffer = new byte[8192];
                    try (var fos = Files.newOutputStream(backupFile)) {
                        while ((len = gzip.getInputStream().read(gzipBuffer)) > 0) {
                            fos.write(gzipBuffer, 0, len);
                        }
                    }

                    int exitCode = pgDump.waitFor();
                    if (exitCode != 0) {
                        log.error("pg_dump failed with exit code: {}", exitCode);
                        return false;
                    }
                    gzip.waitFor();
                } catch (Exception e) {
                    log.error("Error during backup: {}", e.getMessage());
                    return false;
                }
            } else {
                process = pb.start();
                boolean finished = process.waitFor(30, TimeUnit.MINUTES);
                if (!finished) {
                    process.destroyForcibly();
                    log.error("Backup timed out");
                    return false;
                }
                if (process.exitValue() != 0) {
                    log.error("Backup failed with exit code: {}", process.exitValue());
                    return false;
                }
            }

            // Generate checksum
            Path checksumFile = Paths.get(backupFile.toString() + ".sha256");
            ProcessBuilder checksumPb = new ProcessBuilder(
                "sha256sum",
                backupFile.toString()
            );
            Process checksumProcess = checksumPb.start();
            String checksumOutput;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(checksumProcess.getInputStream()))) {
                checksumOutput = reader.readLine();
            }
            Files.writeString(checksumFile, checksumOutput);

            // Upload to S3 if enabled
            if (backupProperties.isS3Enabled()) {
                uploadToS3(backupFile);
                uploadToS3(checksumFile);
            }

            // Cleanup old backups
            cleanupOldBackups();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Backup completed successfully in {}ms: {}", duration, backupFile);

            return true;

        } catch (Exception e) {
            log.error("Backup failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Upload file to S3
     */
    private void uploadToS3(Path file) {
        try {
            String s3Path = "s3://" + backupProperties.getS3Bucket() + "/" +
                           backupProperties.getS3Prefix() + "/" + file.getFileName();

            ProcessBuilder pb;
            if (backupProperties.getS3Endpoint() != null && !backupProperties.getS3Endpoint().isEmpty()) {
                pb = new ProcessBuilder(
                    "aws", "--endpoint-url", backupProperties.getS3Endpoint(),
                    "s3", "cp", file.toString(), s3Path
                );
            } else {
                pb = new ProcessBuilder(
                    "aws", "s3", "cp", file.toString(), s3Path
                );
            }
            pb.environment().put("PATH", System.getenv("PATH"));

            Process process = pb.start();
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                log.error("S3 upload timed out for: {}", file);
                return;
            }
            if (process.exitValue() == 0) {
                log.info("Uploaded to S3: {}", s3Path);
            } else {
                log.error("S3 upload failed for: {}", file);
            }
        } catch (Exception e) {
            log.error("Error uploading to S3: {}", e.getMessage());
        }
    }

    /**
     * Cleanup old backups based on retention policy
     */
    private void cleanupOldBackups() {
        try {
            Path backupDir = Paths.get(backupProperties.getBackupDir());
            if (!Files.exists(backupDir)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() -
                             (long) backupProperties.getRetentionDays() * 24 * 60 * 60 * 1000;

            Files.walk(backupDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".sql") || p.toString().endsWith(".sql.gz"))
                .filter(p -> {
                    try {
                        return Files.getLastModifiedTime(p).toMillis() < cutoffTime;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .forEach(p -> {
                    try {
                        Files.delete(p);
                        log.info("Deleted old backup: {}", p);
                    } catch (Exception e) {
                        log.error("Failed to delete old backup: {}", p);
                    }
                });

            // Delete empty directories
            Files.walk(backupDir)
                .filter(Files::isDirectory)
                .filter(p -> !Files.list(p).findAny().isPresent())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (Exception e) {
                        // Ignore
                    }
                });

        } catch (Exception e) {
            log.error("Error cleaning up old backups: {}", e.getMessage());
        }
    }

    /**
     * Scheduled backup - runs based on strategy
     * Default: daily at 2:00 AM
     */
    @Scheduled(cron = "${backup.schedule:0 0 2 * * ?}")
    public void scheduledBackup() {
        String strategy = backupProperties.getStrategy();
        LocalDate today = LocalDate.now();

        boolean shouldRun = false;
        switch (strategy.toLowerCase()) {
            case "daily":
                shouldRun = true;
                break;
            case "weekly":
                shouldRun = today.getDayOfWeek().getValue() == 1; // Monday
                break;
            case "monthly":
                shouldRun = today.getDayOfMonth() == 1; // First day of month
                break;
            default:
                shouldRun = true;
        }

        if (shouldRun) {
            log.info("Running {} backup task", strategy);
            executeBackup();
        } else {
            log.debug("Skipping backup - strategy is {} and today is {}", strategy, today);
        }
    }

    /**
     * Get backup status information
     */
    public BackupStatus getBackupStatus() {
        BackupStatus status = new BackupStatus();
        status.setEnabled(backupProperties.isEnabled());
        status.setBackupDir(backupProperties.getBackupDir());
        status.setRetentionDays(backupProperties.getRetentionDays());
        status.setCompression(backupProperties.getCompression());
        status.setS3Enabled(backupProperties.isS3Enabled());
        status.setStrategy(backupProperties.getStrategy());
        status.setSchedule(backupProperties.getSchedule());

        // Count existing backups
        try {
            Path backupDir = Paths.get(backupProperties.getBackupDir());
            if (Files.exists(backupDir)) {
                long count = Files.walk(backupDir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".sql") || p.toString().endsWith(".sql.gz"))
                    .count();
                status.setBackupCount(count);

                // Calculate total size
                long size = Files.walk(backupDir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".sql") || p.toString().endsWith(".sql.gz"))
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (Exception e) {
                            return 0L;
                        }
                    })
                    .sum();
                status.setTotalSizeBytes(size);
            }
        } catch (Exception e) {
            log.error("Error getting backup status: {}", e.getMessage());
        }

        return status;
    }

    /**
     * Backup status DTO
     */
    public static class BackupStatus {
        private boolean enabled;
        private String backupDir;
        private int retentionDays;
        private String compression;
        private boolean s3Enabled;
        private String strategy;
        private String schedule;
        private long backupCount;
        private long totalSizeBytes;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getBackupDir() { return backupDir; }
        public void setBackupDir(String backupDir) { this.backupDir = backupDir; }
        public int getRetentionDays() { return retentionDays; }
        public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
        public String getCompression() { return compression; }
        public void setCompression(String compression) { this.compression = compression; }
        public boolean isS3Enabled() { return s3Enabled; }
        public void setS3Enabled(boolean s3Enabled) { this.s3Enabled = s3Enabled; }
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        public long getBackupCount() { return backupCount; }
        public void setBackupCount(long backupCount) { this.backupCount = backupCount; }
        public long getTotalSizeBytes() { return totalSizeBytes; }
        public void setTotalSizeBytes(long totalSizeBytes) { this.totalSizeBytes = totalSizeBytes; }
    }
}
