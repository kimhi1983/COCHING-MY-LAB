Insert into T_TERMS_INF (TERMS_CD,VERSION,TERMS_TYPE,CONTENT, USE_YN,REQUIRED_YN,FROM_DATE,TO_DATE,REMARKS,RGTR,RGT_DTTM,CHNR,CHNG_DTTM) values ('019','Ver.0001','TERMS','<div class="article-h1">카드 결제 승인 이후 취소는 불가능합니다.</div>','Y','Y','23/10/23  ','2050-12-31','카드 결제 승인 이후 취소는 불가능합니다.',1,to_date('24/01/24','RR/MM/DD'),1,to_date('24/01/24','RR/MM/DD'));
Insert into T_TERMS_INF (TERMS_CD,VERSION,TERMS_TYPE,CONTENT,USE_YN,REQUIRED_YN,FROM_DATE,TO_DATE,REMARKS,RGTR,RGT_DTTM,CHNR,CHNG_DTTM) values ('020','Ver.0001','TERMS','<div class="article-h1">Cancellation is not possible after card payment approval.</div>','Y','Y','23/10/23  ','2050-12-31','카드 결제 승인 이후 취소는 불가능합니다.',1,to_date('24/01/24','RR/MM/DD'),1,to_date('24/01/24','RR/MM/DD'));

COMMIT;
