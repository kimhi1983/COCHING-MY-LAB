#!/usr/bin/env python3
"""
COCHING n8n Workflow v7 — Gemini-First Sequential Pipeline
- Stage 1: Gemini generates ingredient list (50/batch, topic rotation)
- Stage 2: PubChem fetches physical properties
- Stage 3: Gemini enriches with KR/EU regulation info
- Stage 4: Claude Code validates + insights
- All nodes: alwaysOutputData + onError handling
"""
import json, sqlite3, sys, os, shutil, hashlib
from datetime import datetime

WORKFLOW_ID = "FW6GUTq0AzBXjJQ5"
DB_PATH = os.path.expanduser("~/.n8n/database.sqlite")
SSH_CRED = {"sshPrivateKey": {"id": "uK4wQlmywxsEwHyU", "name": "Claude Code WSL2 SSH"}}
PG_CRED  = {"postgres": {"id": "2c8e9119-0bb0-49f2-ad43-f040ab4a7a64", "name": "COCHING PostgreSQL (Local)"}}
GEMINI_KEY = os.environ.get("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY_HERE")

POS = {
    "trigger_schedule":  [-400, 200],
    "trigger_manual":    [-400, 400],
    "exec_config":       [-50,  300],
    "existing_query":    [150,  300],
    # Stage 1: Gemini collects ingredient list
    "gem1_build":        [300,  300],
    "gem1_fetch":        [620,  300],
    "gem1_parse":        [940,  300],
    "gem1_save":         [1260, 300],
    # Stage 2: PubChem physical properties
    "pub_query":         [1580, 300],
    "pub_build":         [1900, 300],
    "pub_fetch":         [2220, 300],
    "pub_parse":         [2540, 300],
    "pub_save":          [2860, 300],
    # Stage 3: Gemini regulation enrichment
    "gem2_query":        [3180, 300],
    "gem2_build":        [3500, 300],
    "gem2_fetch":        [3820, 300],
    "gem2_parse":        [4140, 300],
    "gem2_save_master":  [4460, 200],
    "gem2_save_kb":      [4460, 500],
    "gem2_save_reg":     [4780, 500],
    # Stage 4: Claude + Progress
    "claude_prep":       [5100, 300],
    "claude_ssh":        [5420, 300],
    "claude_result":     [5740, 300],
    "progress_save":     [6060, 300],
    "report":            [6380, 300],
}

def uid(p):
    h = hashlib.md5(p.encode()).hexdigest()
    return f"{h[:8]}-{h[8:12]}-{h[12:16]}-{h[16:20]}-{h[20:32]}"

# ─── CODE: 실행 설정 + 토픽 로테이션 ─────────────────────────────────────────
CODE_EXEC_CONFIG = r"""
const now = new Date();
const batchId = 'batch_' + now.toISOString().replace(/[:.]/g,'_');

// 6-topic rotation: 30분마다 다음 토픽, 3시간에 전 분야 1순환
const dayOfYear = Math.floor((now - new Date(now.getFullYear(),0,0)) / 86400000);
const halfHourSlot = now.getHours() * 2 + Math.floor(now.getMinutes() / 30);
const slot = (dayOfYear * 48 + halfHourSlot) % 6;

const topics = [
  { name: '보습/연화 원료', prompt: '보습제(Humectant), 연화제(Emollient), 피부컨디셔닝제 — 글리세린, 히알루론산, 세라마이드, 스쿠알란, 시어버터 계열' },
  { name: '유화/계면활성 원료', prompt: '유화제(Emulsifier), 계면활성제(Surfactant), 가용화제 — 폴리소르베이트, 세테아릴알코올, PEG 계열, 레시틴, 소르비탄에스터' },
  { name: '방부/항산화 원료', prompt: '방부제(Preservative), 항산화제(Antioxidant) — 페녹시에탄올, 토코페롤, 아스코르빈산, 카프릴릴글라이콜, 에틸헥실글리세린, BHT' },
  { name: '기능성 활성 원료', prompt: '미백성분(나이아신아마이드, 알부틴, 비타민C유도체), 주름개선성분(레티놀, 아데노신, 펩타이드), 자외선차단성분(옥틸메톡시신나메이트, 아보벤존, 징크옥사이드)' },
  { name: '천연/식물 원료', prompt: '식물추출물(녹차, 알로에, 카모마일, 센텔라), 에센셜오일, 식물유(호호바, 아르간, 로즈힙), 천연유래 원료' },
  { name: '제형/특수 원료', prompt: '점증제(카보머, 잔탄검), 점토(벤토나이트, 카올린), pH조절(시트르산, 트리에탄올아민), 향료, 색소(CI번호), 캡슐화/나노 원료' }
];

const topic = topics[slot];

return [{ json: {
  batchId,
  executionTime: now.toISOString(),
  topicSlot: slot,
  topicName: topic.name,
  topicPrompt: topic.prompt,
  batchSize: 50,
  pubchemSize: 10,
  enrichSize: 20
}}];
"""

# ─── CODE: Gemini 원료 목록 생성 요청 ─────────────────────────────────────────
CODE_GEM1_BUILD = r"""
const config = $('⚙️ 실행 설정').first().json;

// 기존 수집된 원료 목록 가져오기 (이번 토픽 유형에 해당하는 것들)
let existingNames = [];
try {
  const dbRows = $('📦 기존 원료 조회').all();
  existingNames = dbRows
    .map(r => r.json.inci_name)
    .filter(n => n && n.length > 1);
} catch(e) {}

const excludeList = existingNames.length > 0
  ? `\n\n⚠️ 아래 원료는 이미 DB에 있으므로 반드시 제외하세요:\n${existingNames.join(', ')}`
  : '';

const prompt = `당신은 화장품 원료 데이터베이스 전문가입니다.
오늘의 주제: **${config.topicName}** (${config.topicPrompt})

이 카테고리에 해당하는 화장품 원료 ${config.batchSize}종의 정보를 JSON 배열로 제공해주세요.
잘 알려진 기본 원료, 최신 트렌드 원료, 니치/특수 원료를 골고루 포함하세요.
가능한 한 다양하고 폭넓은 원료를 선정하세요.${excludeList}

각 원료 형식:
[
  {
    "inci_name": "INCI 국제 명칭 (영문, 정확한 공식명)",
    "korean_name": "한국어 명칭",
    "cas_number": "CAS 번호 (있으면)",
    "ingredient_type": "HUMECTANT|EMOLLIENT|EMULSIFIER|PRESERVATIVE|ANTIOXIDANT|UV_FILTER|ACTIVE|SURFACTANT|THICKENER|COLORANT|FRAGRANCE|SOLVENT|PH_ADJUSTER|CHELATING|FILM_FORMER|OTHER 중 택1",
    "origin": "NATURAL|SYNTHETIC|BIOTECHNOLOGY|MINERAL 중 택1",
    "description": "원료 설명 (한국어, 100자 이내)",
    "main_function": "화장품에서의 주요 기능 (한국어, 50자 이내)"
  }
]

중요:
- INCI명은 공식 국제명칭 정확히 사용
- CAS 번호가 확실하지 않으면 빈 문자열
- 중복 없이 ${config.batchSize}종, 위 제외 목록과 겹치지 않게
- JSON 배열만 반환하세요.`;

const geminiBody = JSON.stringify({
  contents: [{ parts: [{ text: prompt }] }],
  generationConfig: { temperature: 0.7, maxOutputTokens: 16000 }
});

return [{ json: { geminiBody, batchId: config.batchId, topicName: config.topicName }}];
"""

# ─── CODE: Gemini 원료 목록 파싱 ──────────────────────────────────────────────
CODE_GEM1_PARSE = r"""
const config = $('⚙️ 실행 설정').first().json;
const updatedAt = new Date().toISOString();
const result = [];

try {
  const resp = $input.first().json;

  // Extract text from Gemini response — avoid optional chaining on arrays (n8n sandbox)
  let text = '';
  try {
    const c = resp.candidates;
    if (c && c.length > 0 && c[0].content && c[0].content.parts && c[0].content.parts.length > 0) {
      text = c[0].content.parts[0].text || '';
    }
  } catch(e1) {}

  if (!text && typeof resp === 'string') {
    text = resp;
  }
  if (!text) {
    text = JSON.stringify(resp);
  }

  // Extract JSON array from text (handles ```json ... ``` blocks)
  let items = [];
  const cleaned = text.replace(/```json\s*/gi, '').replace(/```\s*/g, '').trim();
  try { items = JSON.parse(cleaned); } catch(e) {
    const m = cleaned.match(/\[[\s\S]*\]/);
    if (m) { try { items = JSON.parse(m[0]); } catch(e2) {} }
  }
  if (!Array.isArray(items)) items = [];

  const validTypes = ['HUMECTANT','EMOLLIENT','EMULSIFIER','PRESERVATIVE','ANTIOXIDANT',
    'UV_FILTER','ACTIVE','SURFACTANT','THICKENER','COLORANT','FRAGRANCE',
    'SOLVENT','PH_ADJUSTER','CHELATING','FILM_FORMER','OTHER'];
  const validOrigins = ['NATURAL','SYNTHETIC','BIOTECHNOLOGY','MINERAL'];

  for (const item of items) {
    if (!item?.inci_name || item.inci_name.length < 2) continue;

    const itype = validTypes.includes((item.ingredient_type||'').toUpperCase())
      ? item.ingredient_type.toUpperCase() : 'OTHER';
    const origin = validOrigins.includes((item.origin||'').toUpperCase())
      ? item.origin.toUpperCase() : '';

    result.push({ json: {
      inci_name:       item.inci_name.trim().substring(0, 500),
      korean_name:     (item.korean_name || '').substring(0, 200),
      cas_number:      (item.cas_number || '').replace(/[^0-9-]/g, '').substring(0, 50),
      ec_number:       '',
      ingredient_type: itype,
      description:     (item.description || '').substring(0, 2000),
      origin:          origin,
      main_function:   (item.main_function || '').substring(0, 500),
      source:          'GEMINI_COLLECT',
      updatedAt
    }});
  }
} catch(e) {
  result.push({ json: { _skip: true, reason: 'gem1_error: ' + e.message, updatedAt }});
}

if (result.filter(r => r.json.inci_name).length === 0 && result.filter(r => r.json._skip).length === 0) {
  result.push({ json: { _skip: true, reason: 'gem1_no_data', updatedAt }});
}

// Add meta
result.push({ json: {
  _meta: true,
  stage: 'gem1',
  count: result.filter(r => r.json.inci_name).length,
  topic: config.topicName,
  batchId: config.batchId,
  updatedAt
}});

return result;
"""

# ─── CODE: PubChem 후보 → 요청 구성 ──────────────────────────────────────────
CODE_PUB_BUILD = r"""
const rows = $input.all().map(i => i.json).filter(r => r.inci_name && !r._skip && !r._meta);
const config = $('⚙️ 실행 설정').first().json;

if (rows.length === 0) {
  return [{ json: { _skip: true, source: 'pubchem', reason: 'no_candidates' }}];
}

// PubChem can only handle one name at a time reliably
// Take first N ingredients
const batch = rows.slice(0, config.pubchemSize);
const firstName = batch[0].inci_name;

return [{ json: {
  _type: 'pubchem_req',
  lookupName: firstName,
  allNames: JSON.stringify(batch.map(r => r.inci_name)),
  candidateCount: batch.length,
  batchId: config.batchId
}}];
"""

# ─── CODE: PubChem 응답 파싱 ─────────────────────────────────────────────────
CODE_PUB_PARSE = r"""
const updatedAt = new Date().toISOString();
const result = [];

try {
  const resp = $input.first().json;
  if (resp._skip) return [{ json: resp }];

  const props = resp?.PropertyTable?.Properties || [];

  for (const p of props) {
    if (!p.CID) continue;
    result.push({ json: {
      _type:              'property',
      pubchem_cid:        p.CID,
      iupac_name:         (p.IUPACName || '').substring(0, 1000),
      molecular_formula:  p.MolecularFormula || '',
      molecular_weight:   parseFloat(p.MolecularWeight) || null,
      canonical_smiles:   (p.CanonicalSMILES || '').substring(0, 2000),
      xlogp:              parseFloat(p.XLogP) || null,
      hbond_donor:        parseInt(p.HBondDonorCount) || null,
      hbond_acceptor:     parseInt(p.HBondAcceptorCount) || null,
      tpsa:               parseFloat(p.TPSA) || null,
      source:             'PUBCHEM',
      updatedAt
    }});
  }
} catch(e) {}

if (result.length === 0) {
  result.push({ json: { _skip: true, source: 'pubchem', updatedAt }});
}
return result;
"""

# ─── CODE: Gemini 규제 보강 요청 ──────────────────────────────────────────────
CODE_GEM2_BUILD = r"""
const rows = $input.all().map(i => i.json).filter(r => r.inci_name && !r._skip && !r._meta);
const config = $('⚙️ 실행 설정').first().json;

if (rows.length === 0) {
  return [{ json: { _skip: true, source: 'gem2', reason: 'no_candidates' }}];
}

const batch = rows.slice(0, config.enrichSize);
const list = batch.map((r, i) =>
  `${i+1}. ${r.inci_name}${r.cas_number ? ' (CAS: '+r.cas_number+')' : ''}`
).join('\n');

const prompt = `아래 ${batch.length}개 화장품 원료의 규제/안전성 정보를 JSON 배열로 제공해주세요.

${list}

각 원료에 대해:
[
  {
    "inci_name": "원래 INCI명",
    "typical_max_concentration": "일반적 최대 사용 농도 (%)",
    "kr_regulation": "한국 식약처 고시/제한 내용",
    "eu_regulation": "EU CosIng 규제 내용",
    "safety_notes": "안전성 주의사항",
    "ewg_score": "EWG 점수 (1-10, 모르면 null)"
  }
]
JSON 배열만 반환하세요.`;

const geminiBody = JSON.stringify({
  contents: [{ parts: [{ text: prompt }] }],
  generationConfig: { temperature: 0.2, maxOutputTokens: 8000 }
});

return [{ json: { geminiBody, batchId: config.batchId }}];
"""

# ─── CODE: Gemini 규제 파싱 ──────────────────────────────────────────────────
CODE_GEM2_PARSE = r"""
const updatedAt = new Date().toISOString();
const result = [];

try {
  const resp = $input.first().json;
  if (resp._skip) return [{ json: resp }];

  let text = '';
  try {
    const c = resp.candidates;
    if (c && c.length > 0 && c[0].content && c[0].content.parts && c[0].content.parts.length > 0) {
      text = c[0].content.parts[0].text || '';
    }
  } catch(e1) {}
  if (!text && typeof resp === 'string') text = resp;
  if (!text) text = JSON.stringify(resp);
  const cleaned = text.replace(/```json\s*/gi, '').replace(/```\s*/g, '').trim();
  let items = [];
  try { items = JSON.parse(cleaned); } catch(e) {
    const m = cleaned.match(/\[[\s\S]*\]/);
    if (m) { try { items = JSON.parse(m[0]); } catch(e2) {} }
  }
  if (!Array.isArray(items)) items = [];

  for (const item of items) {
    if (!item?.inci_name) continue;
    result.push({ json: {
      _type:             'regulation_enrichment',
      inci_name:         item.inci_name.trim(),
      max_concentration: (item.typical_max_concentration || '').substring(0, 50),
      kr_regulation:     (item.kr_regulation || '').substring(0, 500),
      eu_regulation:     (item.eu_regulation || '').substring(0, 500),
      safety_notes:      (item.safety_notes || '').substring(0, 500),
      ewg_score:         item.ewg_score || null,
      updatedAt
    }});
  }
} catch(e) {}

if (result.length === 0) {
  result.push({ json: { _skip: true, source: 'gem2', updatedAt }});
}
return result;
"""

# ─── CODE: Gemini 규제 → KB + regulation_cache 준비 ──────────────────────────
CODE_GEM2_SAVE_KB = r"""
const items = $input.all();
const result = [];
const updatedAt = new Date().toISOString();

for (const item of items) {
  const d = item.json;
  if (d._type !== 'regulation_enrichment') continue;

  const safeId = d.inci_name.toLowerCase().replace(/[^a-z0-9]/g,'_').substring(0,60);

  // knowledge_base
  result.push({ json: {
    _kbType: 'knowledge',
    id:       'reg_' + safeId,
    category: 'INGREDIENT_REGULATION',
    data:     JSON.stringify({
      inci_name: d.inci_name, max_concentration: d.max_concentration,
      kr_regulation: d.kr_regulation, eu_regulation: d.eu_regulation,
      safety_notes: d.safety_notes, ewg_score: d.ewg_score
    }),
    search_key: d.inci_name.toLowerCase(),
    version: 'v7'
  }});

  // regulation_cache (KR)
  if (d.kr_regulation) {
    result.push({ json: {
      _kbType: 'regulation',
      source: 'GEMINI_KR', ingredient: d.inci_name, inci_name: d.inci_name,
      max_concentration: d.max_concentration || '', restriction: d.kr_regulation
    }});
  }
  // regulation_cache (EU)
  if (d.eu_regulation) {
    result.push({ json: {
      _kbType: 'regulation',
      source: 'GEMINI_EU', ingredient: d.inci_name, inci_name: d.inci_name,
      max_concentration: d.max_concentration || '', restriction: d.eu_regulation
    }});
  }
}

if (result.length === 0) {
  result.push({ json: { _skip: true, source: 'gem2_kb' }});
}
return result;
"""

# ─── CODE: Claude 검증 준비 ──────────────────────────────────────────────────
CODE_CLAUDE_PREP = r"""
const config = $('⚙️ 실행 설정').first().json;

// Count saved items
let masterCount = 0;
let propCount = 0;
let regCount = 0;
try { masterCount = $('💾 원료 저장').all().filter(i => i.json.inci_name).length; } catch(e) {}
try { propCount = $('💾 물성 저장').all().filter(i => i.json.pubchem_cid).length; } catch(e) {}
try { regCount = $('💾 규제 저장').all().filter(i => i.json.source).length; } catch(e) {}

// Sample of saved ingredients
let samples = '';
try {
  samples = $('💾 원료 저장').all()
    .filter(i => i.json.inci_name)
    .slice(0, 8)
    .map(i => `- ${i.json.inci_name}: ${i.json.korean_name || '?'} (${i.json.ingredient_type || '?'})`)
    .join(' | ');
} catch(e) {}

const prompt = `COCHING 원료 DB 수집 v7 리포트 (배치: ${config.batchId}, 주제: ${config.topicName}). 원료 ${masterCount}건, 물성 ${propCount}건, 규제 ${regCount}건 처리. 샘플: ${samples || '없음'}. 3문장으로: 1)성과 2)품질 3)다음 수집 제안`;

return [{ json: {
  claudePrompt: prompt,
  batchId: config.batchId,
  topicName: config.topicName,
  masterCount, propCount, regCount
}}];
"""

# ─── CODE: Claude 결과 + 리포트 ──────────────────────────────────────────────
CODE_CLAUDE_RESULT = r"""
const prep = $('🧠 Claude 준비').first().json;
const raw = $input.first().json;
const insight = raw.stdout || raw.response || (typeof raw === 'string' ? raw : JSON.stringify(raw));

return [{ json: {
  _type: 'progress', source: 'GEMINI_COLLECT',
  batchId: prep.batchId, processed: prep.masterCount, status: 'in_progress'
}}, { json: {
  _type: 'progress', source: 'PUBCHEM_PROPS',
  batchId: prep.batchId, processed: prep.propCount, status: 'in_progress'
}}, { json: {
  _type: 'progress', source: 'GEMINI_ENRICH',
  batchId: prep.batchId, processed: prep.regCount, status: 'in_progress'
}}, { json: {
  _type: '_report',
  batchId: prep.batchId,
  topicName: prep.topicName,
  masterCount: prep.masterCount,
  propCount: prep.propCount,
  regCount: prep.regCount,
  claudeInsight: (typeof insight === 'string' ? insight : '').substring(0, 2000)
}}];
"""

# ─── CODE: 최종 리포트 ───────────────────────────────────────────────────────
CODE_REPORT = r"""
const items = $input.all();
let report = null;
for (const i of items) { if (i.json._type === '_report') { report = i.json; break; } }
if (!report) report = { batchId: '?', masterCount: 0, propCount: 0, regCount: 0 };

return [{ json: {
  type: 'execution_report_v7',
  batchId: report.batchId,
  topicName: report.topicName || '',
  masterCount: report.masterCount,
  propCount: report.propCount,
  regCount: report.regCount,
  claudeInsight: report.claudeInsight || '',
  reportedAt: new Date().toISOString()
}}];
"""


def build_nodes():
    N = []
    def add(params, nid, name, ntype, tv, pos, cred=None, err=False):
        node = {"parameters": params, "id": uid(nid), "name": name,
                "type": f"n8n-nodes-base.{ntype}", "typeVersion": tv,
                "position": pos if isinstance(pos, list) else POS[pos],
                "alwaysOutputData": True}
        if cred: node["credentials"] = cred
        if err: node["onError"] = "continueRegularOutput"
        N.append(node)

    # Triggers — 30분 간격 (하루 48회, 8회마다 전 토픽 순환)
    add({"rule": {"interval": [{"field": "minutes", "minutesInterval": 30}]}},
        "sched", "⏰ 30분 자동실행", "scheduleTrigger", 1.2, "trigger_schedule")
    add({}, "manual", "▶ 수동 실행", "manualTrigger", 1, "trigger_manual")

    # Config
    add({"jsCode": CODE_EXEC_CONFIG}, "config", "⚙️ 실행 설정", "code", 2, "exec_config")

    # 기존 수집 원료 조회 (제외 목록 생성용)
    add({"operation": "executeQuery",
         "query": "SELECT inci_name FROM ingredient_master ORDER BY id", "options": {}},
        "eq", "📦 기존 원료 조회", "postgres", 2.5, "existing_query", PG_CRED, True)

    # ── Stage 1: Gemini collects ingredient list ────────────────────────────
    add({"jsCode": CODE_GEM1_BUILD}, "g1b", "🔄 원료수집 구성", "code", 2, "gem1_build")

    add({"method": "POST",
         "url": f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={GEMINI_KEY}",
         "sendHeaders": True,
         "headerParameters": {"parameters": [{"name": "Content-Type", "value": "application/json"}]},
         "sendBody": True, "contentType": "raw", "rawContentType": "application/json",
         "body": "={{ $json.geminiBody }}", "options": {"timeout": 120000}},
        "g1f", "🌐 Gemini 원료수집", "httpRequest", 4.2, "gem1_fetch", err=True)

    add({"jsCode": CODE_GEM1_PARSE}, "g1p", "🔄 원료 파싱", "code", 2, "gem1_parse")

    add({"operation": "executeQuery",
         "query": """INSERT INTO ingredient_master
  (inci_name, korean_name, cas_number, ec_number, ingredient_type, description, origin, source)
SELECT $1, $2, $3, $4, $5, $6, $7, $8
WHERE length($1) > 1
ON CONFLICT (inci_name, cas_number) DO UPDATE
  SET korean_name = COALESCE(NULLIF(EXCLUDED.korean_name,''), ingredient_master.korean_name),
      ingredient_type = COALESCE(NULLIF(EXCLUDED.ingredient_type,''), ingredient_master.ingredient_type),
      description = COALESCE(NULLIF(EXCLUDED.description,''), ingredient_master.description),
      origin = COALESCE(NULLIF(EXCLUDED.origin,''), ingredient_master.origin),
      source = EXCLUDED.source""",
         "options": {"queryReplacement": "={{ [$json.inci_name || '', $json.korean_name || '', $json.cas_number || '', $json.ec_number || '', $json.ingredient_type || '', $json.description || '', $json.origin || '', $json.source || 'GEMINI_COLLECT'] }}"}},
        "g1s", "💾 원료 저장", "postgres", 2.5, "gem1_save", PG_CRED, True)

    # ── Stage 2: PubChem properties ─────────────────────────────────────────
    add({"operation": "executeQuery",
         "query": """SELECT im.id, im.inci_name, im.cas_number
FROM ingredient_master im
LEFT JOIN ingredient_properties ip ON ip.ingredient_id = im.id
WHERE ip.id IS NULL
ORDER BY im.id LIMIT 10""", "options": {}},
        "pq", "🔍 물성 후보", "postgres", 2.5, "pub_query", PG_CRED, True)

    add({"jsCode": CODE_PUB_BUILD}, "pb", "🔄 PubChem 구성", "code", 2, "pub_build")

    add({"method": "GET",
         "url": "=https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/{{ encodeURIComponent($json.lookupName) }}/property/MolecularFormula,MolecularWeight,IUPACName,CanonicalSMILES,XLogP,HBondDonorCount,HBondAcceptorCount,TPSA/JSON",
         "options": {"timeout": 30000}},
        "pf", "🔬 PubChem API", "httpRequest", 4.2, "pub_fetch", err=True)

    add({"jsCode": CODE_PUB_PARSE}, "pp", "🔄 물성 파싱", "code", 2, "pub_parse")

    add({"operation": "executeQuery",
         "query": """INSERT INTO ingredient_properties
  (ingredient_id, pubchem_cid, molecular_formula, molecular_weight,
   iupac_name, canonical_smiles, xlogp, hbond_donor, hbond_acceptor, tpsa, source)
SELECT im.id, $1::int, $2, $3::numeric, $4, $5, $6::numeric, $7::int, $8::int, $9::numeric, 'PUBCHEM'
FROM ingredient_master im
WHERE LOWER(im.inci_name) = LOWER($4) OR im.cas_number = $10
LIMIT 1
ON CONFLICT (ingredient_id, source) DO UPDATE
  SET pubchem_cid=EXCLUDED.pubchem_cid, molecular_formula=EXCLUDED.molecular_formula,
      molecular_weight=EXCLUDED.molecular_weight, canonical_smiles=EXCLUDED.canonical_smiles,
      xlogp=EXCLUDED.xlogp, tpsa=EXCLUDED.tpsa""",
         "options": {"queryReplacement": "={{ [$json.pubchem_cid || 0, $json.molecular_formula || '', $json.molecular_weight || 0, $json.iupac_name || '', $json.canonical_smiles || '', $json.xlogp || 0, $json.hbond_donor || 0, $json.hbond_acceptor || 0, $json.tpsa || 0, ''] }}"}},
        "ps", "💾 물성 저장", "postgres", 2.5, "pub_save", PG_CRED, True)

    # ── Stage 3: Gemini regulation enrichment ───────────────────────────────
    add({"operation": "executeQuery",
         "query": """SELECT id, inci_name, cas_number FROM ingredient_master
WHERE id NOT IN (SELECT DISTINCT im.id FROM ingredient_master im
  INNER JOIN regulation_cache rc ON rc.inci_name = im.inci_name)
ORDER BY id LIMIT 20""", "options": {}},
        "g2q", "🔍 규제 후보", "postgres", 2.5, "gem2_query", PG_CRED, True)

    add({"jsCode": CODE_GEM2_BUILD}, "g2b", "🔄 규제수집 구성", "code", 2, "gem2_build")

    add({"method": "POST",
         "url": f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={GEMINI_KEY}",
         "sendHeaders": True,
         "headerParameters": {"parameters": [{"name": "Content-Type", "value": "application/json"}]},
         "sendBody": True, "contentType": "raw", "rawContentType": "application/json",
         "body": "={{ $json.geminiBody }}", "options": {"timeout": 90000}},
        "g2f", "🌐 Gemini 규제수집", "httpRequest", 4.2, "gem2_fetch", err=True)

    add({"jsCode": CODE_GEM2_PARSE}, "g2p", "🔄 규제 파싱", "code", 2, "gem2_parse")

    add({"operation": "executeQuery",
         "query": """UPDATE ingredient_master
SET description = COALESCE(description,'') || ' [안전성: ' || $1 || ']'
WHERE LOWER(inci_name) = LOWER($2)""",
         "options": {"queryReplacement": "={{ [($json.safety_notes || '').substring(0,200), $json.inci_name || ''] }}"}},
        "g2sm", "💾 규제→원료", "postgres", 2.5, "gem2_save_master", PG_CRED, True)

    add({"jsCode": CODE_GEM2_SAVE_KB}, "g2kb", "🔄 KB 준비", "code", 2, "gem2_save_kb")

    add({"operation": "executeQuery",
         "query": """INSERT INTO coching_knowledge_base (id, category, data, search_key, version, updated_at)
VALUES ($1, $2, $3::jsonb, $4, $5, NOW())
ON CONFLICT (id) DO UPDATE SET data=EXCLUDED.data, search_key=EXCLUDED.search_key, version=EXCLUDED.version, updated_at=NOW()""",
         "options": {"queryReplacement": "={{ [$json.id || 'skip_'+Date.now(), $json.category || '', $json.data || '{}', $json.search_key || '', $json.version || 'v7'] }}"}},
        "skb", "💾 지식 저장", "postgres", 2.5, "gem2_save_reg", PG_CRED, True)

    add({"operation": "executeQuery",
         "query": """INSERT INTO regulation_cache (source, ingredient, inci_name, max_concentration, restriction, updated_at)
VALUES ($1, $2, $3, $4, $5, NOW())
ON CONFLICT ON CONSTRAINT uq_reg DO UPDATE
  SET inci_name=EXCLUDED.inci_name, max_concentration=EXCLUDED.max_concentration,
      restriction=EXCLUDED.restriction, updated_at=NOW()""",
         "options": {"queryReplacement": "={{ [$json.source || 'UNKNOWN', $json.ingredient || '', $json.inci_name || '', $json.max_concentration || '', $json.restriction || ''] }}"}},
        "sreg", "💾 규제 저장", "postgres", 2.5,
        [POS["gem2_save_reg"][0], POS["gem2_save_reg"][1]+200], PG_CRED, True)

    # ── Stage 4: Claude + Progress ──────────────────────────────────────────
    add({"jsCode": CODE_CLAUDE_PREP}, "cp", "🧠 Claude 준비", "code", 2, "claude_prep")

    N.append({
        "parameters": {
            "command": "=bash /home/kpros/claude_run.sh '{{ $json.claudePrompt.replaceAll(\"'\", \"\\\\'\").replaceAll(\"\\n\", \" \") }}' 60"
        },
        "id": uid("cssh"), "name": "🧠 Claude 분석",
        "type": "n8n-nodes-base.ssh", "typeVersion": 1,
        "position": POS["claude_ssh"],
        "credentials": SSH_CRED,
        "alwaysOutputData": True,
        "onError": "continueRegularOutput"
    })

    add({"jsCode": CODE_CLAUDE_RESULT}, "cr", "📋 결과 정리", "code", 2, "claude_result")

    add({"operation": "executeQuery",
         "query": """INSERT INTO collection_progress (source, batch_id, processed_items, status)
VALUES ($1, $2, $3::int, $4)
ON CONFLICT (source) DO UPDATE
  SET batch_id=EXCLUDED.batch_id,
      processed_items=collection_progress.processed_items + EXCLUDED.processed_items,
      status=EXCLUDED.status, updated_at=NOW()""",
         "options": {"queryReplacement": "={{ [$json.source || 'UNKNOWN', $json.batchId || '', $json.processed || 0, $json.status || 'in_progress'] }}"}},
        "psave", "💾 진행 저장", "postgres", 2.5, "progress_save", PG_CRED, True)

    add({"jsCode": CODE_REPORT}, "rpt", "📊 실행 리포트", "code", 2, "report")

    return N


def build_connections(nodes):
    C = [
        ("⏰ 30분 자동실행", "⚙️ 실행 설정"),
        ("▶ 수동 실행", "⚙️ 실행 설정"),
        # 기존 원료 조회 → 원료수집 구성
        ("⚙️ 실행 설정", "📦 기존 원료 조회"),
        ("📦 기존 원료 조회", "🔄 원료수집 구성"),
        ("🔄 원료수집 구성", "🌐 Gemini 원료수집"),
        ("🌐 Gemini 원료수집", "🔄 원료 파싱"),
        ("🔄 원료 파싱", "💾 원료 저장"),
        # Stage 2
        ("💾 원료 저장", "🔍 물성 후보"),
        ("🔍 물성 후보", "🔄 PubChem 구성"),
        ("🔄 PubChem 구성", "🔬 PubChem API"),
        ("🔬 PubChem API", "🔄 물성 파싱"),
        ("🔄 물성 파싱", "💾 물성 저장"),
        # Stage 3
        ("💾 물성 저장", "🔍 규제 후보"),
        ("🔍 규제 후보", "🔄 규제수집 구성"),
        ("🔄 규제수집 구성", "🌐 Gemini 규제수집"),
        ("🌐 Gemini 규제수집", "🔄 규제 파싱"),
        ("🔄 규제 파싱", "💾 규제→원료"),
        ("🔄 규제 파싱", "🔄 KB 준비"),
        ("🔄 KB 준비", "💾 지식 저장"),
        ("🔄 KB 준비", "💾 규제 저장"),
        # Stage 4
        ("💾 규제→원료", "🧠 Claude 준비"),
        ("🧠 Claude 준비", "🧠 Claude 분석"),
        ("🧠 Claude 분석", "📋 결과 정리"),
        ("📋 결과 정리", "💾 진행 저장"),
        ("💾 진행 저장", "📊 실행 리포트"),
    ]

    conns = {}
    for f, t in C:
        if f not in conns: conns[f] = {"main": [[]]}
        conns[f]["main"][0].append({"node": t, "type": "main", "index": 0})
    return conns


def main():
    if not os.path.exists(DB_PATH):
        print(f"ERROR: DB not found at {DB_PATH}"); sys.exit(1)

    backup = DB_PATH + f".bak_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    shutil.copy2(DB_PATH, backup)
    print(f"Backup: {backup}")

    nodes = build_nodes()
    conns = build_connections(nodes)
    nj, cj = json.dumps(nodes, ensure_ascii=False), json.dumps(conns, ensure_ascii=False)
    print(f"Nodes: {len(nodes)}, Connections: {len([c for v in conns.values() for a in v['main'] for c in a])}")

    db = sqlite3.connect(DB_PATH)
    cur = db.cursor()
    cur.execute("SELECT name FROM workflow_entity WHERE id=?", (WORKFLOW_ID,))
    row = cur.fetchone()
    if not row: print(f"ERROR: Not found"); db.close(); sys.exit(1)
    print(f"Updating: {row[0]}")

    cur.execute("UPDATE workflow_entity SET nodes=?, connections=?, name='COCHING AI — 원료 수집 v7 Gemini+Claude', updatedAt=? WHERE id=?",
                (nj, cj, datetime.now().isoformat(), WORKFLOW_ID))
    db.commit()
    print(f"Updated {cur.rowcount} row(s)")
    cur.execute("SELECT name, length(nodes), length(connections) FROM workflow_entity WHERE id=?", (WORKFLOW_ID,))
    v = cur.fetchone()
    print(f"OK: '{v[0]}' nodes={v[1]}B conns={v[2]}B")
    db.close()

if __name__ == "__main__":
    main()
