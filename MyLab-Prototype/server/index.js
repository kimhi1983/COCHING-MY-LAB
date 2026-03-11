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

      formulaIngredients.push({
        id: row.id,
        name: row.search_key,
        inci_name: inciName,
        percentage: pct,
        type: guessType(inciName, data),
        function: guessFunction(inciName, data),
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

const PORT = process.env.API_PORT || 3001
app.listen(PORT, () => {
  console.log(`[MyLab API] Running on http://localhost:${PORT}`)
})
