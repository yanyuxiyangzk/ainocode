#!/bin/bash

# PostgreSQL Database Backup Script
# Supports full backup, incremental backup, and backup rotation

set -e

# ============================================
# Configuration
# ============================================

# Database connection
DB_HOST=${DB_HOST:-127.0.0.1}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-cost}
DB_USER=${DB_USER:-ods}
DB_PASSWORD=${DB_PASSWORD:-Test@123}

# Backup settings
BACKUP_DIR=${BACKUP_DIR:-./backups}
RETENTION_DAYS=${RETENTION_DAYS:-30}
COMPRESSION=${COMPRESSION:-gzip}

# S3 settings (optional)
S3_ENABLED=${S3_ENABLED:-false}
S3_BUCKET=${S3_BUCKET:-ruoyi-nocode-backups}
S3_PREFIX=${S3_PREFIX:-postgresql}
S3_ENDPOINT=${S3_ENDPOINT:-}

# ============================================
# Functions
# ============================================

log_info() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [INFO] $1"
}

log_error() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [ERROR] $1" >&2
}

log_warn() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [WARN] $1"
}

# Export PGPASSWORD for psql
export PGPASSWORD="$DB_PASSWORD"

# Create backup directory
create_backup_dir() {
    if [ ! -d "$BACKUP_DIR" ]; then
        mkdir -p "$BACKUP_DIR"
        log_info "Created backup directory: $BACKUP_DIR"
    fi

    # Create subdirectories by date
    DATE_DIR="$BACKUP_DIR/$(date '+%Y%m%d')"
    if [ ! -d "$DATE_DIR" ]; then
        mkdir -p "$DATE_DIR"
    fi
}

# Full backup
full_backup() {
    local backup_name="$1"
    local start_time=$(date +%s)

    log_info "Starting full backup: $backup_name"

    local backup_file="$BACKUP_DIR/$(date '+%Y%m%d')/${backup_name}_full_$(date '+%Y%m%d_%H%M%S').sql"

    # pg_dump with compression
    if [ "$COMPRESSION" = "gzip" ]; then
        backup_file="${backup_file}.gz"
        pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
            --no-owner --no-acl -Fc \
        | gzip > "$backup_file"
    else
        pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
            --no-owner --no-acl -Fc > "$backup_file"
    fi

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))

    local file_size=$(du -h "$backup_file" | cut -f1)

    log_info "Backup completed: $backup_file"
    log_info "Size: $file_size, Duration: ${duration}s"

    # Generate checksum
    local checksum_file="${backup_file}.sha256"
    sha256sum "$backup_file" > "$checksum_file"

    # Upload to S3 if enabled
    if [ "$S3_ENABLED" = "true" ]; then
        upload_to_s3 "$backup_file"
        upload_to_s3 "$checksum_file"
    fi

    # Cleanup old backups
    cleanup_old_backups

    echo "$backup_file"
}

# Schema only backup
schema_backup() {
    local backup_name="$1"
    local start_time=$(date +%s)

    log_info "Starting schema backup: $backup_name"

    local backup_file="$BACKUP_DIR/$(date '+%Y%m%d')/${backup_name}_schema_$(date '+%Y%m%d_%H%M%S').sql"

    if [ "$COMPRESSION" = "gzip" ]; then
        backup_file="${backup_file}.gz"
        pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
            --no-owner --no-acl --schema-only \
        | gzip > "$backup_file"
    else
        pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
            --no-owner --no-acl --schema-only > "$backup_file"
    fi

    log_info "Schema backup completed: $backup_file"

    echo "$backup_file"
}

# Upload to S3
upload_to_s3() {
    local file="$1"
    local s3_path="s3://$S3_BUCKET/$S3_PREFIX/$(basename $file)"

    log_info "Uploading to S3: $s3_path"

    if [ -n "$S3_ENDPOINT" ]; then
        aws --endpoint-url "$S3_ENDPOINT" s3 cp "$file" "$s3_path"
    else
        aws s3 cp "$file" "$s3_path"
    fi

    log_info "Upload completed"
}

# Cleanup old backups
cleanup_old_backups() {
    log_info "Cleaning up backups older than $RETENTION_DAYS days"

    # Local cleanup
    find "$BACKUP_DIR" -type f -name "*.sql*" -mtime +$RETENTION_DAYS -delete
    find "$BACKUP_DIR" -type d -empty -delete

    # S3 cleanup if enabled
    if [ "$S3_ENABLED" = "true" ]; then
        local cutoff_date=$(date -d "$RETENTION_DAYS days ago" '+%Y-%m-%d')
        log_info "Cleaning S3 backups older than $cutoff_date"

        if [ -n "$S3_ENDPOINT" ]; then
            aws --endpoint-url "$S3_ENDPOINT" s3 ls "s3://$S3_BUCKET/$S3_PREFIX/" | \
                while read -r line; do
                    local file_date=$(echo $line | awk '{print $1}')
                    local file_name=$(echo $line | awk '{print $4}')
                    if [[ "$file_date" < "$cutoff_date" ]]; then
                        aws --endpoint-url "$S3_ENDPOINT" s3 rm "s3://$S3_BUCKET/$S3_PREFIX/$file_name"
                        log_info "Deleted S3 file: $file_name"
                    fi
                done
        fi
    fi
}

# List backups
list_backups() {
    log_info "Available backups in $BACKUP_DIR:"
    find "$BACKUP_DIR" -type f -name "*.sql*" -printf "%T+ %p\n" | sort -r | head -20
}

# Show backup status
show_status() {
    echo "=== Backup Status ==="
    echo "Backup Directory: $BACKUP_DIR"
    echo "Retention Days: $RETENTION_DAYS"
    echo "Compression: $COMPRESSION"
    echo "S3 Enabled: $S3_ENABLED"
    echo ""
    echo "=== Recent Backups ==="
    list_backups
    echo ""
    echo "=== Disk Usage ==="
    du -sh "$BACKUP_DIR" 2>/dev/null || echo "No backups found"
}

# ============================================
# Main
# ============================================

case "$1" in
    full)
        create_backup_dir
        full_backup "${DB_NAME}"
        ;;
    schema)
        create_backup_dir
        schema_backup "${DB_NAME}"
        ;;
    list)
        list_backups
        ;;
    status)
        show_status
        ;;
    cleanup)
        cleanup_old_backups
        ;;
    *)
        echo "Usage: $0 {full|schema|list|status|cleanup}"
        echo ""
        echo "Environment Variables:"
        echo "  DB_HOST         Database host (default: 127.0.0.1)"
        echo "  DB_PORT         Database port (default: 5432)"
        echo "  DB_NAME         Database name (default: cost)"
        echo "  DB_USER         Database user (default: ods)"
        echo "  DB_PASSWORD     Database password"
        echo "  BACKUP_DIR      Backup directory"
        echo "  RETENTION_DAYS Days to retain backups (default: 30)"
        echo "  COMPRESSION     Compression method: gzip or none (default: gzip)"
        echo "  S3_ENABLED      Enable S3 upload (default: false)"
        echo "  S3_BUCKET       S3 bucket name"
        echo "  S3_PREFIX       S3 key prefix"
        echo "  S3_ENDPOINT     S3 endpoint (for S3-compatible storage)"
        ;;
esac
