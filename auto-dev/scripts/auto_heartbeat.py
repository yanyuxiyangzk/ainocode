#!/usr/bin/env python3
"""
Auto Heartbeat - 定时心跳守护
每10分钟检查一次，维护自我改进系统

用法:
  python auto_heartbeat.py run [--continuous]
  python auto_heartbeat.py check
"""

import sys
import time
from datetime import datetime
from pathlib import Path

# 配置
AUTO_DEV_BASE = Path(__file__).parent.parent
SELF_IMPROVE_DIR = AUTO_DEV_BASE / "self-improving"
HEARTBEAT_INTERVAL = 600  # 10分钟


class AutoHeartbeat:
    def __init__(self):
        self.self_improve_ops = SELF_IMPROVE_DIR.parent / "scripts" / "self_improve_ops.py"

    def heartbeat(self) -> dict:
        """执行心跳"""
        result = {
            "timestamp": datetime.now().isoformat(),
            "status": "unknown",
            "actions": []
        }

        # 1. 检查 self-improving 目录变更
        try:
            import os
            files_changed = []

            for root, dirs, files in os.walk(str(SELF_IMPROVE_DIR)):
                for f in files:
                    if f.endswith(".md"):
                        filepath = Path(root) / f
                        mtime = datetime.fromtimestamp(filepath.stat().st_mtime)
                        age = (datetime.now() - mtime).total_seconds() / 3600  # 小时
                        if age < HEARTBEAT_INTERVAL / 3600:
                            files_changed.append(str(filepath.relative_to(SELF_IMPROVE_DIR)))

            if files_changed:
                result["status"] = "CHANGES_DETECTED"
                result["changed_files"] = files_changed
                result["actions"].append("refresh_index")
            else:
                result["status"] = "HEARTBEAT_OK"
                result["actions"].append("no_action")

        except Exception as e:
            result["status"] = "ERROR"
            result["error"] = str(e)

        return result

    def update_heartbeat_state(self, result: dict):
        """更新心跳状态"""
        state_file = SELF_IMPROVE_DIR / "heartbeat-state.md"

        now = result["timestamp"]

        content = f"""# Self-Improving Heartbeat State

last_heartbeat_started_at: {now}
last_reviewed_change_at: {now}
last_heartbeat_result: {result['status']}

## Last actions
- {datetime.now().strftime('%Y-%m-%d %H:%M')}: {', '.join(result['actions'])}

## Recent changes
"""

        if "changed_files" in result:
            for f in result["changed_files"]:
                content += f"- {f}\n"
        else:
            content += "- (none)\n"

        with open(state_file, "w", encoding="utf-8") as f:
            f.write(content)

    def run(self, continuous: bool = False):
        """运行心跳"""
        print(f"[HEARTBEAT] Starting heartbeat daemon")
        print(f"[HEARTBEAT] Interval: {HEARTBEAT_INTERVAL}s")

        while True:
            result = self.heartbeat()

            print(f"[{result['timestamp']}] Status: {result['status']}")
            if result["actions"]:
                print(f"  Actions: {', '.join(result['actions'])}")

            self.update_heartbeat_state(result)

            if not continuous:
                break

            time.sleep(HEARTBEAT_INTERVAL)

    def check(self) -> dict:
        """检查当前状态"""
        state_file = SELF_IMPROVE_DIR / "heartbeat-state.md"

        if not state_file.exists():
            print("[INFO] No heartbeat state file found")
            return {}

        with open(state_file, "r", encoding="utf-8") as f:
            content = f.read()

        print("[HEARTBEAT] Current state:")
        print(content)

        return {}


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Auto Heartbeat - 心跳守护")
    subparsers = parser.add_subparsers(dest="command", help="子命令")

    # run命令
    run_parser = subparsers.add_parser("run", help="运行心跳")
    run_parser.add_argument("--continuous", action="store_true", help="持续运行")

    # check命令
    subparsers.add_parser("check", help="检查状态")

    args = parser.parse_args()

    if not args.command:
        parser.print_help()
        return

    heartbeat = AutoHeartbeat()

    if args.command == "run":
        heartbeat.run(continuous=args.continuous)

    elif args.command == "check":
        heartbeat.check()


if __name__ == "__main__":
    main()
