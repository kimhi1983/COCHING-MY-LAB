"""
COCHING AI — PostgreSQL DB 접근 레이어
asyncpg 기반 연결 풀 + 원료/규제/제품 조회
"""
import os, logging
from typing import Optional

log = logging.getLogger("coching-ai")

# asyncpg 사용 가능 여부
try:
    import asyncpg
    _HAS_ASYNCPG = True
except ImportError:
    _HAS_ASYNCPG = False
    log.warning("asyncpg 미설치 — DB 기능 비활성")

_pool: Optional[object] = None
DB_AVAILABLE = False


async def init_db_pool():
    """FastAPI startup 시 호출 — 연결 풀 생성"""
    global _pool, DB_AVAILABLE
    if not _HAS_ASYNCPG:
        return
    db_url = os.getenv("COCHING_DB_URL", "")
    if not db_url:
        log.warning("COCHING_DB_URL 미설정 — DB 기능 비활성")
        return
    try:
        _pool = await asyncpg.create_pool(db_url, min_size=2, max_size=10,
                                          command_timeout=10)
        # 연결 테스트
        async with _pool.acquire() as conn:
            cnt = await conn.fetchval("SELECT count(*) FROM ingredient_master")
        DB_AVAILABLE = True
        log.info(f"PostgreSQL 연결 성공 — ingredient_master: {cnt}건")
    except Exception as e:
        log.warning(f"PostgreSQL 연결 실패 — DB 기능 비활성: {e}")
        _pool = None
        DB_AVAILABLE = False


async def close_db_pool():
    """FastAPI shutdown 시 호출"""
    global _pool, DB_AVAILABLE
    if _pool:
        await _pool.close()
        _pool = None
    DB_AVAILABLE = False


# ─── 원료 검색 ───────────────────────────────────────────────────────────────

async def search_ingredients(query: str, ingredient_type: str = None,
                              limit: int = 20) -> list[dict]:
    """INCI명/한글명/별명으로 원료 검색"""
    if not DB_AVAILABLE or not _pool:
        return []
    try:
        async with _pool.acquire() as conn:
            q = query.strip()
            like = f"%{q}%"
            if ingredient_type:
                rows = await conn.fetch("""
                    SELECT id, inci_name, korean_name, cas_number, ec_number,
                           ingredient_type, description, origin, source
                    FROM ingredient_master
                    WHERE ingredient_type = $1
                      AND (inci_name ILIKE $2 OR korean_name ILIKE $2
                           OR array_to_string(aliases, ',') ILIKE $2)
                    ORDER BY inci_name LIMIT $3
                """, ingredient_type, like, limit)
            else:
                rows = await conn.fetch("""
                    SELECT id, inci_name, korean_name, cas_number, ec_number,
                           ingredient_type, description, origin, source
                    FROM ingredient_master
                    WHERE inci_name ILIKE $1 OR korean_name ILIKE $1
                          OR array_to_string(aliases, ',') ILIKE $1
                    ORDER BY inci_name LIMIT $2
                """, like, limit)
            return [dict(r) for r in rows]
    except Exception as e:
        log.warning(f"search_ingredients 실패: {e}")
        return []


async def get_ingredients_by_types(types: list[str]) -> dict[str, list[dict]]:
    """유형별 원료 후보 조회 (가이드처방용)"""
    if not DB_AVAILABLE or not _pool:
        return {}
    result = {}
    try:
        async with _pool.acquire() as conn:
            rows = await conn.fetch("""
                SELECT id, inci_name, korean_name, cas_number,
                       ingredient_type, description
                FROM ingredient_master
                WHERE ingredient_type = ANY($1)
                ORDER BY ingredient_type, inci_name
            """, types)
            for r in rows:
                t = r["ingredient_type"]
                if t not in result:
                    result[t] = []
                result[t].append(dict(r))
    except Exception as e:
        log.warning(f"get_ingredients_by_types 실패: {e}")
    return result


async def get_regulation_data(ingredient_names: list[str]) -> list[dict]:
    """원료명 목록 → 규제/농도제한/EWG 일괄 조회"""
    if not DB_AVAILABLE or not _pool or not ingredient_names:
        return []
    try:
        async with _pool.acquire() as conn:
            # regulation_cache
            reg_rows = await conn.fetch("""
                SELECT ingredient, inci_name, source, max_concentration, restriction
                FROM regulation_cache
                WHERE ingredient = ANY($1) OR inci_name = ANY($1)
            """, ingredient_names)

            # coching_knowledge_base — JSONB에서 EWG/안전성 추출
            kb_rows = await conn.fetch("""
                SELECT search_key, data
                FROM coching_knowledge_base
                WHERE search_key = ANY($1)
                  AND category = 'INGREDIENT_REGULATION'
            """, [n.lower().replace(" ", "_") for n in ingredient_names])

            result = []
            for r in reg_rows:
                result.append({
                    "ingredient": r["ingredient"],
                    "inci_name": r["inci_name"],
                    "source": r["source"],
                    "max_concentration": r["max_concentration"],
                    "restriction": r["restriction"],
                })

            kb_map = {}
            for r in kb_rows:
                try:
                    import json
                    data = json.loads(r["data"]) if isinstance(r["data"], str) else r["data"]
                    kb_map[r["search_key"]] = data
                except Exception:
                    pass

            return result, kb_map
    except Exception as e:
        log.warning(f"get_regulation_data 실패: {e}")
        return [], {}


async def get_reference_products(category: str = None, limit: int = 10) -> list[dict]:
    """참조 제품 조회"""
    if not DB_AVAILABLE or not _pool:
        return []
    try:
        async with _pool.acquire() as conn:
            if category:
                rows = await conn.fetch("""
                    SELECT brand_name, product_name, category,
                           full_ingredients, key_ingredients,
                           product_type, target_skin_type, ph_range
                    FROM product_master
                    WHERE category ILIKE $1
                    ORDER BY id LIMIT $2
                """, f"%{category}%", limit)
            else:
                rows = await conn.fetch("""
                    SELECT brand_name, product_name, category,
                           full_ingredients, key_ingredients,
                           product_type, target_skin_type, ph_range
                    FROM product_master
                    ORDER BY id LIMIT $1
                """, limit)
            return [dict(r) for r in rows]
    except Exception as e:
        log.warning(f"get_reference_products 실패: {e}")
        return []


async def get_ingredient_details(names: list[str]) -> list[dict]:
    """원료명 목록 → 상세 정보 일괄 조회 (기존 formulate 강화용)"""
    if not DB_AVAILABLE or not _pool or not names:
        return []
    try:
        async with _pool.acquire() as conn:
            # 한글명 또는 INCI명으로 매칭
            rows = await conn.fetch("""
                SELECT im.id, im.inci_name, im.korean_name, im.cas_number,
                       im.ingredient_type, im.description,
                       rc.max_concentration, rc.restriction, rc.source AS reg_source
                FROM ingredient_master im
                LEFT JOIN regulation_cache rc
                  ON (rc.inci_name = im.inci_name OR rc.ingredient = im.korean_name)
                WHERE im.inci_name = ANY($1)
                   OR im.korean_name = ANY($1)
                   OR lower(im.inci_name) = ANY($2)
                ORDER BY im.inci_name
            """, names, [n.lower() for n in names])
            return [dict(r) for r in rows]
    except Exception as e:
        log.warning(f"get_ingredient_details 실패: {e}")
        return []


async def get_db_known_ingredients() -> set[str]:
    """DB에 등록된 모든 INCI명 반환 (KNOWN_INGREDIENTS 대체용)"""
    if not DB_AVAILABLE or not _pool:
        return set()
    try:
        async with _pool.acquire() as conn:
            rows = await conn.fetch(
                "SELECT lower(inci_name) AS name FROM ingredient_master")
            return {r["name"] for r in rows}
    except Exception as e:
        log.warning(f"get_db_known_ingredients 실패: {e}")
        return set()


async def get_db_stats() -> dict:
    """DB 통계 (health 엔드포인트용)"""
    if not DB_AVAILABLE or not _pool:
        return {}
    try:
        async with _pool.acquire() as conn:
            stats = {}
            for table in ["ingredient_master", "product_master",
                          "regulation_cache", "coching_knowledge_base"]:
                cnt = await conn.fetchval(f"SELECT count(*) FROM {table}")
                stats[table] = cnt
            return stats
    except Exception as e:
        log.warning(f"get_db_stats 실패: {e}")
        return {}


def format_db_enrichment(details: list[dict], kb_data: dict = None) -> str:
    """DB 조회 결과를 Claude 프롬프트용 텍스트로 변환"""
    if not details:
        return ""

    lines = [f"\n[DB 성분 데이터 — {len(details)}개 매칭]"]
    lines.append("| 성분명 | INCI | 유형 | CAS | 최대농도 | 규제 |")
    lines.append("|--------|------|------|-----|---------|------|")

    seen = set()
    for d in details:
        inci = d.get("inci_name", "")
        if inci in seen:
            continue
        seen.add(inci)
        korean = d.get("korean_name", "") or ""
        itype = d.get("ingredient_type", "") or ""
        cas = d.get("cas_number", "") or ""
        maxc = d.get("max_concentration", "") or ""
        restriction = d.get("restriction", "") or ""
        if len(restriction) > 40:
            restriction = restriction[:40] + "..."
        lines.append(f"| {korean} | {inci} | {itype} | {cas} | {maxc} | {restriction} |")

    return "\n".join(lines)


def format_reference_products(products: list[dict]) -> str:
    """참조 제품을 Claude 프롬프트용 텍스트로 변환"""
    if not products:
        return ""

    lines = [f"\n[참고 제품 — {len(products)}건]"]
    for p in products:
        brand = p.get("brand_name", "")
        name = p.get("product_name", "")
        cat = p.get("category", "")
        skin = p.get("target_skin_type", "")
        ph = p.get("ph_range", "")
        ingr = p.get("full_ingredients", "")
        if ingr and len(ingr) > 200:
            ingr = ingr[:200] + "..."
        lines.append(f"- **{brand} {name}** ({cat}, {skin}, pH {ph})")
        if ingr:
            lines.append(f"  전성분: {ingr}")

    return "\n".join(lines)
