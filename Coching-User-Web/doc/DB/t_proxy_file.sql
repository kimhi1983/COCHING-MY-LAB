-- Table: coching.t_proxy_file

-- DROP TABLE IF EXISTS coching.t_proxy_file;

CREATE TABLE IF NOT EXISTS coching.t_proxy_file
(
    file_id character varying(200) COLLATE pg_catalog."default" NOT NULL,
    ref_code character varying(50) COLLATE pg_catalog."default" NOT NULL,
    org_url character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_ext character varying(100) COLLATE pg_catalog."default",
    file_name_dest character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_path character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_size integer NOT NULL,
    del_yn character(1) COLLATE pg_catalog."default" NOT NULL DEFAULT 'N'::bpchar,
    rgtr integer NOT NULL,
    rgt_dttm timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chnr integer,
    chng_dttm timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_t_proxy_file PRIMARY KEY (file_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS coching.t_proxy_file
    OWNER to coching;

COMMENT ON TABLE coching.t_proxy_file
    IS '프록시파일';

COMMENT ON COLUMN coching.t_proxy_file.file_id
    IS '파일 ID';

COMMENT ON COLUMN coching.t_proxy_file.ref_code
    IS '참조 CD';

COMMENT ON COLUMN coching.t_proxy_file.org_url
    IS '원본 URL';

COMMENT ON COLUMN coching.t_proxy_file.file_name
    IS '파일명';

COMMENT ON COLUMN coching.t_proxy_file.file_ext
    IS '확장자';

COMMENT ON COLUMN coching.t_proxy_file.file_name_dest
    IS '저장명';

COMMENT ON COLUMN coching.t_proxy_file.file_path
    IS '저장경로';

COMMENT ON COLUMN coching.t_proxy_file.file_size
    IS '파일크기';

COMMENT ON COLUMN coching.t_proxy_file.del_yn
    IS '삭제여부';

COMMENT ON COLUMN coching.t_proxy_file.rgtr
    IS '등록자';

COMMENT ON COLUMN coching.t_proxy_file.rgt_dttm
    IS '등록일';

COMMENT ON COLUMN coching.t_proxy_file.chnr
    IS '수정자';

COMMENT ON COLUMN coching.t_proxy_file.chng_dttm
    IS '수정일';


-- 인덱스 생성
CREATE UNIQUE INDEX idx_unique_org_url
ON coching.t_proxy_file (org_url);    