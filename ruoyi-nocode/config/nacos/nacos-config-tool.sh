#!/bin/bash

# Nacos Configuration Management Script
# 用于配置导出、导入、备份和比较

NACOS_HOST=${NACOS_HOST:-127.0.0.1}
NACOS_PORT=${NACOS_PORT:-8848}
NACOS_USERNAME=${NACOS_USERNAME:-nacos}
NACOS_PASSWORD=${NACOS_PASSWORD:-nacos}
EXPORT_DIR=${EXPORT_DIR:-./nacos-export}
BACKUP_DIR=${BACKUP_DIR:-./nacos-backup}

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 导出所有配置
export_all_configs() {
    local namespace=$1
    local group=$2
    local output_file=$3

    log_info "Exporting configs: namespace=$namespace, group=$group"

    curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=&group=$group&namespaceId=$namespace&pageNo=1&pageSize=1000" \
        | jq '.' > "$output_file" 2>/dev/null || {
            log_error "Failed to export configs"
            return 1
        }

    log_info "Exported to $output_file"
}

# 导出单个配置
export_config() {
    local data_id=$1
    local group=$2
    local namespace=$3
    local output_file=$4

    log_info "Exporting config: dataId=$data_id, group=$group, namespace=$namespace"

    local content=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=$data_id&group=$group&namespaceId=$namespace")

    echo "$content" > "$output_file"
    log_info "Exported to $output_file"
}

# 导入配置
import_config() {
    local data_id=$1
    local group=$2
    local namespace=$3
    local content_file=$4

    local content=$(cat "$content_file")

    curl -s -X POST -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs" \
        -d "dataId=$data_id" \
        -d "group=$group" \
        -d "namespaceId=$namespace" \
        -d "content=$content" \
        -d "type=yaml"

    if [ $? -eq 0 ]; then
        log_info "Imported: $data_id"
    else
        log_error "Failed to import: $data_id"
    fi
}

# 批量导入配置
batch_import() {
    local import_dir=$1
    local group=$2
    local namespace=$3

    log_info "Batch importing from $import_dir"

    for file in "$import_dir"/*.yml "$import_dir"/*.yaml "$import_dir"/*.properties; do
        if [ -f "$file" ]; then
            local data_id=$(basename "$file")
            import_config "$data_id" "$group" "$namespace" "$file"
        fi
    done
}

# 备份所有配置
backup_all() {
    local backup_name="backup-$(date +%Y%m%d-%H%M%S)"
    local backup_path="$BACKUP_DIR/$backup_name"

    mkdir -p "$backup_path"

    log_info "Creating backup: $backup_name"

    # 导出所有命名空间
    for namespace in public dev test pre prod; do
        mkdir -p "$backup_path/$namespace"
        for group in DEFAULT_GROUP MICROSERVICE_GROUP GATEWAY_GROUP INFRA_GROUP BUSINESS_GROUP; do
            export_all_configs "$namespace" "$group" "$backup_path/$namespace/$group.json"
        done
    done

    log_info "Backup created at $backup_path"
    echo "$backup_path"
}

# 列出所有配置
list_configs() {
    local namespace=${1:-public}
    local group=${2:-DEFAULT_GROUP}

    curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=&group=$group&namespaceId=$namespace&pageNo=1&pageSize=1000" \
        | jq -r '.pageItems[] | "\(.dataId)\t\(.group)\t\(.type)\t\(.lastModifiedTime)"'
}

# 比较两个配置版本
diff_config() {
    local data_id=$1
    local group=$2
    local namespace=$3

    local content1=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=$data_id&group=$group&namespaceId=$namespace")

    local history_id=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/history/configs?dataId=$data_id&group=$group&namespaceId=$namespace&pageNo=1&pageSize=1" \
        | jq -r '.pageItems[0].id')

    if [ -n "$history_id" ]; then
        local old_content=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
            "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/history/configs?dataId=$data_id&group=$group&namespaceId=$namespace&id=$history_id" \
            | jq -r '.pageItems[0].content')

        echo "=== Current Version ==="
        echo "$content1"
        echo ""
        echo "=== Previous Version ==="
        echo "$old_content"
    fi
}

# 监听配置变化
watch_config() {
    local data_id=$1
    local group=$2
    local namespace=$3

    log_info "Watching config: $data_id (Press Ctrl+C to stop)"

    while true; do
        response=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
            "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=$data_id&group=$group&namespaceId=$namespace")

        md5=$(curl -s -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
            "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/config?dataId=$data_id&group=$group&namespaceId=$namespace" \
            | md5sum | cut -d' ' -f1)

        echo "[$(date '+%Y-%m-%d %H:%M:%S')] MD5: $md5"

        sleep 5
    done
}

# 发布配置
publish_config() {
    local data_id=$1
    local group=$2
    local namespace=$3
    local content=$4
    local type=${5:-yaml}

    curl -s -X POST -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs" \
        -d "dataId=$data_id" \
        -d "group=$group" \
        -d "namespaceId=$namespace" \
        -d "content=$content" \
        -d "type=$type"

    log_info "Published: $data_id"
}

# 删除配置
delete_config() {
    local data_id=$1
    local group=$2
    local namespace=$3

    curl -s -X DELETE -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?dataId=$data_id&group=$group&namespaceId=$namespace"

    log_info "Deleted: $data_id"
}

# 显示使用帮助
show_help() {
    echo "Nacos Configuration Management Tool"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  export-all <namespace> <group> <output>   Export all configs in a group"
    echo "  export <dataId> <group> <namespace> <output>  Export single config"
    echo "  import <dataId> <group> <namespace> <file>     Import config from file"
    echo "  batch-import <dir> <group> <namespace>        Batch import from directory"
    echo "  backup                                     Create full backup"
    echo "  list [namespace] [group]                     List all configs"
    echo "  diff <dataId> <group> <namespace>           Compare current with previous"
    echo "  watch <dataId> <group> <namespace>          Watch config changes"
    echo "  publish <dataId> <group> <namespace> <content> [type]  Publish config"
    echo "  delete <dataId> <group> <namespace>          Delete config"
    echo ""
    echo "Environment Variables:"
    echo "  NACOS_HOST      Nacos server host (default: 127.0.0.1)"
    echo "  NACOS_PORT      Nacos server port (default: 8848)"
    echo "  NACOS_USERNAME  Nacos username (default: nacos)"
    echo "  NACOS_PASSWORD  Nacos password (default: nacos)"
    echo "  EXPORT_DIR      Export directory"
    echo "  BACKUP_DIR      Backup directory"
}

# 主逻辑
case "$1" in
    export-all)
        export_all_configs "$2" "$3" "$4"
        ;;
    export)
        export_config "$2" "$3" "$4" "$5"
        ;;
    import)
        import_config "$2" "$3" "$4" "$5"
        ;;
    batch-import)
        batch_import "$2" "$3" "$4"
        ;;
    backup)
        backup_all
        ;;
    list)
        list_configs "$2" "$3"
        ;;
    diff)
        diff_config "$2" "$3" "$4"
        ;;
    watch)
        watch_config "$2" "$3" "$4"
        ;;
    publish)
        publish_config "$2" "$3" "$4" "$5" "$6"
        ;;
    delete)
        delete_config "$2" "$3" "$4"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        show_help
        ;;
esac
