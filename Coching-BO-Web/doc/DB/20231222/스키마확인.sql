-- 데이터 타입 확인
SELECT * FROM COLS
WHERE TABLE_NAME like 'T_%'
ORDER BY DATA_TYPE
    --, COLUMN_ID, TABLE_NAME,
;


-- 데이터 타입 확인 CLOB
SELECT * FROM COLS
WHERE TABLE_NAME like 'T_%'
    AND DATA_TYPE = 'CLOB'
ORDER BY DATA_TYPE
    --, COLUMN_ID, TABLE_NAME,
;

-- 인덱스 조회
SELECT *
FROM user_indexes
ORDER BY TABLE_NAME, INDEX_NAME
;

--ALTER INDEX IX_T_MEMB_ADMIN_INF_P RENAME TO IX_t_memb_admin_inf_P;
--ALTER INDEX UQ_T_MEMB_ADMIN_INF_1 RENAME TO IX_t_memb_admin_inf_U01;
--ALTER INDEX IX_T_TOKEN_ACCESS_P RENAME TO IX_T_TOKEN_ACCESS_P;
--ALTER INDEX IX_T_TOKEN_BLACKLIST_P RENAME TO IX_T__TOKEN_BLACKLIST_P;
--ALTER INDEX IX_T_TOKEN_REFRESH_P RENAME TO IX_T_TOKEN_REFRESH_P;

--CREATE SYNONYM TB_CM_ESNDG_BACH    FOR c##ndtaxdietd.TB_CM_ESNDG_BACH;


tablespace : "TSD_APPS_DEFT"

-- 테이블 코멘트 조회
SELECT table_name, comments
FROM user_tab_comments
WHERE table_name like 'T%';

-- 테이블 컬럼 코멘트 조회
SELECT table_name, column_name, comments
FROM user_col_comments
WHERE table_name like 'T%'
;
