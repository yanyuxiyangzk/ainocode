package com.ruoyi.nocode.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Database Backup Configuration Properties
 */
@Component
@ConfigurationProperties(prefix = "backup")
public class BackupProperties {

    /**
     * Whether backup is enabled
     */
    private boolean enabled = true;

    /**
     * Backup directory
     */
    private String backupDir = "./backups";

    /**
     * Retention days
     */
    private int retentionDays = 30;

    /**
     * Compression method: gzip or none
     */
    private String compression = "gzip";

    /**
     * S3 enabled
     */
    private boolean s3Enabled = false;

    /**
     * S3 bucket name
     */
    private String s3Bucket = "ruoyi-nocode-backups";

    /**
     * S3 prefix
     */
    private String s3Prefix = "postgresql";

    /**
     * S3 endpoint
     */
    private String s3Endpoint = "";

    /**
     * Backup schedule cron expression
     */
    private String schedule = "0 0 2 * * ?"; // Default: 2:00 AM daily

    /**
     * Backup strategy: daily, weekly, monthly
     */
    private String strategy = "daily";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(int retentionDays) {
        this.retentionDays = retentionDays;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public boolean isS3Enabled() {
        return s3Enabled;
    }

    public void setS3Enabled(boolean s3Enabled) {
        this.s3Enabled = s3Enabled;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3Prefix() {
        return s3Prefix;
    }

    public void setS3Prefix(String s3Prefix) {
        this.s3Prefix = s3Prefix;
    }

    public String getS3Endpoint() {
        return s3Endpoint;
    }

    public void setS3Endpoint(String s3Endpoint) {
        this.s3Endpoint = s3Endpoint;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
