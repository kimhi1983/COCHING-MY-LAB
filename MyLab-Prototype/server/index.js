import 'dotenv/config'
import express from 'express'
import cors from 'cors'
import pool from './db.js'

const app = express()
app.use(cors())
app.use(express.json())

// ─── Health ───
app.get('/api/health', async (req, res) => {
  try {
    const { rows } = await pool.query('SELECT NOW() as now')
    res.json({ ok: true, time: rows[0].now })
  } catch (err) {
    res.status(500).json({ ok: false, error: err.message })
  }
})

// ─── 통계 (KPI 위젯용) ───
app.get('/api/stats', async (req, res) => {
  try {
    const [kbAll, kbIngredients, regulations] = await Promise.all([
      pool.query('SELECT count(*) as cnt FROM coching_knowledge_base'),
      pool.query("SELECT count(*) as cnt FROM coching_knowledge_base WHERE category = 'INGREDIENT_REGULATION'"),
      pool.query('SELECT count(*) as cnt FROM regulation_cache'),
    ])
    const sourceBreakdown = await pool.query(
      'SELECT source, count(*) as cnt FROM regulation_cache GROUP BY source ORDER BY cnt DESC'
    )
    res.json({
      totalIngredients: parseInt(kbIngredients.rows[0].cnt),
      totalRegulations: parseInt(regulations.rows[0].cnt),
      totalKnowledge: parseInt(kbAll.rows[0].cnt),
      regulationsBySource: sourceBreakdown.rows.map(r => ({ source: r.source, count: parseInt(r.cnt) })),
    })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 원료 목록 (knowledge_base INGREDIENT_REGULATION에서 추출) ───
app.get('/api/ingredients', async (req, res) => {
  try {
    const { q, limit = 50, offset = 0 } = req.query
    let where = ["category = 'INGREDIENT_REGULATION'"]
    let params = []
    let idx = 1

    if (q) {
      where.push(`(search_key ILIKE $${idx} OR data->>'inci_name' ILIKE $${idx})`)
      params.push(`%${q}%`)
      idx++
    }

    const whereClause = 'WHERE ' + where.join(' AND ')
    const countQuery = `SELECT count(*) FROM coching_knowledge_base ${whereClause}`
    const dataQuery = `SELECT id, search_key, data, updated_at
      FROM coching_knowledge_base ${whereClause}
      ORDER BY search_key
      LIMIT $${idx} OFFSET $${idx + 1}`
    params.push(parseInt(limit), parseInt(offset))

    const [countRes, dataRes] = await Promise.all([
      pool.query(countQuery, params.slice(0, idx - 1)),
      pool.query(dataQuery, params),
    ])

    res.json({
      total: parseInt(countRes.rows[0].count),
      items: dataRes.rows.map(r => ({
        id: r.id,
        inci_name: r.data?.inci_name || r.search_key,
        korean_name: r.search_key,
        ewg_score: r.data?.ewg_score,
        kr_regulation: r.data?.kr_regulation,
        eu_regulation: r.data?.eu_regulation,
        max_concentration: r.data?.max_concentration,
        safety_notes: r.data?.safety_notes,
        updated_at: r.updated_at,
      })),
    })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 원료 상세 ───
app.get('/api/ingredients/:id', async (req, res) => {
  try {
    const { id } = req.params
    const { rows } = await pool.query('SELECT * FROM coching_knowledge_base WHERE id = $1', [id])
    if (!rows.length) return res.status(404).json({ error: 'Not found' })

    const row = rows[0]
    const inciName = row.data?.inci_name || row.search_key

    // 규제 정보 조회
    const regs = await pool.query(
      'SELECT * FROM regulation_cache WHERE inci_name ILIKE $1',
      [inciName]
    )

    res.json({
      id: row.id,
      inci_name: inciName,
      korean_name: row.search_key,
      data: row.data,
      regulations: regs.rows,
      updated_at: row.updated_at,
    })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 규제 데이터 (위젯용) ───
app.get('/api/regulations', async (req, res) => {
  try {
    const { source, q, limit = 50, offset = 0 } = req.query
    let where = []
    let params = []
    let idx = 1

    if (source && source !== 'ALL') {
      where.push(`source = $${idx}`)
      params.push(source)
      idx++
    }
    if (q) {
      where.push(`(ingredient ILIKE $${idx} OR inci_name ILIKE $${idx})`)
      params.push(`%${q}%`)
      idx++
    }

    const whereClause = where.length ? 'WHERE ' + where.join(' AND ') : ''
    const countQuery = `SELECT count(*) FROM regulation_cache ${whereClause}`
    const dataQuery = `SELECT source, ingredient, inci_name, max_concentration, restriction, updated_at
      FROM regulation_cache ${whereClause}
      ORDER BY ingredient
      LIMIT $${idx} OFFSET $${idx + 1}`
    params.push(parseInt(limit), parseInt(offset))

    const [countRes, dataRes] = await Promise.all([
      pool.query(countQuery, params.slice(0, idx - 1)),
      pool.query(dataQuery, params),
    ])

    res.json({
      total: parseInt(countRes.rows[0].count),
      items: dataRes.rows,
    })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 규제 소스 목록 ───
app.get('/api/regulation-sources', async (req, res) => {
  try {
    const { rows } = await pool.query(
      'SELECT source, count(*) as cnt FROM regulation_cache GROUP BY source ORDER BY cnt DESC'
    )
    res.json(rows.map(r => ({ source: r.source, count: parseInt(r.cnt) })))
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 지식베이스 검색 ───
app.get('/api/knowledge', async (req, res) => {
  try {
    const { q, limit = 20, offset = 0 } = req.query
    let where = ["category = 'INGREDIENT_REGULATION'"]
    let params = []
    let idx = 1

    if (q) {
      where.push(`search_key ILIKE $${idx}`)
      params.push(`%${q}%`)
      idx++
    }

    const whereClause = 'WHERE ' + where.join(' AND ')
    const dataQuery = `SELECT id, category, search_key, version, updated_at, data
      FROM coching_knowledge_base ${whereClause}
      ORDER BY updated_at DESC
      LIMIT $${idx} OFFSET $${idx + 1}`
    params.push(parseInt(limit), parseInt(offset))

    const { rows } = await pool.query(dataQuery, params)
    res.json({ items: rows })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// ─── 가이드 처방 생성 (DB 원료 기반) ───
app.post('/api/guide-formula', async (req, res) => {
  try {
    const { productType, requirements } = req.body

    // DB에서 INGREDIENT_REGULATION 카테고리 원료 랜덤 선택
    const { rows: kbRows } = await pool.query(
      "SELECT id, search_key, data FROM coching_knowledge_base WHERE category = 'INGREDIENT_REGULATION' ORDER BY RANDOM() LIMIT 12"
    )

    // 선택된 원료들의 규제 정보 조회
    const inciNames = kbRows.map(r => r.data?.inci_name || r.search_key)
    const { rows: regulations } = await pool.query(
      'SELECT inci_name, max_concentration, restriction, source FROM regulation_cache WHERE inci_name = ANY($1)',
      [inciNames]
    )
    const regMap = {}
    for (const r of regulations) {
      if (!regMap[r.inci_name]) regMap[r.inci_name] = []
      regMap[r.inci_name].push(r)
    }

    // 배합비 자동 생성
    const formulaIngredients = []
    let totalPct = 0

    for (const row of kbRows) {
      const data = row.data || {}
      const inciName = data.inci_name || row.search_key
      const pct = Math.round((0.5 + Math.random() * 5) * 100) / 100
      totalPct += pct

      // 복합원료 판별: INCI에 콤마/and 포함 또는 composition 데이터 존재
      const isCompound = !!(
        (inciName && (inciName.includes(',') || / and /i.test(inciName))) ||
        data.composition || data.components
      )

      formulaIngredients.push({
        id: row.id,
        name: row.search_key,
        korean_name: data.korean_name || row.search_key,
        inci_name: inciName,
        percentage: pct,
        type: guessType(inciName, data),
        function: guessFunction(inciName, data),
        is_compound: isCompound,
        compound_name: isCompound ? (data.trade_name || data.korean_name || row.search_key) : null,
        regulations: regMap[inciName] || [],
        safety: data.ewg_score ? {
          ewg_score: data.ewg_score,
          kr_regulation: data.kr_regulation,
          eu_regulation: data.eu_regulation,
          max_concentration: data.max_concentration,
          safety_notes: data.safety_notes,
        } : null,
      })
    }

    // 정제수로 100% 맞추기
    const waterPct = Math.round((100 - totalPct) * 100) / 100
    formulaIngredients.unshift({
      id: null,
      name: '정제수',
      inci_name: 'Water',
      percentage: waterPct,
      type: 'SOLVENT',
      function: '용매',
      regulations: [],
      safety: null,
    })

    res.json({
      description: generateDescription(productType, requirements, formulaIngredients),
      ingredients: formulaIngredients,
      totalPercentage: 100,
      totalDbIngredients: kbRows.length,
      regulationsChecked: regulations.length,
      generatedAt: new Date().toISOString(),
    })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

function guessType(inciName, data) {
  const name = (inciName || '').toLowerCase()
  const kr = (data.kr_regulation || '').toLowerCase()
  if (kr.includes('보존제') || name.includes('paraben') || name.includes('phenoxyethanol')) return 'PRESERVATIVE'
  if (kr.includes('자외선') || name.includes('benzophenone') || name.includes('titanium dioxide')) return 'UV_FILTER'
  if (kr.includes('색소')) return 'COLORANT'
  if (name.includes('glycerin') || name.includes('hyaluronic') || name.includes('panthenol')) return 'HUMECTANT'
  if (name.includes('tocopherol') || name.includes('ascorb')) return 'ANTIOXIDANT'
  if (name.includes('carbomer') || name.includes('xanthan') || name.includes('cellulose')) return 'THICKENER'
  if (name.includes('stearate') || name.includes('cetearyl') || name.includes('polysorbate')) return 'EMULSIFIER'
  if (name.includes('dimethicone') || name.includes('oil') || name.includes('butter')) return 'EMOLLIENT'
  if (name.includes('retino') || name.includes('niacinamide') || name.includes('peptide')) return 'ACTIVE'
  return 'OTHER'
}

function guessFunction(inciName, data) {
  const type = guessType(inciName, data)
  const labels = {
    HUMECTANT: '보습제', ACTIVE: '활성성분', EMULSIFIER: '유화제',
    EMOLLIENT: '에몰리언트', THICKENER: '증점제', PRESERVATIVE: '방부제',
    PH_ADJUSTER: 'pH조절제', ANTIOXIDANT: '항산화제', SURFACTANT: '계면활성제',
    UV_FILTER: '자외선차단제', FILM_FORMER: '피막형성제', COLORANT: '색소',
    FRAGRANCE: '향료', OTHER: '기타', SOLVENT: '용매',
  }
  return labels[type] || '기타'
}

function generateDescription(productType, requirements, ingredients) {
  const typeNames = {
    'moisturizing-serum': '고보습 세럼',
    'brightening-cream': '미백 크림',
    'sunscreen-spf50': '선크림 SPF50+',
    'cleansing-foam': '클렌징 폼',
    'anti-aging-serum': '안티에이징 세럼',
  }
  const name = typeNames[productType] || '화장품'
  const regCount = ingredients.filter(i => i.regulations.length > 0).length

  let desc = `${name} 처방입니다. `
  desc += `총 ${ingredients.length}종 원료로 구성되었으며, COCHING DB 기반 ${regCount}종 규제 정보가 확인되었습니다.`
  if (requirements) desc += `\n\n사용자 요청: ${requirements}`
  return desc
}

// ─── Phase 분류 헬퍼 ───
function assignPhase(inciName, type) {
  const name = (inciName || '').toLowerCase()
  if (type === 'PRESERVATIVE' || type === 'FRAGRANCE') return 'D'
  if (
    type === 'EMOLLIENT' ||
    type === 'EMULSIFIER' ||
    name.includes('dimethicone') ||
    name.includes('silicone') ||
    name.includes('oil') ||
    name.includes('butter') ||
    name.includes('wax') ||
    name.includes('stearate') ||
    name.includes('cetearyl') ||
    name.includes('polysorbate')
  ) return 'B'
  if (
    type === 'ACTIVE' ||
    type === 'THICKENER' ||
    name.includes('carbomer') ||
    name.includes('xanthan') ||
    name.includes('pH') ||
    name.includes('sodium hydroxide') ||
    name.includes('citric acid') ||
    name.includes('triethanolamine')
  ) return 'C'
  // Water, Glycerin, 수용성 성분 등
  return 'A'
}

// Phase 목록 생성 헬퍼
function buildPhaseSummary(ingredients) {
  const phaseMap = {
    A: { phase: 'A', name: '수상(Water Phase)', temp: '75°C', items: [] },
    B: { phase: 'B', name: '유상(Oil Phase)', temp: '75°C', items: [] },
    C: { phase: 'C', name: '첨가(Add Phase)', temp: '45°C 이하', items: [] },
    D: { phase: 'D', name: '방부/향(Finishing)', temp: '40°C 이하', items: [] },
  }
  for (const ing of ingredients) {
    const p = ing.phase || 'A'
    if (phaseMap[p]) phaseMap[p].items.push(ing.inci_name)
  }
  return Object.values(phaseMap).filter(p => p.items.length > 0)
}

// 기본 제조 공정 생성 헬퍼
function buildDefaultProcess(phases) {
  const steps = []
  let stepNum = 1
  const phaseOrder = ['A', 'B', 'C', 'D']
  for (const phaseId of phaseOrder) {
    const p = phases.find(ph => ph.phase === phaseId)
    if (!p) continue
    if (phaseId === 'A') {
      steps.push({ step: stepNum++, phase: 'A', desc: `Phase A 원료(${p.items.slice(0, 3).join(', ')} 등)를 배합조에 투입`, temp: p.temp, time: '10분', note: '완전 용해 확인' })
      steps.push({ step: stepNum++, phase: 'A', desc: 'Phase A 75°C까지 가열 및 균질화', temp: '75°C', time: '15분', note: '교반 속도 중속 유지' })
    } else if (phaseId === 'B') {
      steps.push({ step: stepNum++, phase: 'B', desc: `Phase B 원료(${p.items.slice(0, 3).join(', ')} 등) 별도 가열 용해`, temp: p.temp, time: '10분', note: '완전 용해 후 진행' })
      steps.push({ step: stepNum++, phase: 'B', desc: 'Phase B를 Phase A에 서서히 투입하며 유화', temp: '75°C', time: '20분', note: '고속 교반(호모믹서) 사용' })
    } else if (phaseId === 'C') {
      steps.push({ step: stepNum++, phase: 'C', desc: `45°C 이하로 냉각 후 Phase C 투입(${p.items.slice(0, 3).join(', ')} 등)`, temp: p.temp, time: '10분', note: '순서대로 투입, 충분히 혼합' })
    } else if (phaseId === 'D') {
      steps.push({ step: stepNum++, phase: 'D', desc: `40°C 이하에서 Phase D 투입(${p.items.slice(0, 3).join(', ')} 등)`, temp: p.temp, time: '5분', note: '방부제/향료 휘발 주의' })
    }
  }
  steps.push({ step: stepNum++, phase: '-', desc: '최종 품질 검사 (점도, pH, 외관, 향) 후 충전', temp: '상온', time: '-', note: 'pH 4.5~7.0 범위 확인' })
  return steps
}

// DB 원료에서 Phase 포함 처방 생성 (폴백 공통 로직)
async function buildDbFormula(productType, requirements, targetMarket) {
  const { rows: kbRows } = await pool.query(
    "SELECT id, search_key, data FROM coching_knowledge_base WHERE category = 'INGREDIENT_REGULATION' ORDER BY RANDOM() LIMIT 12"
  )
  const inciNames = kbRows.map(r => r.data?.inci_name || r.search_key)
  const { rows: regulations } = await pool.query(
    'SELECT inci_name, max_concentration, restriction, source FROM regulation_cache WHERE inci_name = ANY($1)',
    [inciNames]
  )
  const regMap = {}
  for (const r of regulations) {
    if (!regMap[r.inci_name]) regMap[r.inci_name] = []
    regMap[r.inci_name].push(r)
  }

  const formulaIngredients = []
  let totalPct = 0

  for (const row of kbRows) {
    const data = row.data || {}
    const inciName = data.inci_name || row.search_key
    const type = guessType(inciName, data)
    const pct = Math.round((0.5 + Math.random() * 5) * 100) / 100
    totalPct += pct

    const isCompound = !!(
      (inciName && (inciName.includes(',') || / and /i.test(inciName))) ||
      data.composition || data.components
    )

    formulaIngredients.push({
      id: row.id,
      name: row.search_key,
      inci_name: inciName,
      korean_name: data.korean_name || row.search_key,
      percentage: pct,
      phase: assignPhase(inciName, type),
      type,
      function: guessFunction(inciName, data),
      is_compound: isCompound,
      compound_name: isCompound ? (data.trade_name || data.korean_name || row.search_key) : null,
      regulations: regMap[inciName] || [],
      safety: data.ewg_score ? {
        ewg_score: data.ewg_score,
        kr_regulation: data.kr_regulation,
        eu_regulation: data.eu_regulation,
        max_concentration: data.max_concentration,
        safety_notes: data.safety_notes,
      } : null,
    })
  }

  const waterPct = Math.round((100 - totalPct) * 100) / 100
  formulaIngredients.unshift({
    id: null,
    name: '정제수',
    inci_name: 'Water',
    korean_name: '정제수',
    percentage: waterPct,
    phase: 'A',
    type: 'SOLVENT',
    function: '용매',
    regulations: [],
    safety: null,
  })

  const phases = buildPhaseSummary(formulaIngredients)
  const process = buildDefaultProcess(phases)

  return {
    description: generateDescription(productType, requirements, formulaIngredients),
    ingredients: formulaIngredients,
    phases,
    process,
    cautions: ['처방은 참고용이며 실제 제조 전 안정성 테스트 필수', '규제 정보는 최신 공식 문서로 교차 확인 필요'],
    totalPercentage: 100,
    totalDbIngredients: kbRows.length,
    regulationsChecked: regulations.length,
    generatedAt: new Date().toISOString(),
    source: 'db-fallback',
  }
}

// ─── Gemini API 호출 헬퍼 ───
async function callGemini(prompt) {
  const apiKey = process.env.GEMINI_API_KEY
  if (!apiKey) return null

  const geminiRes = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.3,
          responseMimeType: 'application/json',
        },
      }),
    }
  )

  if (!geminiRes.ok) {
    const errBody = await geminiRes.text()
    throw new Error(`Gemini API 오류 (${geminiRes.status}): ${errBody}`)
  }

  const geminiData = await geminiRes.json()
  const text = geminiData.candidates?.[0]?.content?.parts?.[0]?.text
  if (!text) throw new Error('Gemini 응답에 content가 없습니다.')

  // JSON 파싱 (코드블록 래핑 제거)
  const cleaned = text.replace(/^```json\s*/, '').replace(/\s*```$/, '').trim()
  try {
    return JSON.parse(cleaned)
  } catch {
    throw new Error('Gemini 응답 JSON 파싱 실패: ' + cleaned.substring(0, 200))
  }
}

// ─── LLM 정밀 처방 생성 (Gemini) ───
app.post('/api/ai-formula', async (req, res) => {
  try {
    const { productType, requirements, targetMarket = 'KR', customIngredients = [], physicalSpecs = [] } = req.body

    if (!productType) {
      return res.status(400).json({ success: false, error: 'productType은 필수입니다.' })
    }

    if (!process.env.GEMINI_API_KEY) {
      // 폴백: DB 기반 Phase 분류 처방
      const formula = await buildDbFormula(productType, requirements, targetMarket)
      return res.json({ success: true, data: formula })
    }

    // DB 원료 후보 조회
    const { rows: kbRows } = await pool.query(
      "SELECT id, search_key, data FROM coching_knowledge_base WHERE category = 'INGREDIENT_REGULATION' ORDER BY RANDOM() LIMIT 30"
    )
    // 규제 제약 조회
    const inciNames = kbRows.map(r => r.data?.inci_name || r.search_key)
    const { rows: regRows } = await pool.query(
      'SELECT inci_name, max_concentration, restriction, source FROM regulation_cache WHERE inci_name = ANY($1) AND source ILIKE $2',
      [inciNames, `%${targetMarket}%`]
    )

    const ingredientList = kbRows.map(r => {
      const inci = r.data?.inci_name || r.search_key
      const reg = regRows.find(rg => rg.inci_name === inci)
      return `- ${inci} (한국명: ${r.search_key})${reg ? ` [최대 ${reg.max_concentration}, ${reg.source}]` : ''}`
    }).join('\n')

    const customList = customIngredients.length
      ? '\n\n반드시 포함할 원료:\n' + customIngredients.map(c => `- ${c.name}: ${c.percentage}%`).join('\n')
      : ''

    const physSpecBlock = physicalSpecs.length
      ? '\n\n목표 물성 스펙 (처방이 이 물성을 달성하도록 원료를 선정하세요):\n' + physicalSpecs.map(s => `- ${s}`).join('\n')
      : ''

    const prompt = `당신은 화장품 처방 전문가입니다. 다음 조건으로 처방을 생성하세요.

규칙:
1. 배합비 합계는 정확히 100.00%
2. 정제수(Water)로 잔량 조절
3. Phase A(수상)/B(유상)/C(첨가)/D(방부/향) 구분
4. 규제 최대 농도를 초과하지 않을 것
5. 제조 공정(Manufacturing Process) 포함
6. 목표 물성(pH, 점도 등)을 달성할 수 있는 원료 조합으로 처방할 것

응답은 반드시 JSON 형식:
{
  "ingredients": [
    {"inci_name": "...", "korean_name": "...", "percentage": 0.00, "phase": "A", "function": "...", "type": "..."}
  ],
  "phases": [
    {"phase": "A", "name": "수상", "temp": "75°C", "items": ["Water", "Glycerin"]}
  ],
  "process": [
    {"step": 1, "phase": "A", "desc": "...", "temp": "75°C", "time": "10분", "note": "..."}
  ],
  "description": "...",
  "cautions": ["..."]
}

제형: ${productType}
요구사항: ${requirements || '없음'}
타겟 시장: ${targetMarket}
${customList}${physSpecBlock}

사용 가능한 DB 원료 목록:
${ingredientList}

위 원료에서 적합한 것을 선택하여 처방을 완성하세요.`

    const parsed = await callGemini(prompt)
    const totalPct = parsed.ingredients?.reduce((sum, i) => sum + (i.percentage || 0), 0) ?? 0

    return res.json({
      success: true,
      data: {
        description: parsed.description || '',
        ingredients: parsed.ingredients || [],
        phases: parsed.phases || buildPhaseSummary(parsed.ingredients || []),
        process: parsed.process || [],
        cautions: parsed.cautions || [],
        totalPercentage: Math.round(totalPct * 100) / 100,
        generatedAt: new Date().toISOString(),
        source: 'gemini-2.0-flash',
      },
    })
  } catch (err) {
    res.status(500).json({ success: false, error: err.message })
  }
})

// ─── 카피 처방 (역처방, Gemini) ───
app.post('/api/copy-formula', async (req, res) => {
  try {
    const { productName, targetMarket = 'KR' } = req.body

    if (!productName) {
      return res.status(400).json({ success: false, error: 'productName은 필수입니다.' })
    }

    if (!process.env.GEMINI_API_KEY) {
      // 폴백: 제품명 키워드 기반 템플릿
      const lowerName = productName.toLowerCase()
      let templateType = 'moisturizing-serum'
      if (lowerName.includes('크림') || lowerName.includes('cream')) templateType = 'brightening-cream'
      else if (lowerName.includes('선') || lowerName.includes('sun')) templateType = 'sunscreen-spf50'
      else if (lowerName.includes('클렌') || lowerName.includes('clean') || lowerName.includes('foam')) templateType = 'cleansing-foam'
      else if (lowerName.includes('세럼') || lowerName.includes('serum') || lowerName.includes('에센스')) templateType = 'moisturizing-serum'

      const formula = await buildDbFormula(templateType, `${productName} 참조 역처방`, targetMarket)
      formula.source = 'template-fallback'
      formula.description = `[${productName}] 참조 역처방(템플릿 기반). ` + formula.description
      return res.json({ success: true, data: formula })
    }

    const prompt = `당신은 화장품 역처방(reverse formulation) 전문가입니다.

다음 제품의 공개 전성분 표기 순서를 기반으로 배합비를 역산하세요.

규칙:
1. 전성분 표기 순서는 함량 순서 (1% 미만은 순서 무관)
2. 배합비 합계 100.00%
3. Phase A(수상)/B(유상)/C(첨가)/D(방부/향) 구분
4. 규제 최대 농도 준수

응답은 반드시 JSON 형식:
{
  "ingredients": [
    {"inci_name": "...", "korean_name": "...", "percentage": 0.00, "phase": "A", "function": "...", "type": "..."}
  ],
  "phases": [
    {"phase": "A", "name": "수상", "temp": "75°C", "items": ["Water", "Glycerin"]}
  ],
  "process": [
    {"step": 1, "phase": "A", "desc": "...", "temp": "75°C", "time": "10분", "note": "..."}
  ],
  "description": "...",
  "cautions": ["..."]
}

제품명: ${productName}
타겟 시장: ${targetMarket}

위 제품의 공개 전성분 정보를 기반으로 역처방을 추정하세요. 해당 제품 유형에 전형적인 배합비를 참조하여 합계 100%가 되도록 처방을 완성하세요.`

    const parsed = await callGemini(prompt)
    const totalPct = parsed.ingredients?.reduce((sum, i) => sum + (i.percentage || 0), 0) ?? 0

    return res.json({
      success: true,
      data: {
        description: parsed.description || `[${productName}] 역처방 분석 결과`,
        ingredients: parsed.ingredients || [],
        phases: parsed.phases || buildPhaseSummary(parsed.ingredients || []),
        process: parsed.process || [],
        cautions: parsed.cautions || [],
        totalPercentage: Math.round(totalPct * 100) / 100,
        generatedAt: new Date().toISOString(),
        source: 'gemini-2.0-flash',
      },
    })
  } catch (err) {
    res.status(500).json({ success: false, error: err.message })
  }
})

// ─── 처방 품질 검증 ───
app.post('/api/validate-formula', async (req, res) => {
  try {
    const { ingredients, targetMarket = 'KR' } = req.body

    if (!Array.isArray(ingredients) || ingredients.length === 0) {
      return res.status(400).json({ success: false, error: 'ingredients 배열이 필요합니다.' })
    }

    const checks = []
    let passed = true

    // 1. 배합비 합계 검사 (±0.5% 허용)
    const totalPct = Math.round(ingredients.reduce((sum, i) => sum + (parseFloat(i.percentage) || 0), 0) * 100) / 100
    const totalDiff = Math.abs(totalPct - 100)
    const totalStatus = totalDiff <= 0.5 ? 'pass' : 'fail'
    if (totalStatus === 'fail') passed = false
    checks.push({
      name: '배합비 합계',
      status: totalStatus,
      message: totalStatus === 'pass' ? '총 배합비가 허용 범위 이내입니다.' : '총 배합비가 허용 범위를 벗어났습니다.',
      detail: `${totalPct}% (허용 범위: 99.5~100.5%)`,
    })

    // 2. 규제 최대 농도 초과 검사
    const inciNames = ingredients.map(i => i.inci_name).filter(Boolean)
    const { rows: regRows } = await pool.query(
      'SELECT inci_name, max_concentration, restriction, source FROM regulation_cache WHERE inci_name = ANY($1)',
      [inciNames]
    )

    const violationDetails = []
    for (const ing of ingredients) {
      const regs = regRows.filter(r => r.inci_name === ing.inci_name && r.max_concentration)
      for (const reg of regs) {
        const maxMatch = reg.max_concentration?.match(/([\d.]+)\s*%/)
        if (maxMatch) {
          const maxVal = parseFloat(maxMatch[1])
          if (!isNaN(maxVal) && parseFloat(ing.percentage) > maxVal) {
            violationDetails.push(`${ing.inci_name} ${ing.percentage}% — ${reg.source} 최대 ${maxVal}% 초과`)
          }
        }
      }
    }

    // knowledge_base의 max_concentration도 검사
    const { rows: kbRows } = await pool.query(
      "SELECT search_key, data FROM coching_knowledge_base WHERE category = 'INGREDIENT_REGULATION' AND (data->>'inci_name') = ANY($1)",
      [inciNames]
    )
    for (const ing of ingredients) {
      const kb = kbRows.find(r => r.data?.inci_name === ing.inci_name)
      if (kb?.data?.max_concentration) {
        const maxMatch = kb.data.max_concentration.match(/([\d.]+)\s*%/)
        if (maxMatch) {
          const maxVal = parseFloat(maxMatch[1])
          if (!isNaN(maxVal) && parseFloat(ing.percentage) > maxVal) {
            violationDetails.push(`${ing.inci_name} ${ing.percentage}% — DB 최대 ${maxVal}% 초과`)
          }
        }
      }
    }

    const regStatus = violationDetails.length > 0 ? 'warn' : 'pass'
    checks.push({
      name: '규제 농도 검사',
      status: regStatus,
      message: violationDetails.length > 0 ? '일부 성분이 규제 농도를 초과했습니다.' : '모든 성분이 규제 범위 이내입니다.',
      detail: violationDetails.length > 0 ? violationDetails.join('; ') : `${regRows.length}건 규제 확인, 이상 없음`,
    })

    // 3. 방부제 존재 여부 확인
    const preservatives = ingredients.filter(i => {
      const name = (i.inci_name || '').toLowerCase()
      const type = (i.type || '').toUpperCase()
      return type === 'PRESERVATIVE' ||
        name.includes('phenoxyethanol') ||
        name.includes('paraben') ||
        name.includes('benzoic acid') ||
        name.includes('sorbic acid') ||
        name.includes('dehydroacetic acid') ||
        name.includes('benzyl alcohol')
    })
    const preservativeStatus = preservatives.length > 0 ? 'pass' : 'warn'
    checks.push({
      name: '방부제 확인',
      status: preservativeStatus,
      message: preservatives.length > 0 ? '방부제 성분이 포함되어 있습니다.' : '방부제 성분이 없습니다.',
      detail: preservatives.length > 0
        ? preservatives.map(p => `${p.inci_name} ${p.percentage}%`).join(', ')
        : '방부제 성분이 없습니다. 제품 안정성 검토 필요',
    })

    // 4. pH 조절제 존재 여부
    const phAdjusters = ingredients.filter(i => {
      const name = (i.inci_name || '').toLowerCase()
      const type = (i.type || '').toUpperCase()
      return type === 'PH_ADJUSTER' ||
        name.includes('sodium hydroxide') ||
        name.includes('citric acid') ||
        name.includes('triethanolamine') ||
        name.includes('lactic acid') ||
        name.includes('phosphoric acid') ||
        name.includes('arginine')
    })
    const phStatus = phAdjusters.length > 0 ? 'pass' : 'warn'
    checks.push({
      name: 'pH 조절제',
      status: phStatus,
      message: phAdjusters.length > 0 ? 'pH 조절제가 포함되어 있습니다.' : 'pH 조절제가 없습니다.',
      detail: phAdjusters.length > 0
        ? phAdjusters.map(p => `${p.inci_name} 존재`).join(', ')
        : 'pH 조절제가 없습니다. 적정 pH(4.5~7.0) 유지 검토 필요',
    })

    // 최종 pass 여부: fail이 하나라도 있으면 false, warn만이면 true
    const hasFail = checks.some(c => c.status === 'fail')
    if (hasFail) passed = false

    return res.json({
      success: true,
      data: {
        passed,
        totalPercentage: totalPct,
        checks,
        validatedAt: new Date().toISOString(),
      },
    })
  } catch (err) {
    res.status(500).json({ success: false, error: err.message })
  }
})

const PORT = process.env.API_PORT || 3001
app.listen(PORT, () => {
  console.log(`[MyLab API] Running on http://localhost:${PORT}`)
})
