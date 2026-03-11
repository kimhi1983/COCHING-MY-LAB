#!/usr/bin/env python3
"""
Track 1: 6개 카테고리 워크플로우 — 중복 방지 + 스마트 타겟 선택
Track 2: 제품카피 가이드 워크플로우 6개 생성

핵심 변경:
- "생성 대상 선정" Code 노드 제거
- "🔍 미생성 대상 찾기" PostgreSQL 노드가 직접 DB에서 미생성 조합 조회
- "⚙️ 대상 설정" Code 노드가 힌트/메타 추가
"""
import sqlite3, json, uuid, copy
from datetime import datetime

db = sqlite3.connect("/home/kpros/.n8n/database.sqlite")

# ===== 자격증명 ID =====
PG_CRED_ID = "2c8e9119-0bb0-49f2-ad43-f040ab4a7a64"
PG_CRED_NAME = "COCHING PostgreSQL (Local)"
SSH_CRED_ID = "uK4wQlmywxsEwHyU"
SSH_CRED_NAME = "Claude Code WSL2 SSH"

now = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S.000')

# ===== 카테고리 정의 =====
CATEGORIES = {
    "A": {
        "name": "기초화장",
        "types": ["수분크림","영양크림","에센스","세럼","앰플","토너","스킨","로션","아이크림","넥크림","나이트크림","데이크림"]
    },
    "B": {
        "name": "세정",
        "types": ["폼클렌저","클렌징오일","클렌징워터","바디워시","핸드워시"]
    },
    "C": {
        "name": "선케어",
        "types": ["선크림","선로션","선스프레이","선스틱"]
    },
    "D": {
        "name": "색조",
        "types": ["파운데이션","BB크림","CC크림","컨실러","립스틱","립글로스","립밤","아이섀도"]
    },
    "E": {
        "name": "두발",
        "types": ["샴푸","컨디셔너","헤어트리트먼트","헤어에센스","헤어오일","헤어왁스","두피케어토닉"]
    },
    "F": {
        "name": "기타",
        "types": ["향수","데오도란트","네일케어","베이비로션","베이비오일","치약","마스크팩"]
    }
}

SKIN_TYPES = ["건성","지성","복합성","민감성","중성"]

INGREDIENT_HINTS = {
    "수분크림": "hyaluronic acid,glycerin,ceramide,squalane",
    "영양크림": "shea butter,jojoba oil,vitamin E,peptide",
    "에센스": "niacinamide,hyaluronic acid,centella,galactomyces",
    "세럼": "vitamin C,retinol,peptide,AHA",
    "앰플": "EGF,peptide,ceramide,collagen",
    "토너": "BHA,AHA,witch hazel,centella",
    "스킨": "aloe vera,glycerin,panthenol",
    "로션": "cetyl alcohol,glycerin,dimethicone",
    "아이크림": "retinol,peptide,caffeine,vitamin K",
    "넥크림": "peptide,adenosine,collagen",
    "나이트크림": "retinol,ceramide,peptide,squalane",
    "데이크림": "SPF,niacinamide,vitamin C",
    "폼클렌저": "cocamidopropyl betaine,glycerin,salicylic acid",
    "클렌징오일": "mineral oil,jojoba oil,PEG-20",
    "클렌징워터": "micellar,glycerin,panthenol",
    "바디워시": "SLS,cocamide DEA,glycerin",
    "핸드워시": "triclosan,aloe vera,glycerin",
    "선크림": "zinc oxide,titanium dioxide,octinoxate",
    "선로션": "avobenzone,homosalate,octisalate",
    "선스프레이": "octinoxate,homosalate,alcohol denat",
    "선스틱": "zinc oxide,beeswax,coconut oil",
    "파운데이션": "iron oxide,dimethicone,titanium dioxide",
    "BB크림": "titanium dioxide,niacinamide,SPF",
    "CC크림": "color correcting,niacinamide,SPF",
    "컨실러": "iron oxide,dimethicone,wax",
    "립스틱": "castor oil,beeswax,carnauba wax,iron oxide",
    "립글로스": "polybutene,castor oil,mica",
    "립밤": "beeswax,shea butter,vitamin E",
    "아이섀도": "mica,iron oxide,talc,dimethicone",
    "샴푸": "sodium laureth sulfate,cocamidopropyl betaine",
    "컨디셔너": "cetrimonium chloride,dimethicone,behentrimonium",
    "헤어트리트먼트": "keratin,argan oil,silk amino acid",
    "헤어에센스": "dimethicone,cyclomethicone,argan oil",
    "헤어오일": "argan oil,jojoba oil,camellia oil",
    "헤어왁스": "beeswax,PVP,VP copolymer",
    "두피케어토닉": "salicylic acid,menthol,biotin",
    "향수": "alcohol denat,fragrance,DPG",
    "데오도란트": "aluminum chlorohydrate,triclosan",
    "네일케어": "nitrocellulose,tosylamide",
    "베이비로션": "glycerin,allantoin,panthenol",
    "베이비오일": "mineral oil,vitamin E,chamomile",
    "치약": "sodium fluoride,sorbitol,silica",
    "마스크팩": "hyaluronic acid,collagen,centella"
}


def make_pg_creds():
    return {"postgres": {"id": PG_CRED_ID, "name": PG_CRED_NAME}}

def make_ssh_creds():
    return {"sshPrivateKey": {"id": SSH_CRED_ID, "name": SSH_CRED_NAME}}


def build_track1_workflow(cat_id, cat_info):
    """Track 1: 카테고리 가이드 — 미생성 대상 자동 선택"""
    types = cat_info["types"]
    # VALUES 절 생성
    values_sql = ",".join(f"('{t}')" for t in types)
    skins_sql = ",".join(f"('{s}')" for s in SKIN_TYPES)
    hints_map = {t: INGREDIENT_HINTS.get(t, t) for t in types}

    nodes = [
        # 1. 스케줄 트리거 (2분)
        {
            "parameters": {"rule": {"interval": [{"field": "minutes", "minutesInterval": 2}]}},
            "type": "n8n-nodes-base.scheduleTrigger",
            "typeVersion": 1.2,
            "name": "⏰ 2분 자동실행",
            "position": [0, 300],
            "id": str(uuid.uuid4())
        },
        # 2. 수동 트리거
        {
            "parameters": {},
            "type": "n8n-nodes-base.manualTrigger",
            "typeVersion": 1,
            "name": "▶ 수동 실행",
            "position": [0, 500],
            "id": str(uuid.uuid4())
        },
        # 3. 미생성 대상 찾기 (PostgreSQL)
        {
            "parameters": {
                "operation": "executeQuery",
                "query": f"""WITH all_combos AS (
  SELECT pt.product_type || '_' || st.skin_type AS combo_key,
         pt.product_type, st.skin_type
  FROM (VALUES {values_sql}) AS pt(product_type)
  CROSS JOIN (VALUES {skins_sql}) AS st(skin_type)
)
SELECT ac.combo_key, ac.product_type, ac.skin_type
FROM all_combos ac
LEFT JOIN guide_cache gc ON gc.combo_key = ac.combo_key
WHERE gc.id IS NULL
ORDER BY ac.combo_key
LIMIT 1""",
                "options": {}
            },
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "🔍 미생성 대상 찾기",
            "position": [300, 400],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 4. 대상 설정 (Code)
        {
            "parameters": {
                "jsCode": f"""const row = $input.first().json;
if (!row.combo_key) {{
  return [{{json: {{done: true, message: '{cat_info["name"]} 카테고리 전체 완료!', category: '{cat_info["name"]}'}}}}];
}}
const HINTS = {json.dumps(hints_map, ensure_ascii=False)};
const skinMap = {json.dumps(dict(zip(SKIN_TYPES, ["dry","oily","combination","sensitive","normal"])), ensure_ascii=False)};
return [{{json: {{
  combo_key: row.combo_key,
  product_type_kr: row.product_type,
  skin_type_kr: row.skin_type,
  skin_type_eng: skinMap[row.skin_type] || row.skin_type,
  ingredient_hints: HINTS[row.product_type] || row.product_type,
  category: '{cat_info["name"]}',
  run_id: 'guide_' + Date.now(),
  timestamp: new Date().toISOString(),
  done: false
}}}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "⚙️ 대상 설정",
            "position": [520, 400],
            "id": str(uuid.uuid4())
        },
        # 5. 완료 체크 (IF)
        {
            "parameters": {
                "conditions": {
                    "options": {"caseSensitive": True, "leftValue": "", "typeValidation": "strict"},
                    "conditions": [{"id": str(uuid.uuid4()), "leftValue": "={{ $json.done }}", "rightValue": "true", "operator": {"type": "boolean", "operation": "equals", "singleValue": True}}],
                    "combinator": "and"
                }
            },
            "type": "n8n-nodes-base.if",
            "typeVersion": 2.2,
            "name": "⚡ 완료?",
            "position": [740, 400],
            "id": str(uuid.uuid4())
        },
        # 6. 원료 SQL 생성 (Code)
        {
            "parameters": {
                "jsCode": """const config = $input.first().json;
const hints = (config.ingredient_hints || '').split(',').map(h => h.trim()).filter(h => h);
const likeConditions = hints.map(h => "im.inci_name ILIKE '%" + h + "%' OR im.korean_name ILIKE '%" + h + "%'").join(' OR ');
return [{json: {
  ...config,
  ingredient_sql: "SELECT inci_name, korean_name, cas_number, description FROM ingredient_master WHERE " + likeConditions + " LIMIT 30",
  regulation_sql: "SELECT inci_name, restriction, max_concentration FROM regulation_cache WHERE " + hints.map(h => "inci_name ILIKE '%" + h + "%'").join(' OR ') + " LIMIT 20",
  reference_sql: "SELECT brand_name, product_name, full_ingredients, ph_value, viscosity_cp FROM product_master WHERE category ILIKE '%" + config.product_type_kr + "%' OR product_type ILIKE '%" + config.product_type_kr + "%' LIMIT 5"
}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🔄 원료 SQL 생성",
            "position": [960, 300],
            "id": str(uuid.uuid4())
        },
        # 7. 원료 DB 조회
        {
            "parameters": {"operation": "executeQuery", "query": "={{ $json.ingredient_sql }}", "options": {}},
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "📦 원료 DB 조회",
            "position": [1180, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 8. 규제 DB 조회
        {
            "parameters": {"operation": "executeQuery", "query": "={{ $('🔄 원료 SQL 생성').first().json.regulation_sql }}", "options": {}},
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "📋 규제 DB 조회",
            "position": [1400, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 9. 참조제품 조회
        {
            "parameters": {"operation": "executeQuery", "query": "={{ $('🔄 원료 SQL 생성').first().json.reference_sql }}", "options": {}},
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "📦 참조제품 조회",
            "position": [1620, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 10. Claude 프롬프트 구성
        {
            "parameters": {
                "jsCode": """const config = $('🔄 원료 SQL 생성').first().json;
const ingredients = $('📦 원료 DB 조회').all().map(i=>i.json);
const regulations = $('📋 규제 DB 조회').all().map(i=>i.json);
const refProducts = $('📦 참조제품 조회').all().map(i=>i.json);
const ingList = ingredients.map(i=> i.inci_name+' ('+(i.korean_name||'-')+') CAS:'+(i.cas_number||'-')).join('\\n');
const regList = regulations.map(r=> r.inci_name+': '+r.restriction+' (최대 '+(r.max_concentration||'미정')+')').join('\\n');
const refList = refProducts.map(p=> p.brand_name+' '+p.product_name+' pH:'+(p.ph_value||'?')+' 점도:'+(p.viscosity_cp||'?')).join('\\n');
const prompt = `당신은 화장품 처방 전문가입니다.
[제품유형] ${config.product_type_kr}
[피부타입] ${config.skin_type_kr} (${config.skin_type_eng})
[DB 원료 후보]
${ingList || '(DB 원료 없음 - 일반 지식 활용)'}
[규제 제한]
${regList || '(규제 정보 없음)'}
[참조 제품]
${refList || '(참조 제품 없음)'}
위 정보를 참고하여 "${config.product_type_kr}" 제품의 완전한 가이드 처방을 JSON으로 생성하세요.
반드시 다음 구조를 지켜주세요:
{"formula_name":"처방명","product_type":"${config.product_type_kr}","skin_type":"${config.skin_type_kr}",
"phases":[{"phase":"A (수상)","temperature":"70-75도","ingredients":[{"inci_name":"INCI명","korean_name":"한글명","wt_percent":숫자,"function":"기능"}]}],
"process_steps":["단계1","단계2"],
"quality_checks":["검사1","검사2"],
"estimated_ph":5.5,"estimated_viscosity_cp":숫자,
"total_ingredients":숫자,"total_wt_percent":100.00,
"notes":"처방 설명"}
중요: 전체 배합비 합계는 반드시 100.00%이어야 합니다. JSON만 출력하세요.`;
const b64 = Buffer.from(prompt,'utf-8').toString('base64');
return [{json:{...config, prompt_b64: b64, prompt_length: prompt.length}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🧠 Claude 프롬프트 구성",
            "position": [1840, 300],
            "id": str(uuid.uuid4())
        },
        # 11. Claude SSH 호출
        {
            "parameters": {
                "command": "={{ 'echo \"' + $json.prompt_b64 + '\" | base64 -d > /tmp/guide_prompt.txt && cat /tmp/guide_prompt.txt | /home/kpros/.npm-global/bin/claude -p 2>/dev/null | head -c 50000' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "🧠 Claude 처방 생성 (SSH)",
            "position": [2060, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 12. 파싱 & 검증
        {
            "parameters": {
                "jsCode": """const input = $input.first().json;
const config = $('🧠 Claude 프롬프트 구성').first().json;
let guide = {};
try {
  const stdout = (input.stdout||'').trim();
  let jsonStr = stdout.replace(/```json\\s*/g,'').replace(/```\\s*/g,'').trim();
  const jsonMatch = jsonStr.match(/\\{[\\s\\S]*\\}/);
  if (jsonMatch) { guide = JSON.parse(jsonMatch[0]); }
  else { guide = JSON.parse(jsonStr); }
} catch(e) {
  guide = {
    product_type:config.product_type_kr, skin_type:config.skin_type_kr,
    formula_name:config.combo_key+' (파싱실패)',
    raw_output:(input.stdout||'').substring(0,3000), parse_error:e.message
  };
}
let totalWt = 0;
if (guide.phases) {
  guide.phases.forEach(ph=>{
    (ph.ingredients||[]).forEach(ing=>{ totalWt += parseFloat(ing.wt_percent)||0; });
  });
}
const guideJson = JSON.stringify(guide);
const guideEscaped = guideJson.replace(/'/g,"''");
const fileB64 = Buffer.from(guideJson,'utf-8').toString('base64');
return [{json:{
  combo_key:config.combo_key,
  product_type_kr:config.product_type_kr, skin_type_kr:config.skin_type_kr,
  guide_data_escaped:guideEscaped,
  formula_name:(guide.formula_name||config.combo_key).replace(/'/g,"''"),
  total_wt_percent:totalWt.toFixed(2),
  wt_valid:Math.abs(totalWt-100)<0.1,
  estimated_ph:guide.estimated_ph||null,
  estimated_viscosity:guide.estimated_viscosity_cp||null,
  has_error:!!guide.parse_error,
  file_b64:fileB64
}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🔄 처방 파싱 & 검증",
            "position": [2280, 300],
            "id": str(uuid.uuid4())
        },
        # 13. DB 저장
        {
            "parameters": {
                "operation": "executeQuery",
                "query": "INSERT INTO guide_cache (combo_key,product_type,skin_type,formula_name,guide_data,total_wt_percent,wt_valid,estimated_ph,estimated_viscosity,source,created_at) VALUES ('{{ $json.combo_key }}','{{ $json.product_type_kr }}','{{ $json.skin_type_kr }}','{{ $json.formula_name }}','{{ $json.guide_data_escaped }}'::jsonb,{{ $json.total_wt_percent }},{{ $json.wt_valid }},{{ $json.estimated_ph || 'NULL' }},{{ $json.estimated_viscosity || 'NULL' }},'claude_local',NOW()) ON CONFLICT (combo_key) DO UPDATE SET guide_data='{{ $json.guide_data_escaped }}'::jsonb, formula_name='{{ $json.formula_name }}', total_wt_percent={{ $json.total_wt_percent }}, wt_valid={{ $json.wt_valid }}, updated_at=NOW(), version=guide_cache.version+1",
                "options": {}
            },
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "💾 가이드 캐시 저장",
            "position": [2500, 200],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds()
        },
        # 14. 학습파일 저장 (SSH)
        {
            "parameters": {
                "command": "={{ 'mkdir -p /home/kpros/COCHING/Coching-Cosmetic-AI-V1/ai-server/data/learning && echo \"' + $json.file_b64 + '\" | base64 -d > /home/kpros/COCHING/Coching-Cosmetic-AI-V1/ai-server/data/learning/' + $json.combo_key + '.json && echo LEARN_OK' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "💾 학습파일 저장 (SSH)",
            "position": [2500, 350],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 15. 백업 저장 (SSH)
        {
            "parameters": {
                "command": "={{ 'mkdir -p /mnt/e/COCHING/backup/formulations && echo \"' + $json.file_b64 + '\" | base64 -d > /mnt/e/COCHING/backup/formulations/' + $json.combo_key + '.json && echo BACKUP_OK' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "💾 백업 저장 (SSH)",
            "position": [2500, 500],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 16. 실행 리포트
        {
            "parameters": {
                "jsCode": f"""const config = $('⚙️ 대상 설정').first().json;
if (config.done) {{
  return [{{json:{{report:'✅ {cat_info["name"]} 카테고리 전체 완료', action:'completed'}}}}];
}}
const result = $('🔄 처방 파싱 & 검증').first().json;
return [{{json:{{
  report:'✅ 가이드 생성 완료',
  combo_key:result.combo_key,
  formula_name:result.formula_name,
  wt_valid:result.wt_valid,
  category:'{cat_info["name"]}',
  cost:'0원 (Claude Code Max)'
}}}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "📊 실행 리포트",
            "position": [2720, 400],
            "id": str(uuid.uuid4())
        }
    ]

    connections = {
        "⏰ 2분 자동실행": {"main": [[{"node": "🔍 미생성 대상 찾기", "type": "main", "index": 0}]]},
        "▶ 수동 실행": {"main": [[{"node": "🔍 미생성 대상 찾기", "type": "main", "index": 0}]]},
        "🔍 미생성 대상 찾기": {"main": [[{"node": "⚙️ 대상 설정", "type": "main", "index": 0}]]},
        "⚙️ 대상 설정": {"main": [[{"node": "⚡ 완료?", "type": "main", "index": 0}]]},
        "⚡ 완료?": {"main": [
            [{"node": "📊 실행 리포트", "type": "main", "index": 0}],  # true = done
            [{"node": "🔄 원료 SQL 생성", "type": "main", "index": 0}]  # false = generate
        ]},
        "🔄 원료 SQL 생성": {"main": [[{"node": "📦 원료 DB 조회", "type": "main", "index": 0}]]},
        "📦 원료 DB 조회": {"main": [[{"node": "📋 규제 DB 조회", "type": "main", "index": 0}]]},
        "📋 규제 DB 조회": {"main": [[{"node": "📦 참조제품 조회", "type": "main", "index": 0}]]},
        "📦 참조제품 조회": {"main": [[{"node": "🧠 Claude 프롬프트 구성", "type": "main", "index": 0}]]},
        "🧠 Claude 프롬프트 구성": {"main": [[{"node": "🧠 Claude 처방 생성 (SSH)", "type": "main", "index": 0}]]},
        "🧠 Claude 처방 생성 (SSH)": {"main": [[{"node": "🔄 처방 파싱 & 검증", "type": "main", "index": 0}]]},
        "🔄 처방 파싱 & 검증": {"main": [[
            {"node": "💾 가이드 캐시 저장", "type": "main", "index": 0},
            {"node": "💾 학습파일 저장 (SSH)", "type": "main", "index": 0},
            {"node": "💾 백업 저장 (SSH)", "type": "main", "index": 0}
        ]]},
        "💾 백업 저장 (SSH)": {"main": [[{"node": "📊 실행 리포트", "type": "main", "index": 0}]]}
    }

    return nodes, connections


def build_track2_workflow(cat_id, cat_info, product_types_sql):
    """Track 2: 제품카피 가이드 워크플로우"""

    nodes = [
        # 1. 스케줄 트리거 (2분)
        {
            "parameters": {"rule": {"interval": [{"field": "minutes", "minutesInterval": 2}]}},
            "type": "n8n-nodes-base.scheduleTrigger",
            "typeVersion": 1.2,
            "name": "⏰ 2분 자동실행",
            "position": [0, 300],
            "id": str(uuid.uuid4())
        },
        # 2. 수동 트리거
        {
            "parameters": {},
            "type": "n8n-nodes-base.manualTrigger",
            "typeVersion": 1,
            "name": "▶ 수동 실행",
            "position": [0, 500],
            "id": str(uuid.uuid4())
        },
        # 3. 미카피 제품 찾기 (PostgreSQL) — t_coos_prod + product_master에서 미처리 제품 조회
        {
            "parameters": {
                "operation": "executeQuery",
                "query": f"""WITH source_products AS (
  SELECT id::text as prod_id, prod_name, company_name,
         detail_json->>'INCIs' as incis_raw,
         json_array_length(detail_json->'INCIs') as inci_count,
         'coching_db' as source
  FROM coching.t_coos_prod
  WHERE json_array_length(detail_json->'INCIs') >= 4
  UNION ALL
  SELECT id::text as prod_id, brand_name || ' ' || product_name as prod_name,
         brand_name as company_name,
         full_ingredients as incis_raw,
         array_length(string_to_array(full_ingredients, ','), 1) as inci_count,
         'product_master' as source
  FROM product_master
  WHERE full_ingredients IS NOT NULL AND length(full_ingredients) > 20
)
SELECT sp.prod_id, sp.prod_name, sp.company_name, sp.incis_raw,
       sp.inci_count, sp.source
FROM source_products sp
LEFT JOIN guide_cache_copy gcc ON gcc.source_product_id = sp.prod_id AND gcc.source = sp.source
WHERE gcc.id IS NULL
ORDER BY sp.inci_count DESC
LIMIT 1""",
                "options": {}
            },
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "🔍 미카피 제품 찾기",
            "position": [300, 400],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 4. 대상 설정
        {
            "parameters": {
                "jsCode": f"""const row = $input.first().json;
if (!row.prod_id) {{
  return [{{json: {{done: true, message: '카피 대상 제품 전체 완료!'}}}}];
}}
// INCI 리스트 파싱
let incis = [];
try {{
  if (row.incis_raw && row.incis_raw.startsWith('[')) {{
    incis = JSON.parse(row.incis_raw);
  }} else if (row.incis_raw) {{
    incis = row.incis_raw.split(',').map(s => s.trim());
  }}
}} catch(e) {{
  incis = row.incis_raw ? row.incis_raw.split(',').map(s => s.trim()) : [];
}}
return [{{json: {{
  prod_id: row.prod_id,
  prod_name: row.prod_name,
  company_name: row.company_name || '',
  incis: incis,
  inci_count: row.inci_count || incis.length,
  source: row.source,
  copy_key: (row.prod_name || '').replace(/[^a-zA-Z0-9가-힣]/g, '_').substring(0, 80),
  run_id: 'copy_' + Date.now(),
  timestamp: new Date().toISOString(),
  done: false
}}}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "⚙️ 대상 설정",
            "position": [520, 400],
            "id": str(uuid.uuid4())
        },
        # 5. 완료 체크
        {
            "parameters": {
                "conditions": {
                    "options": {"caseSensitive": True, "leftValue": "", "typeValidation": "strict"},
                    "conditions": [{"id": str(uuid.uuid4()), "leftValue": "={{ $json.done }}", "rightValue": "true", "operator": {"type": "boolean", "operation": "equals", "singleValue": True}}],
                    "combinator": "and"
                }
            },
            "type": "n8n-nodes-base.if",
            "typeVersion": 2.2,
            "name": "⚡ 완료?",
            "position": [740, 400],
            "id": str(uuid.uuid4())
        },
        # 6. 원료 규제 조회 (Code → SQL 생성)
        {
            "parameters": {
                "jsCode": """const config = $input.first().json;
const incis = config.incis || [];
// 상위 10개 성분으로 규제 조회
const top10 = incis.slice(0, 10);
const likeConditions = top10.map(i => "inci_name ILIKE '%" + i.replace(/'/g,"''") + "%'").join(' OR ');
return [{json: {
  ...config,
  regulation_sql: likeConditions ? "SELECT inci_name, restriction, max_concentration FROM regulation_cache WHERE " + likeConditions + " LIMIT 20" : "SELECT 1 WHERE false",
  inci_list_str: incis.join(', ')
}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🔄 규제 SQL 생성",
            "position": [960, 300],
            "id": str(uuid.uuid4())
        },
        # 7. 규제 DB 조회
        {
            "parameters": {"operation": "executeQuery", "query": "={{ $json.regulation_sql }}", "options": {}},
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "📋 규제 DB 조회",
            "position": [1180, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds(),
            "alwaysOutputData": True
        },
        # 8. Claude 프롬프트 구성
        {
            "parameters": {
                "jsCode": """const config = $('🔄 규제 SQL 생성').first().json;
const regulations = $('📋 규제 DB 조회').all().map(i=>i.json);
const regList = regulations.filter(r=>r.inci_name).map(r=> r.inci_name+': '+r.restriction+' (최대 '+(r.max_concentration||'미정')+')').join('\\n');
const prompt = `당신은 화장품 처방 역설계(reverse formulation) 전문가입니다.

[원본 제품] ${config.prod_name} (${config.company_name})
[전성분 리스트 (배합비 순서)]
${config.inci_list_str}

[규제 제한]
${regList || '(규제 정보 없음)'}

위 전성분을 분석하여, 이 제품의 **카피 처방(copy formulation)**을 역추정하세요.

전성분 표기 규칙:
1. 1% 초과 성분은 배합비 내림차순 표기
2. 1% 이하 성분은 순서 무관
3. 정제수(Water/Aqua)는 보통 60-80%

반드시 다음 JSON 구조로 출력:
{"formula_name":"[카피] ${config.prod_name}",
"original_product":"${config.prod_name}",
"original_company":"${config.company_name}",
"product_type":"역추정",
"phases":[{"phase":"A (수상)","temperature":"70-75도","ingredients":[{"inci_name":"INCI명","korean_name":"한글명","wt_percent":숫자,"function":"기능"}]}],
"process_steps":["단계1","단계2"],
"quality_checks":["검사1"],
"estimated_ph":5.5,"estimated_viscosity_cp":숫자,
"total_ingredients":${config.inci_count},
"total_wt_percent":100.00,
"notes":"역추정 근거 설명",
"confidence":"high/medium/low"}

중요: 전체 배합비 합계는 반드시 100.00%. 모든 전성분을 빠짐없이 포함. JSON만 출력.`;
const b64 = Buffer.from(prompt,'utf-8').toString('base64');
return [{json:{...config, prompt_b64: b64, prompt_length: prompt.length}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🧠 Claude 프롬프트 구성",
            "position": [1400, 300],
            "id": str(uuid.uuid4())
        },
        # 9. Claude SSH 호출
        {
            "parameters": {
                "command": "={{ 'echo \"' + $json.prompt_b64 + '\" | base64 -d > /tmp/copy_prompt.txt && cat /tmp/copy_prompt.txt | /home/kpros/.npm-global/bin/claude -p 2>/dev/null | head -c 50000' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "🧠 Claude 카피처방 생성 (SSH)",
            "position": [1620, 300],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 10. 파싱 & 검증
        {
            "parameters": {
                "jsCode": """const input = $input.first().json;
const config = $('🧠 Claude 프롬프트 구성').first().json;
let guide = {};
try {
  const stdout = (input.stdout||'').trim();
  let jsonStr = stdout.replace(/```json\\s*/g,'').replace(/```\\s*/g,'').trim();
  const jsonMatch = jsonStr.match(/\\{[\\s\\S]*\\}/);
  if (jsonMatch) { guide = JSON.parse(jsonMatch[0]); }
  else { guide = JSON.parse(jsonStr); }
} catch(e) {
  guide = {
    formula_name:'[카피실패] '+config.prod_name,
    original_product:config.prod_name,
    parse_error:e.message, raw_output:(input.stdout||'').substring(0,3000)
  };
}
let totalWt = 0;
if (guide.phases) {
  guide.phases.forEach(ph=>{
    (ph.ingredients||[]).forEach(ing=>{ totalWt += parseFloat(ing.wt_percent)||0; });
  });
}
const guideJson = JSON.stringify(guide);
const guideEscaped = guideJson.replace(/'/g,"''");
const fileB64 = Buffer.from(guideJson,'utf-8').toString('base64');
return [{json:{
  source_product_id: config.prod_id,
  source: config.source,
  prod_name: config.prod_name,
  copy_key: config.copy_key,
  guide_data_escaped: guideEscaped,
  formula_name: (guide.formula_name||'[카피] '+config.prod_name).replace(/'/g,"''"),
  total_wt_percent: totalWt.toFixed(2),
  wt_valid: Math.abs(totalWt-100)<0.1,
  estimated_ph: guide.estimated_ph||null,
  confidence: guide.confidence||'unknown',
  has_error: !!guide.parse_error,
  file_b64: fileB64
}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "🔄 처방 파싱 & 검증",
            "position": [1840, 300],
            "id": str(uuid.uuid4())
        },
        # 11. DB 저장
        {
            "parameters": {
                "operation": "executeQuery",
                "query": "INSERT INTO guide_cache_copy (source_product_id, source, original_product_name, formula_name, guide_data, total_wt_percent, wt_valid, estimated_ph, confidence, created_at) VALUES ('{{ $json.source_product_id }}', '{{ $json.source }}', '{{ $json.prod_name }}', '{{ $json.formula_name }}', '{{ $json.guide_data_escaped }}'::jsonb, {{ $json.total_wt_percent }}, {{ $json.wt_valid }}, {{ $json.estimated_ph || 'NULL' }}, '{{ $json.confidence }}', NOW()) ON CONFLICT (source_product_id, source) DO UPDATE SET guide_data='{{ $json.guide_data_escaped }}'::jsonb, formula_name='{{ $json.formula_name }}', total_wt_percent={{ $json.total_wt_percent }}, wt_valid={{ $json.wt_valid }}, updated_at=NOW(), version=guide_cache_copy.version+1",
                "options": {}
            },
            "type": "n8n-nodes-base.postgres",
            "typeVersion": 2.5,
            "name": "💾 카피가이드 저장",
            "position": [2060, 200],
            "id": str(uuid.uuid4()),
            "credentials": make_pg_creds()
        },
        # 12. 학습파일 저장
        {
            "parameters": {
                "command": "={{ 'mkdir -p /home/kpros/COCHING/Coching-Cosmetic-AI-V1/ai-server/data/learning/copy && echo \"' + $json.file_b64 + '\" | base64 -d > /home/kpros/COCHING/Coching-Cosmetic-AI-V1/ai-server/data/learning/copy/' + $json.copy_key + '.json && echo LEARN_OK' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "💾 학습파일 저장 (SSH)",
            "position": [2060, 350],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 13. 백업 저장
        {
            "parameters": {
                "command": "={{ 'mkdir -p /mnt/e/COCHING/backup/formulations/copy && echo \"' + $json.file_b64 + '\" | base64 -d > /mnt/e/COCHING/backup/formulations/copy/' + $json.copy_key + '.json && echo BACKUP_OK' }}"
            },
            "type": "n8n-nodes-base.ssh",
            "typeVersion": 1,
            "name": "💾 백업 저장 (SSH)",
            "position": [2060, 500],
            "id": str(uuid.uuid4()),
            "credentials": make_ssh_creds()
        },
        # 14. 실행 리포트
        {
            "parameters": {
                "jsCode": """const config = $('⚙️ 대상 설정').first().json;
if (config.done) {
  return [{json:{report:'✅ 제품카피 전체 완료', action:'completed'}}];
}
const result = $('🔄 처방 파싱 & 검증').first().json;
return [{json:{
  report:'✅ 카피가이드 생성 완료',
  product: result.prod_name,
  formula_name: result.formula_name,
  wt_valid: result.wt_valid,
  confidence: result.confidence,
  cost: '0원 (Claude Code Max)'
}}];"""
            },
            "type": "n8n-nodes-base.code",
            "typeVersion": 2,
            "name": "📊 실행 리포트",
            "position": [2280, 400],
            "id": str(uuid.uuid4())
        }
    ]

    connections = {
        "⏰ 2분 자동실행": {"main": [[{"node": "🔍 미카피 제품 찾기", "type": "main", "index": 0}]]},
        "▶ 수동 실행": {"main": [[{"node": "🔍 미카피 제품 찾기", "type": "main", "index": 0}]]},
        "🔍 미카피 제품 찾기": {"main": [[{"node": "⚙️ 대상 설정", "type": "main", "index": 0}]]},
        "⚙️ 대상 설정": {"main": [[{"node": "⚡ 완료?", "type": "main", "index": 0}]]},
        "⚡ 완료?": {"main": [
            [{"node": "📊 실행 리포트", "type": "main", "index": 0}],
            [{"node": "🔄 규제 SQL 생성", "type": "main", "index": 0}]
        ]},
        "🔄 규제 SQL 생성": {"main": [[{"node": "📋 규제 DB 조회", "type": "main", "index": 0}]]},
        "📋 규제 DB 조회": {"main": [[{"node": "🧠 Claude 프롬프트 구성", "type": "main", "index": 0}]]},
        "🧠 Claude 프롬프트 구성": {"main": [[{"node": "🧠 Claude 카피처방 생성 (SSH)", "type": "main", "index": 0}]]},
        "🧠 Claude 카피처방 생성 (SSH)": {"main": [[{"node": "🔄 처방 파싱 & 검증", "type": "main", "index": 0}]]},
        "🔄 처방 파싱 & 검증": {"main": [[
            {"node": "💾 카피가이드 저장", "type": "main", "index": 0},
            {"node": "💾 학습파일 저장 (SSH)", "type": "main", "index": 0},
            {"node": "💾 백업 저장 (SSH)", "type": "main", "index": 0}
        ]]},
        "💾 백업 저장 (SSH)": {"main": [[{"node": "📊 실행 리포트", "type": "main", "index": 0}]]}
    }

    return nodes, connections


# ==========================================
# 실행
# ==========================================

# 기존 v2.2 시리즈 전부 삭제
print("=== 기존 워크플로우 정리 ===")
old_wfs = db.execute("SELECT id, name FROM workflow_entity WHERE name LIKE '%v2.2%'").fetchall()
for wf_id, wf_name in old_wfs:
    db.execute("DELETE FROM shared_workflow WHERE workflowId=?", [wf_id])
    db.execute("DELETE FROM workflow_entity WHERE id=?", [wf_id])
    print(f"  삭제: {wf_name}")

proj = db.execute("SELECT id FROM project LIMIT 1").fetchone()

# Track 1: 카테고리 가이드 6개
print("\n=== Track 1: 카테고리 가이드 생성 ===")
for cat_id, cat_info in CATEGORIES.items():
    wf_name = f"COCHING AI — 가이드 v2.3-{cat_id} {cat_info['name']}"
    wf_id = str(uuid.uuid4()).replace('-', '')[:20]
    version_id = str(uuid.uuid4())

    nodes, connections = build_track1_workflow(cat_id, cat_info)

    db.execute("""
        INSERT INTO workflow_entity
        (id, name, active, nodes, connections, settings, versionId,
         triggerCount, createdAt, updatedAt, isArchived, versionCounter)
        VALUES (?, ?, 1, ?, ?, '{}', ?, 0, ?, ?, 0, 1)
    """, [wf_id, wf_name, json.dumps(nodes), json.dumps(connections), version_id, now, now])

    if proj:
        db.execute("INSERT OR IGNORE INTO shared_workflow (workflowId, projectId, role) VALUES (?, ?, 'workflow:owner')", [wf_id, proj[0]])

    combo_count = len(cat_info["types"]) * 5
    print(f"  [{cat_id}] {wf_name} — {len(cat_info['types'])}유형 × 5피부 = {combo_count}조합 | {len(nodes)}노드")

# Track 2: 제품카피 가이드 (단일 워크플로우 — 2,511개 순차 처리)
print("\n=== Track 2: 제품카피 가이드 생성 ===")
wf_name = "COCHING AI — 제품카피 가이드 v1.0"
wf_id = str(uuid.uuid4()).replace('-', '')[:20]
version_id = str(uuid.uuid4())

nodes, connections = build_track2_workflow("COPY", {"name": "제품카피"}, "")

db.execute("""
    INSERT INTO workflow_entity
    (id, name, active, nodes, connections, settings, versionId,
     triggerCount, createdAt, updatedAt, isArchived, versionCounter)
    VALUES (?, ?, 1, ?, ?, '{}', ?, 0, ?, ?, 0, 1)
""", [wf_id, wf_name, json.dumps(nodes), json.dumps(connections), version_id, now, now])

if proj:
    db.execute("INSERT OR IGNORE INTO shared_workflow (workflowId, projectId, role) VALUES (?, ?, 'workflow:owner')", [wf_id, proj[0]])

print(f"  [COPY] {wf_name} — 2,511+ 제품 카피 | {len(nodes)}노드")

db.commit()

# 최종 확인
print("\n=== 전체 워크플로우 현황 ===")
for r in db.execute("SELECT id, name, active FROM workflow_entity ORDER BY name").fetchall():
    active = "ON" if r[2] else "OFF"
    print(f"  [{active}] {r[0][:12]}... {r[1]}")

total_t1 = sum(len(c["types"]) * 5 for c in CATEGORIES.values())
print(f"\nTrack 1: {total_t1} 카테고리 조합 (6개 병렬, 2분 간격)")
print(f"Track 2: 2,511+ 제품 카피 (1개 워크플로우, 2분 간격)")
print(f"동시 실행: 7개 워크플로우 × 2분 = 분당 ~3.5건")

db.close()
print("\nDone!")
