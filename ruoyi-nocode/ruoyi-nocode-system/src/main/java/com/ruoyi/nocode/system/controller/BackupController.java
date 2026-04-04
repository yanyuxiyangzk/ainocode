package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.system.task.DatabaseBackupTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Database Backup Controller
 */
@Tag(name = "Backup API", description = "数据库备份恢复接口")
@RestController
@RequestMapping("/system/backup")
public class BackupController {

    private static final Logger log = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private DatabaseBackupTask databaseBackupTask;

    /**
     * Execute manual backup
     */
    @PostMapping("/backup")
    @Operation(summary = "Execute manual backup", description = "执行手动数据库备份")
    public ResponseEntity<Map<String, Object>> executeBackup() {
        log.info("Manual backup triggered via API");

        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = databaseBackupTask.executeBackup();
            response.put("success", success);
            response.put("message", success ? "Backup completed successfully" : "Backup failed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Backup error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Backup error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get backup status
     */
    @GetMapping("/status")
    @Operation(summary = "Get backup status", description = "获取备份状态信息")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        try {
            DatabaseBackupTask.BackupStatus status = databaseBackupTask.getBackupStatus();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting backup status: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * List available backups
     */
    @GetMapping("/list")
    @Operation(summary = "List backups", description = "列出可用的备份文件")
    public ResponseEntity<Map<String, Object>> listBackups() {
        try {
            DatabaseBackupTask.BackupStatus status = databaseBackupTask.getBackupStatus();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "backupDir", status.getBackupDir(),
                "backupCount", status.getBackupCount(),
                "totalSizeBytes", status.getTotalSizeBytes(),
                "strategy", status.getStrategy()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error listing backups: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
