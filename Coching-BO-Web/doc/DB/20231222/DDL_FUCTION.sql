create or replace FUNCTION "SF_QP_FN_PASSWD" (
        P_PASSWD IN VARCHAR2
      , P_SALT IN VARCHAR2  
       )
       
RETURN VARCHAR2 IS
        v_return VARCHAR2(200) ;

BEGIN
    /*
        ============================================================================================
            비밀번호 SHA512 해시 함수
        ===========================================================================================
        1. 파라미터 : 
                   P_PASSWD(비밀번호)
                   P_SALT  (salt 값)

        2. 설명 : 비밀번호 Hash값 생성한다.

        ===========================================================================================
    */

    SELECT STANDARD_HASH(CAST (CONCAT(P_PASSWD, P_SALT) as varchar2(300)), 'SHA512')     
    INTO v_return
    FROM DUAL;

    RETURN v_return;

END;