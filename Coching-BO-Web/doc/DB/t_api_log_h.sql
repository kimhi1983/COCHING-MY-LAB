-- Table: coching.t_api_log_h

-- DROP TABLE IF EXISTS coching.t_api_log_h CASCADE;
CREATE TABLE IF NOT EXISTS coching.t_api_log_h
(
    rgt_dttm TIMESTAMP NOT NULL,

    site_type VARCHAR(20),
    memb_seq INTEGER NOT NULL,    
    log_type VARCHAR(20),
    router_nm VARCHAR(100),
    access_ip VARCHAR(100),
    proc_status CHAR(1),
    api_uri VARCHAR(2000),
    api_params JSONB,
    api_res JSONB    
) PARTITION BY RANGE (rgt_dttm);


ALTER TABLE IF EXISTS coching.t_api_log_h
    OWNER to coching;

COMMENT ON TABLE coching.t_api_log_h
    IS 'API 요청 로그';

COMMENT ON COLUMN coching.t_api_log_h.rgt_dttm
    IS '로그 시간';

COMMENT ON COLUMN coching.t_api_log_h.site_type
    IS '사이트 타입';

COMMENT ON COLUMN coching.t_api_log_h.memb_seq
    IS '사용자 SEQ';

COMMENT ON COLUMN coching.t_api_log_h.log_type
    IS '로그 타입';

COMMENT ON COLUMN coching.t_api_log_h.access_ip
    IS '요청 IP';

COMMENT ON COLUMN coching.t_api_log_h.router_nm
    IS '라우터명';

COMMENT ON COLUMN coching.t_api_log_h.proc_status
    IS '처리상태 (요청:0, 응답:1, 에러:E)';

COMMENT ON COLUMN coching.t_api_log_h.api_uri
    IS 'api uri';

COMMENT ON COLUMN coching.t_api_log_h.api_params
    IS '요청 파라미터 JSON';

COMMENT ON COLUMN coching.t_api_log_h.api_res
    IS '응답 JSON';


/*
 * 파티션 생성 함수
 */
CREATE OR REPLACE FUNCTION coching.create_monthly_partition(v_table_name TEXT, v_schema_name TEXT, v_parted_date TIMESTAMP)
RETURNS VOID AS $$
DECLARE
    v_partition_name TEXT;
    v_start_date DATE;
    v_end_date DATE;
BEGIN
    -- 새로 들어올 데이터의 `created_at` 값에서 월의 시작일 구하기
    v_start_date := DATE_TRUNC('month', v_parted_date);
    v_end_date := v_start_date + INTERVAL '1 month';
    v_partition_name := v_table_name || '_' || TO_CHAR(v_start_date, 'YYYYMM');  -- 동적으로 테이블명 생성

    -- 해당 월의 파티션이 존재하는지 확인 후, 없으면 생성
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables 
        WHERE table_name = v_partition_name 
        AND table_schema = v_schema_name
    ) THEN
        EXECUTE FORMAT(
            'CREATE TABLE %I.%I PARTITION OF %I.%I 
            FOR VALUES FROM (%L) TO (%L)',
            v_schema_name, v_partition_name,  -- 스키마명과 파티션명
            v_schema_name, v_table_name,      -- 스키마명과 부모 테이블명
            v_start_date, v_end_date          -- 범위 값 설정
        );
    END IF;
END;
$$ LANGUAGE plpgsql;


--Test
SELECT coching.create_monthly_partition('t_api_log_h', 'coching', '2024-03-10'::TIMESTAMP);

INSERT INTO coching.t_api_log_h (rgt_dttm, site_type, memb_seq, log_type, router_nm, access_ip, proc_status, api_uri, api_params, api_res)
VALUES ('2024-03-10 12:34:56', 'site1', 12345, 'INFO', 'routerA', '192.168.0.1', '0', '/api/test', '{}', '{}');