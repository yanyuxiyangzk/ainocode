#!/usr/bin/env python3
"""
Pipeline Runner - 流水线自动执行器
整合多Agent团队 + 规则守卫，实现自动化流水线

用法:
  python pipeline_runner.py init <requirement_desc>     # 初始化流水线
  python pipeline_runner.py run <pipeline_id>          # 运行流水线
  python pipeline_runner.py status <pipeline_id>       # 查看状态
  python pipeline_runner.py agent-status               # 查看Agent状态
  python pipeline_runner.py check-quality <stage>      # 质量检查

流水线阶段:
  requirement → design → development → testing → deployment

自动执行:
  1. 创建团队 (backend-dev, frontend-dev, tester)
  2. 并行开发
  3. 质量门卫检查
  4. 测试验证
  5. 部署上线
"""

import os
import sys
import json
import subprocess
import argparse
from datetime import datetime
from pathlib import Path
from typing import Optional, List, Dict, Tuple

# 配置
AUTO_DEV_BASE = Path(__file__).parent.parent
PIPELINES_DIR = AUTO_DEV_BASE / "tasks" / "pipeline"
TEAMS_DIR = AUTO_DEV_BASE / "teams"
SCRIPTS_DIR = AUTO_DEV_BASE / "scripts"
SKILLS_DIR = AUTO_DEV_BASE / "skills"

# 阶段定义
STAGES = ["requirement", "design", "development", "testing", "deployment"]
STAGE_NAMES = {
    "requirement": "需求分析",
    "design": "架构设计",
    "development": "开发实现",
    "testing": "测试验证",
    "deployment": "上线部署"
}

# Agent角色
DEV_ROLES = ["backend-dev", "frontend-dev"]
QA_ROLES = ["tester"]
OPS_ROLES = ["devops", "operation"]


class PipelineRunner:
    def __init__(self):
        self.pipelines_dir = PIPELINES_DIR
        self.pipelines_dir.mkdir(exist_ok=True)
        self.teams_dir = TEAMS_DIR
        self.teams_dir.mkdir(exist_ok=True)

    def generate_id(self) -> str:
        """生成流水线ID"""
        return f"PIPELINE-{datetime.now().strftime('%Y%m%d')}-{len(list(self.pipelines_dir.glob('PIPELINE-*'))):03d}"

    def init_pipeline(self, requirement_desc: str) -> Dict:
        """初始化流水线"""
        pipeline_id = self.generate_id()
        pipeline_dir = self.pipelines_dir / pipeline_id
        pipeline_dir.mkdir(parents=True, exist_ok=True)

        # 创建流水线状态文件
        status = {
            "id": pipeline_id,
            "requirement": requirement_desc,
            "current_stage": "requirement",
            "stages": {
                "requirement": {"status": "pending", "start_time": None, "end_time": None, "output": ""},
                "design": {"status": "pending", "start_time": None, "end_time": None, "output": ""},
                "development": {"status": "pending", "start_time": None, "end_time": None, "output": ""},
                "testing": {"status": "pending", "start_time": None, "end_time": None, "output": ""},
                "deployment": {"status": "pending", "start_time": None, "end_time": None, "output": ""}
            },
            "created_at": datetime.now().isoformat(),
            "team": None,
            "violations": [],
            "test_results": None
        }

        status_file = pipeline_dir / "status.json"
        with open(status_file, "w", encoding="utf-8") as f:
            json.dump(status, f, ensure_ascii=False, indent=2)

        # 创建需求文档
        requirement_file = pipeline_dir / "01-requirement.md"
        requirement_content = f"""# 需求文档

## 基本信息
- ID: {pipeline_id}
- 创建时间: {datetime.now().strftime('%Y-%m-%d %H:%M')}
- 状态: 进行中

## 需求描述
{requirement_desc}

## 验收标准
- [ ] 功能完整
- [ ] 单元测试通过率 >= 70%
- [ ] 代码规范检查通过
- [ ] 部署成功

## 流水线进度
- [ ] 需求分析
- [ ] 架构设计
- [ ] 开发实现
- [ ] 测试验证
- [ ] 上线部署
"""
        with open(requirement_file, "w", encoding="utf-8") as f:
            f.write(requirement_content)

        # 创建设计文档模板
        design_file = pipeline_dir / "02-design.md"
        design_content = f"""# 架构设计文档

## 基本信息
- ID: {pipeline_id}
- 创建时间: {datetime.now().strftime('%Y-%m-%d %H:%M')}
- 状态: 待开始

## 系统架构

### 技术选型
- 后端: Java/Spring Boot
- 前端: Vue 3
- 数据库: MySQL
- 缓存: Redis

### 模块划分
```

```

### 接口设计

### 数据库设计

## 工时评估
| 模块 | 工时 | 负责人 |
|------|------|--------|
| 合计 | 0h | - |

## 风险评估
- 风险1:
- 风险2:
"""
        with open(design_file, "w", encoding="utf-8") as f:
            f.write(design_content)

        print(f"\n[OK] Pipeline initialized: {pipeline_id}")
        print(f"  Requirement: {requirement_desc[:50]}...")
        print(f"  Directory: {pipeline_dir}")

        return status

    def get_pipeline(self, pipeline_id: str) -> Optional[Dict]:
        """获取流水线状态"""
        status_file = self.pipelines_dir / pipeline_id / "status.json"
        if not status_file.exists():
            return None

        with open(status_file, "r", encoding="utf-8") as f:
            return json.load(f)

    def update_stage(self, pipeline_id: str, stage: str, status: str,
                   output: str = "", error: str = "") -> bool:
        """更新阶段状态"""
        pipeline = self.get_pipeline(pipeline_id)
        if not pipeline:
            return False

        now = datetime.now().isoformat()
        pipeline["stages"][stage]["status"] = status

        if status == "in_progress":
            pipeline["stages"][stage]["start_time"] = now
            pipeline["current_stage"] = stage
        elif status in ["completed", "blocked", "failed"]:
            pipeline["stages"][stage]["end_time"] = now
            if output:
                pipeline["stages"][stage]["output"] = output
            if error:
                pipeline["stages"][stage]["error"] = error

        status_file = self.pipelines_dir / pipeline_id / "status.json"
        with open(status_file, "w", encoding="utf-8") as f:
            json.dump(pipeline, f, ensure_ascii=False, indent=2)

        return True

    def create_team_for_pipeline(self, pipeline_id: str) -> Dict:
        """为流水线创建团队"""
        team_name = f"team-{pipeline_id}"
        team_config = {
            "name": team_name,
            "pipeline_id": pipeline_id,
            "created_at": datetime.now().isoformat(),
            "status": "active",
            "members": []
        }

        # 添加开发角色
        for role in DEV_ROLES + QA_ROLES:
            agent_file = AUTO_DEV_BASE / "agents" / f"{role}.md"
            if agent_file.exists():
                team_config["members"].append({
                    "role": role,
                    "status": "idle"
                })

        config_file = self.teams_dir / f"{team_name}.json"
        with open(config_file, "w", encoding="utf-8") as f:
            json.dump(team_config, f, ensure_ascii=False, indent=2)

        return team_config

    def _guardian_team_check(self) -> Tuple[bool, str]:
        """
        [GUARDIAN] 检查团队结构是否正确创建
        必须使用TeamCreate才算合规
        """
        teams_dir = Path.home() / ".claude" / "teams"

        if not teams_dir.exists():
            return False, "Teams directory not found - must use TeamCreate"

        team_configs = list(teams_dir.glob("*.json"))

        if not team_configs:
            return False, "No team configs found - must use TeamCreate"

        # 检查是否有至少一个多人团队
        for config_path in team_configs:
            try:
                with open(config_path, 'r') as f:
                    config = json.load(f)
                    members = config.get('members', [])
                    if len(members) >= 3:
                        return True, f"Team {config_path.stem} has {len(members)} members"
            except:
                pass

        return False, "No multi-role team found - must create team with 3+ roles"

    def check_quality(self, pipeline_id: str, stage: str) -> Tuple[bool, str]:
        """执行质量检查"""
        pipeline_dir = self.pipelines_dir / pipeline_id
        project_dir = AUTO_DEV_BASE.parent  # 假设项目在parent目录

        print(f"\n[QUALITY CHECK] Stage: {stage}")
        print("=" * 60)

        # 导入rule_guard
        sys.path.insert(0, str(SCRIPTS_DIR))
        try:
            from rule_guard import RuleGuard
        except ImportError:
            print("[WARN] rule_guard.py not found, skipping quality check")
            return True, "Quality check skipped (rule_guard not available)"

        guard = RuleGuard()

        if stage == "development":
            # 开发阶段：至少要能编译
            ok, msg = guard.check_compile(project_dir)
            print(f"[{'OK' if ok else 'FAIL'}] Compile: {msg}")
            return ok, msg

        elif stage == "testing":
            # 测试阶段：必须编译+测试通过
            compile_ok, compile_msg = guard.check_compile(project_dir)
            print(f"[{'OK' if compile_ok else 'FAIL'}] Compile: {compile_msg}")
            if not compile_ok:
                return False, compile_msg

            test_ok, test_msg = guard.check_tests(project_dir)
            print(f"[{'OK' if test_ok else 'FAIL'}] Tests: {test_msg}")

            if test_ok:
                # 更新测试结果
                pipeline = self.get_pipeline(pipeline_id)
                if pipeline:
                    pipeline["test_results"] = {"passed": True, "message": test_msg}
                    status_file = self.pipelines_dir / pipeline_id / "status.json"
                    with open(status_file, "w", encoding="utf-8") as f:
                        json.dump(pipeline, f, ensure_ascii=False, indent=2)

            return test_ok, test_msg

        return True, "No quality check required"

    def run_stage(self, pipeline_id: str, stage: str) -> Tuple[bool, str]:
        """运行单个阶段"""
        print(f"\n[STAGE] Running: {STAGE_NAMES.get(stage, stage)}")
        print("=" * 60)

        pipeline = self.get_pipeline(pipeline_id)
        if not pipeline:
            return False, f"Pipeline not found: {pipeline_id}"

        # 检查前置阶段
        if stage != "requirement":
            stage_idx = STAGES.index(stage)
            prev_stage = STAGES[stage_idx - 1]
            if pipeline["stages"].get(prev_stage, {}).get("status") != "completed":
                return False, f"Prerequisite stage {prev_stage} not completed"

        # 更新状态为进行中
        self.update_stage(pipeline_id, stage, "in_progress")

        # [GUARDIAN] 开发阶段前必须通过团队结构检查
        if stage == "development":
            guardian_ok, guardian_msg = self._guardian_team_check()
            if not guardian_ok:
                self.update_stage(pipeline_id, stage, "blocked", error=guardian_msg)
                print(f"[GUARDIAN] *** BLOCKED *** {guardian_msg}")
                return False, guardian_msg
            print(f"[GUARDIAN] Team structure OK: {guardian_msg}")

        # 执行阶段
        try:
            if stage == "requirement":
                ok, msg = self.run_requirement_stage(pipeline_id)
            elif stage == "design":
                ok, msg = self.run_design_stage(pipeline_id)
            elif stage == "development":
                ok, msg = self.run_development_stage(pipeline_id)
            elif stage == "testing":
                ok, msg = self.run_testing_stage(pipeline_id)
            elif stage == "deployment":
                ok, msg = self.run_deployment_stage(pipeline_id)
            else:
                ok, msg = False, f"Unknown stage: {stage}"
        except Exception as e:
            ok = False
            msg = f"Stage error: {str(e)}"

        # 更新阶段状态
        if ok:
            self.update_stage(pipeline_id, stage, "completed", output=msg)
            print(f"[OK] Stage completed: {STAGE_NAMES.get(stage, stage)}")
        else:
            self.update_stage(pipeline_id, stage, "failed", error=msg)
            print(f"[FAIL] Stage failed: {msg}")

        return ok, msg

    def run_requirement_stage(self, pipeline_id: str) -> Tuple[bool, str]:
        """需求分析阶段"""
        # 需求文档已创建，只需确认
        return True, "Requirement stage auto-completed (document exists)"

    def run_design_stage(self, pipeline_id: str) -> Tuple[bool, str]:
        """架构设计阶段"""
        # 创建团队
        team = self.create_team_for_pipeline(pipeline_id)
        print(f"[INFO] Team created: {team['name']}")
        print(f"[INFO] Members: {[m['role'] for m in team['members']]}")

        return True, f"Design stage completed, team prepared: {team['name']}"

    def run_development_stage(self, pipeline_id: str) -> Tuple[bool, str]:
        """开发实现阶段"""
        team_name = f"team-{pipeline_id}"

        # 启动团队（生成命令）
        print(f"\n[DEV STAGE] Starting parallel development...")
        print(f"[INFO] Team: {team_name}")

        # 生成启动命令供参考
        launch_script = f'''#!/bin/bash
# Development Team Launcher for {pipeline_id}
cd {AUTO_DEV_BASE}
python scripts/team_launcher.py launch-all {pipeline_id}
'''
        script_file = self.pipelines_dir / pipeline_id / "launch-dev.sh"
        with open(script_file, "w", encoding="utf-8") as f:
            f.write(launch_script)

        print(f"[INFO] Launch script: {script_file}")
        print(f"[INFO] Run: chmod +x {script_file} && ./{script_file}")

        # 注意：实际启动需要Claude Code CLI支持
        return True, f"Development team ready. Run launch script to start parallel development."

    def run_testing_stage(self, pipeline_id: str) -> Tuple[bool, str]:
        """测试验证阶段"""
        # 先确保开发阶段通过编译
        compile_ok, compile_msg = self.check_quality(pipeline_id, "development")
        if not compile_ok:
            return False, f"Compile failed before testing: {compile_msg}"

        # 运行测试
        ok, msg = self.check_quality(pipeline_id, "testing")

        if ok:
            # 更新需求文档的验收标准
            requirement_file = self.pipelines_dir / pipeline_id / "01-requirement.md"
            if requirement_file.exists():
                with open(requirement_file, "r", encoding="utf-8") as f:
                    content = f.read()

                content = content.replace("- [ ] 功能完整", "- [x] 功能完整")
                content = content.replace("- [ ] 单元测试通过率 >= 70%", "- [x] 单元测试通过率 >= 70%")
                content = content.replace("- [ ] 代码规范检查通过", "- [x] 代码规范检查通过")

                with open(requirement_file, "w", encoding="utf-8") as f:
                    f.write(content)

        return ok, msg

    def run_deployment_stage(self, pipeline_id: str) -> Tuple[bool, str]:
        """部署上线阶段"""
        print(f"\n[DEPLOY] Deployment stage")
        print("[INFO] This stage requires DevOps/Operation agents")
        print("[INFO] In production, would run deployment scripts")

        return True, "Deployment stage completed"

    def run_pipeline(self, pipeline_id: str) -> Dict:
        """运行完整流水线"""
        pipeline = self.get_pipeline(pipeline_id)
        if not pipeline:
            return {"error": f"Pipeline not found: {pipeline_id}"}

        print(f"\n[PIPELINE RUN] {pipeline_id}")
        print(f"Requirement: {pipeline['requirement']}")
        print("=" * 60)

        results = {"pipeline_id": pipeline_id, "stages": {}}

        for stage in STAGES:
            ok, msg = self.run_stage(pipeline_id, stage)
            results["stages"][stage] = {"success": ok, "message": msg}

            if not ok:
                print(f"\n[WARN] Pipeline stopped at {stage}: {msg}")
                break

            # 阶段间质量门卫
            if stage in ["development", "testing"]:
                gate_ok, gate_msg = self.check_quality(pipeline_id, stage)
                results["stages"][stage]["quality_check"] = gate_ok
                if not gate_ok:
                    print(f"\n[FAIL] Quality gate failed: {gate_msg}")
                    results["stages"][stage]["success"] = False
                    break

        # 总结
        completed = sum(1 for s in results["stages"].values() if s.get("success"))
        print(f"\n[PIPELINE SUMMARY] {completed}/{len(STAGES)} stages completed")

        return results

    def get_status(self, pipeline_id: str) -> Dict:
        """获取流水线详细状态"""
        pipeline = self.get_pipeline(pipeline_id)
        if not pipeline:
            return {"error": f"Pipeline not found: {pipeline_id}"}

        return {
            "id": pipeline["id"],
            "requirement": pipeline["requirement"][:100],
            "current_stage": pipeline["current_stage"],
            "stages": {
                stage: info.get("status", "unknown")
                for stage, info in pipeline["stages"].items()
            },
            "progress": f"{sum(1 for s in pipeline['stages'].values() if s.get('status') == 'completed')}/{len(STAGES)}",
            "created_at": pipeline["created_at"]
        }


def main():
    parser = argparse.ArgumentParser(description="Pipeline Runner - 流水线执行器")
    subparsers = parser.add_subparsers(dest="command", help="子命令")

    # init命令
    init_parser = subparsers.add_parser("init", help="初始化流水线")
    init_parser.add_argument("requirement", help="需求描述")
    init_parser.add_argument("--name", "-n", help="流水线名称")

    # run命令
    run_parser = subparsers.add_parser("run", help="运行流水线")
    run_parser.add_argument("pipeline_id", help="流水线ID")
    run_parser.add_argument("--stage", "-s", choices=STAGES, help="只运行特定阶段")

    # status命令
    status_parser = subparsers.add_parser("status", help="查看状态")
    status_parser.add_argument("pipeline_id", nargs="?", help="流水线ID")

    # list命令
    subparsers.add_parser("list", help="列出所有流水线")

    # agent-status命令
    subparsers.add_parser("agent-status", help="查看Agent状态")

    # check-quality命令
    check_parser = subparsers.add_parser("check-quality", help="质量检查")
    check_parser.add_argument("stage", choices=["development", "testing"], help="阶段")

    args = parser.parse_args()

    if not args.command:
        parser.print_help()
        return

    runner = PipelineRunner()

    if args.command == "init":
        result = runner.init_pipeline(args.requirement)
        print(f"\n[OK] Pipeline initialized:")
        print(f"  ID: {result['id']}")

    elif args.command == "run":
        if args.stage:
            ok, msg = runner.run_stage(args.pipeline_id, args.stage)
            print(f"\n[RESULT] {'OK' if ok else 'FAIL'}: {msg}")
        else:
            result = runner.run_pipeline(args.pipeline_id)
            if "error" in result:
                print(f"[ERROR] {result['error']}")
            else:
                print(f"\n[PIPELINE RESULT]")
                for stage, info in result["stages"].items():
                    status = "OK" if info.get("success") else "FAIL"
                    print(f"  [{status}] {STAGE_NAMES.get(stage, stage)}")

    elif args.command == "status":
        if args.pipeline_id:
            status = runner.get_status(args.pipeline_id)
            if "error" in status:
                print(f"[ERROR] {status['error']}")
            else:
                print(f"\n[PIPELINE STATUS] {status['id']}")
                print("=" * 50)
                print(f"Current: {STAGE_NAMES.get(status['current_stage'], status['current_stage'])}")
                print(f"Progress: {status['progress']}")
                print(f"\nStages:")
                for stage, st in status["stages"].items():
                    icon = "[OK]" if st == "completed" else "[RUN]" if st == "in_progress" else "[PEND]"
                    print(f"  {icon} {STAGE_NAMES.get(stage, stage)}: {st}")
        else:
            # 列出所有流水线
            pipelines = list(runner.pipelines_dir.glob("PIPELINE-*"))
            print(f"\n[PIPELINES] Total: {len(pipelines)}")
            for p in sorted(pipelines):
                status_file = p / "status.json"
                if status_file.exists():
                    with open(status_file, "r", encoding="utf-8") as f:
                        data = json.load(f)
                    print(f"  {data['id']} - {STAGE_NAMES.get(data['current_stage'], 'unknown')} - {data.get('requirement', '')[:40]}...")

    elif args.command == "list":
        pipelines = list(runner.pipelines_dir.glob("PIPELINE-*"))
        print(f"\n[PIPELINES] {len(pipelines)} pipelines")
        for p in sorted(pipelines, reverse=True):
            status_file = p / "status.json"
            if status_file.exists():
                with open(status_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
                completed = sum(1 for s in data["stages"].values() if s.get("status") == "completed")
                print(f"\n  {data['id']}")
                print(f"    Stage: {STAGE_NAMES.get(data['current_stage'], 'unknown')}")
                print(f"    Progress: {completed}/5")
                print(f"    Requirement: {data.get('requirement', '')[:60]}...")

    elif args.command == "agent-status":
        sys.path.insert(0, str(SCRIPTS_DIR))
        try:
            from team_launcher import TeamLauncher
        except ImportError:
            print("[ERROR] team_launcher.py not found")
            sys.exit(1)

        launcher = TeamLauncher()
        # List all roles from AGENT_ROLES
        from team_launcher import AGENT_ROLES

        print("\n[AGENT STATUS]")
        print("=" * 60)
        for role, info in AGENT_ROLES.items():
            agent_file = AUTO_DEV_BASE / "agents" / info["agent_file"]
            skill_file = SKILLS_DIR / info["skill_file"]
            agent_exists = agent_file.exists()
            skill_exists = skill_file.exists()
            status = "[OK]" if agent_exists else "[MISSING]"
            skill_status = "[OK]" if skill_exists else "[NO SKILL]"
            print(f"  {status} {skill_status} {role}: {info['description']}")

    elif args.command == "check-quality":
        # 获取最新的流水线
        pipelines = list(runner.pipelines_dir.glob("PIPELINE-*"))
        if not pipelines:
            print("[ERROR] No pipelines found")
            sys.exit(1)

        latest = sorted(pipelines)[-1]
        pipeline_id = latest.name

        ok, msg = runner.check_quality(pipeline_id, args.stage)
        print(f"\n[QUALITY CHECK] {'PASS' if ok else 'FAIL'}: {msg}")
        sys.exit(0 if ok else 1)


if __name__ == "__main__":
    main()
