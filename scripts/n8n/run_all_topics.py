#!/usr/bin/env python3
"""모든 토픽을 강제 실행하는 스크립트"""
import json, sqlite3, subprocess, sys, time

DB_PATH = "/home/kpros/.n8n/database.sqlite"
WF_ID = "FW6GUTq0AzBXjJQ5"

TOPICS = [
    "보습/연화 원료",
    "유화/계면활성 원료",
    "방부/항산화 원료",
    "기능성 활성 원료",
    "천연/식물 원료",
    "제형/특수 원료",
]

def set_forced_slot(slot):
    """워크플로우의 실행설정 코드에서 슬롯을 강제 지정"""
    db = sqlite3.connect(DB_PATH)
    cur = db.cursor()
    cur.execute("SELECT nodes FROM workflow_entity WHERE id=?", (WF_ID,))
    nodes = json.loads(cur.fetchone()[0])

    for node in nodes:
        if node.get("name") == "⚙️ 실행 설정":
            code = node["parameters"]["jsCode"]
            # Replace slot calculation with forced value
            import re
            # Remove any previous force override
            code = re.sub(r'// FORCE_SLOT_OVERRIDE.*?\n.*?const slot = \d+;.*?\n', '', code)
            # Insert force override before 'const topics'
            force_line = f"// FORCE_SLOT_OVERRIDE\nconst slot = {slot};\n"
            # Comment out the original slot calculation
            code = code.replace(
                "const slot = (dayOfYear * 12 + now.getHours()) % 6;",
                f"// const slot = (dayOfYear * 12 + now.getHours()) % 6;\n{force_line}"
            )
            # If already commented, just update the forced value
            code = re.sub(
                r'// FORCE_SLOT_OVERRIDE\nconst slot = \d+;',
                f'// FORCE_SLOT_OVERRIDE\nconst slot = {slot};',
                code
            )
            node["parameters"]["jsCode"] = code
            break

    cur.execute("UPDATE workflow_entity SET nodes=? WHERE id=?",
                (json.dumps(nodes, ensure_ascii=False), WF_ID))
    db.commit()
    db.close()

def restore_original_slot():
    """원래 슬롯 계산으로 복원"""
    db = sqlite3.connect(DB_PATH)
    cur = db.cursor()
    cur.execute("SELECT nodes FROM workflow_entity WHERE id=?", (WF_ID,))
    nodes = json.loads(cur.fetchone()[0])

    for node in nodes:
        if node.get("name") == "⚙️ 실행 설정":
            code = node["parameters"]["jsCode"]
            import re
            # Remove force override
            code = re.sub(r'// FORCE_SLOT_OVERRIDE\nconst slot = \d+;\n', '', code)
            # Uncomment original
            code = code.replace(
                "// const slot = (dayOfYear * 12 + now.getHours()) % 6;",
                "const slot = (dayOfYear * 12 + now.getHours()) % 6;"
            )
            node["parameters"]["jsCode"] = code
            break

    cur.execute("UPDATE workflow_entity SET nodes=? WHERE id=?",
                (json.dumps(nodes, ensure_ascii=False), WF_ID))
    db.commit()
    db.close()

def run_workflow():
    result = subprocess.run(
        ["n8n", "execute", "--id", WF_ID],
        capture_output=True, text=True, timeout=180
    )
    return "finished" in result.stdout and "true" in result.stdout

# 실행할 슬롯 (이미 수집된 1번 제외)
slots_to_run = [int(s) for s in sys.argv[1:]] if len(sys.argv) > 1 else [0, 2, 3, 4, 5]

print(f"=== {len(slots_to_run)}개 토픽 연속 수집 시작 ===\n")

for i, slot in enumerate(slots_to_run):
    print(f"[{i+1}/{len(slots_to_run)}] 슬롯 {slot}: {TOPICS[slot]}")
    set_forced_slot(slot)
    success = run_workflow()
    status = "✅ 성공" if success else "❌ 실패"
    print(f"  → {status}")
    time.sleep(2)  # 짧은 대기

# 원래대로 복원
print("\n슬롯 계산 원래대로 복원 중...")
restore_original_slot()
print("=== 완료 ===")
