#!/bin/bash

# PostgreSQL Database Restore Script
# Supports restore from local files or S3

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

# Backup directory
BACKUP_DIR=${BACKUP_DIR:-./backups}

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

# Verify checksum
verify_checksum() {
    local backup_file="$1"

    if [ -f "${backup_file}.sha256" ]; then
        log_info "Verifying checksum..."
        if sha256sum -c "${backup_file}.sha256"; then
            log_info "Checksum verified successfully"
            return 0
        else
            log_error "Checksum verification failed!"
            return 1
        fi
    else
        log_warn "Checksum file not found, skipping verification"
        return 0
    fi
}

# Download from S3
download_from_s3() {
    local backup_file="$1"
    local s3_path="s3://$S3_BUCKET/$S3_PREFIX/$(basename $backup_file)"

    log_info "Downloading from S3: $s3_path"

    local temp_dir="/tmp/restore_$$"
    mkdir -p "$temp_dir"

    if [ -n "$S3_ENDPOINT" ]; then
        aws --endpoint-url "$S3_ENDPOINT" s3 cp "$s3_path" "$temp_dir/"
        aws --endpoint-url "$S3_ENDPOINT" s3 cp "${s3_path}.sha256" "$temp_dir/" 2>/dev/null || true
    else
        aws s3 cp "$s3_path" "$temp_dir/"
        aws s3 cp "${s3_path}.sha256" "$temp_dir/" 2>/dev/null || true
    fi

    echo "$temp_dir/$(basename $backup_file)"
}

# Full restore
full_restore() {
    local backup_file="$1"
    local verify=${2:-true}

    log_info "Starting full restore from: $backup_file"

    # Check if file exists locally or on S3
    if [ ! -f "$backup_file" ]; then
        if [ "$S3_ENABLED" = "true" ]; then
            log_info "File not found locally, downloading from S3..."
            backup_file=$(download_from_s3 "$backup_file")
        else
            log_error "Backup file not found: $backup_file"
            exit 1
        fi
    fi

    # Verify checksum if enabled
    if [ "$verify" = "true" ]; then
        verify_checksum "$backup_file"
    fi

    # Check if database exists, create if not
    if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
        log_info "Database $DB_NAME does not exist, creating..."
        createdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" "$DB_NAME"
    fi

    # Stop application connections
    log_info "Terminating existing connections to database..."
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c \
        "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE datname = '$DB_NAME' AND pid <> pg_backend_pid();" 2>/dev/null || true

    # Restore from backup
    log_info "Restoring database..."

    if [[ "$backup_file" == *.gz ]]; then
        gunzip -c "$backup_file" | pg_restore -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" --clean --if-exists --no-owner --no-acl
    else
        pg_restore -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" --clean --if-exists --no-owner --no-acl "$backup_file"
    fi

    log_info "Restore completed successfully!"

    # Cleanup temp files if downloaded from S3
    if [[ "$backup_file" == /tmp/restore_* ]]; then
        rm -rf "$(dirname "$backup_file")"
    fi
}

# Point-in-time restore (PITR)
pit_restore() {
    local backup_file="$1"
    local target_time="$2"

    log_info "Starting point-in-time restore to: $target_time"

    # PITR requires WAL files - this is a simplified version
    log_warn "Point-in-time restore requires WAL archiving to be enabled"

    full_restore "$backup_file" "true"
}

# List available backups
list_backups() {
    log_info "Available backups in $BACKUP_DIR:"

    if [ "$S3_ENABLED" = "true" ]; then
        log_info "S3 Backups:"
        if [ -n "$S3_ENDPOINT" ]; then
            aws --endpoint-url "$S3_ENDPOINT" s3 ls "s3://$S3_BUCKET/$S3_PREFIX/" 2>/dev/null || log_info "No S3 backups found"
        else
            aws s3 ls "s3://$S3_BUCKET/$S3_PREFIX/" 2>/dev/null || log_info "No S3 backups found"
        fi
    fi

    log_info ""
    log_info "Local Backups:"
    find "$BACKUP_DIR" -type f -name "*.sql*" -printf "%T+ %p\n" | sort -r
}

# Show backup info
show_backup_info() {
    local backup_file="$1"

    log_info "Backup Information: $backup_file"

    if [ ! -f "$backup_file" ]; then
        log_error "Backup file not found"
        exit 1
    fi

    echo "=== File Info ==="
    ls -lh "$backup_file"
    echo ""

    if [ -f "${backup_file}.sha256" ]; then
        echo "=== Checksum ==="
        cat "${backup_file}.sha256"
        echo ""
    fi

    # Show backup contents (for custom format dumps)
    if [[ "$backup_file" == *.dump ]] || [[ "$backup_file" == *.dump.gz ]]; then
        echo "=== Backup Contents ==="
        if [[ "$backup_file" == *.gz ]]; then
            gunzip -c "$backup_file" | pg_restore -l 2>/dev/null | head -50
        else
            pg_restore -l "$backup_file" 2>/dev/null | head -50
        fi
    fi
}

# ============================================
# Main
# ============================================

case "$1" in
    full)
        if [ -z "$2" ]; then
            log_error "Usage: $0 full <backup_file>"
            exit 1
        fi
        full_restore "$2" "${3:-true}"
        ;;
    pitr)
        if [ -z "$2" ] || [ -z "$3" ]; then
            log_error "Usage: $0 pitr <backup_file> <target_time>"
            exit 1
        fi
        pit_restore "$2" "$3"
        ;;
    list)
        list_backups
        ;;
    info)
        if [ -z "$2" ]; then
            log_error "Usage: $0 info <backup_file>"
            exit 1
        fi
        show_backup_info "$2"
        ;;
    *)
        echo "Usage: $0 {full|pitr|list|info} [args]"
        echo ""
        echo "Commands:"
        echo "  full <backup_file> [verify]   Restore full backup (verify=true|false)"
        echo "  pitr <backup_file> <time>    Point-in-time restore"
        echo "  list                          List available backups"
        echo "  info <backup_file>            Show backup information"
        echo ""
        echo "Environment Variables:"
        echo "  DB_HOST         Database host (default: 127.0.0.1)"
        echo "  DB_PORT         Database port (default: 5432)"
        echo "  DB_NAME         Database name (default: cost)"
        echo "  DB_USER         Database user (default: ods)"
        echo "  DB_PASSWORD     Database password"
        echo "  BACKUP_DIR      Backup directory (default: ./backups)"
        echo "  S3_ENABLED      Enable S3 (default: false)"
        echo "  S3_BUCKET       S3 bucket name"
        echo "  S3_PREFIX       S3 key prefix"
        echo "  S3_ENDPOINT     S3 endpoint"
        ;;
esac
