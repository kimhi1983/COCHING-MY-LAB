-- Table: coching.T_PDF_INFO

-- DROP TABLE IF EXISTS coching.T_PDF_INFO CASCADE;
CREATE TABLE IF NOT EXISTS coching.T_PDF_INFO
(
    file_id VARCHAR(200),

    file_name VARCHAR(1000),
    file_ext VARCHAR(100),
    file_name_dest VARCHAR(1000),
    file_path VARCHAR(1000),
    file_size INTEGER,

    rgt_dttm TIMESTAMP NOT NULL,

    page_count INTEGER,
    page_img_path VARCHAR(1000),
    content TEXT,
    content_dttm TIMESTAMP,
    CONSTRAINT t_pdf_info_pkey PRIMARY KEY (file_id)
    
);


ALTER TABLE IF EXISTS coching.T_PDF_INFO
    OWNER to coching;

COMMENT ON TABLE coching.T_PDF_INFO
    IS 'PDF 정보';

COMMENT ON COLUMN coching.T_PDF_INFO.file_id
    IS '파일 ID';

COMMENT ON COLUMN coching.T_PDF_INFO.file_ext
    IS '파일 확장자';

COMMENT ON COLUMN coching.T_PDF_INFO.file_name_dest
    IS '파일 저장이름';

COMMENT ON COLUMN coching.T_PDF_INFO.file_path
    IS '파일 저장위치';

COMMENT ON COLUMN coching.T_PDF_INFO.file_size
    IS '파일 사이즈';

COMMENT ON COLUMN coching.T_PDF_INFO.rgt_dttm
    IS '등록일';

COMMENT ON COLUMN coching.T_PDF_INFO.page_count
    IS '페이지 수';

COMMENT ON COLUMN coching.T_PDF_INFO.page_img_path
    IS '이미지 저장위치';

COMMENT ON COLUMN coching.T_PDF_INFO.content
    IS '추출 텍스트';

COMMENT ON COLUMN coching.T_PDF_INFO.content_dttm
    IS '텍스트 추출일';


