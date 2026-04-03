#!/usr/bin/env python3
"""
Auto Dev Runner - 自动开发启动器
一键启动auto-dev所有组件

用法:
  python start_dev.py                    # 交互模式
  python start_dev.py check             # 检查状态
  python start_dev.py learn "<text>"    # 快速学习
  python start_dev.py reflect <stage>   # 快速反思
  python start_dev.py stats             # 查看统计
  python start_dev.py init               # 初始化
"""

import sys
import os
from pathlib import Path

# 配置
AUTO_DEV_BASE = Path(__file__).parent.parent


def print_banner():
    print("""
========================================
   AUTO-DEV 自动开发系统 v2.0
   Skill + Script 架构
   越用越聪明 - 自我改进系统
========================================
""")


def run_check():
    """检查系统状态"""
    print("\n[1] 检查 Scripts...")
    scripts_dir = AUTO_DEV_BASE / "scripts"
    scripts = [
        "memory_ops.py",
        "task_ops.py",
        "pipeline_ops.py",
        "orchestrator_ops.py",
        "knowledge_sync_ops.py",
        "knowledge_refine_ops.py",
        "daily_routine_ops.py",
        "self_improve_ops.py",
        "auto_learn_hook.py",
        "auto_reflect_hook.py",
        "auto_archive_hook.py",
        "auto_heartbeat.py",
    ]

    for s in scripts:
        path = scripts_dir / s
        if path.exists():
            print(f"  [OK] {s}")
        else:
            print(f"  [MISSING] {s}")

    print("\n[2] 检查 Self-Improving...")
    self_improve = AUTO_DEV_BASE / "self-improving"
    for f in ["memory.md", "corrections.md", "index.md", "heartbeat-state.md"]:
        path = self_improve / f
        if path.exists():
            print(f"  [OK] {f}")
        else:
            print(f"  [MISSING] {f}")

    print("\n[3] 检查 Memory...")
    memory = AUTO_DEV_BASE / "memory"
    layers = ["episodic", "working", "longterm"]
    for layer in layers:
        path = memory / layer
        if path.exists():
            print(f"  [OK] {layer}/")
        else:
            print(f"  [MISSING] {layer}/")

    print("\n[4] 检查 Config...")
    config = AUTO_DEV_BASE / "config" / "auto-dev.yaml"
    if config.exists():
        print(f"  [OK] config/auto-dev.yaml")
    else:
        print(f"  [MISSING] config/auto-dev.yaml")


def run_stats():
    """查看统计"""
    print("\n[STATS] 运行统计...")
    script_path = str(AUTO_DEV_BASE / "scripts" / "self_improve_ops.py")
    os.system(f'{sys.executable} "{script_path}" stats')


def run_learn(text):
    """快速学习"""
    print(f"\n[LEARN] 记录: {text}")
    script_path = str(AUTO_DEV_BASE / "scripts" / "auto_learn_hook.py")
    os.system(f'{sys.executable} "{script_path}" learn "{text}" --context "quick-learn"')


def run_reflect(stage):
    """快速反思"""
    print(f"\n[REFLECT] 反思: {stage}")
    script_path = str(AUTO_DEV_BASE / "scripts" / "auto_reflect_hook.py")
    os.system(f'{sys.executable} "{script_path}" reflect {stage} "quick-reflect"')


def run_init():
    """初始化"""
    print("\n[INIT] 初始化目录...")

    dirs = [
        AUTO_DEV_BASE / "self-improving" / "projects",
        AUTO_DEV_BASE / "self-improving" / "domains",
        AUTO_DEV_BASE / "self-improving" / "archive",
        AUTO_DEV_BASE / "self-improving" / "logs",
        AUTO_DEV_BASE / "memory" / "longterm" / "knowledge" / "tech",
        AUTO_DEV_BASE / "memory" / "longterm" / "knowledge" / "domain",
        AUTO_DEV_BASE / "tasks" / "pipeline",
        AUTO_DEV_BASE / "logs",
    ]

    for d in dirs:
        d.mkdir(parents=True, exist_ok=True)
        print(f"  [OK] {d.relative_to(AUTO_DEV_BASE)}")

    print("\n[INIT] 创建配置文件...")
    config_dir = AUTO_DEV_BASE / "config"
    config_dir.mkdir(exist_ok=True)

    config_file = config_dir / "auto-dev.yaml"
    if not config_file.exists():
        print("  [SKIP] config/auto-dev.yaml already exists")
    else:
        print("  [OK] config/auto-dev.yaml")

    print("\n[INIT] 完成!")


def main():
    print_banner()

    if len(sys.argv) < 2:
        # 交互模式
        print("可用命令:")
        print("  python start_dev.py check        - 检查系统状态")
        print("  python start_dev.py stats        - 查看自我改进统计")
        print("  python start_dev.py learn <text> - 快速学习")
        print("  python start_dev.py reflect <stage> - 快速反思")
        print("  python start_dev.py init         - 初始化目录")
        print()
        run_check()
        run_stats()
        return

    cmd = sys.argv[1]

    if cmd == "check":
        run_check()
    elif cmd == "stats":
        run_stats()
    elif cmd == "learn":
        if len(sys.argv) < 3:
            print("[ERROR] 请提供学习内容: learn <text>")
        else:
            run_learn(sys.argv[2])
    elif cmd == "reflect":
        if len(sys.argv) < 3:
            print("[ERROR] 请提供阶段: reflect <stage>")
        else:
            run_reflect(sys.argv[2])
    elif cmd == "init":
        run_init()
    else:
        print(f"[ERROR] 未知命令: {cmd}")


if __name__ == "__main__":
    main()
