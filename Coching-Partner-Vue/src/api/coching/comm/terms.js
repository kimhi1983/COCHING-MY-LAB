import request from '@/utils/request'

// 약관
// code grp_code codeName
// 001	QP009	가입 이용약관
// 002	QP009	가입 제3자 정보제공 동의
// 003	QP009	계약생성 서비스 이용동의
// 004	QP009	계약생성 매출채권 양도 동의
// 005	QP009	계약합의 서비스 이용동의
// 006	QP009	결제 제3자 지급이체 동의
// 007	QP009	결제 pg이용약관 동의
// 008	QP009	결제 약관동의
// 009	QP009	결제 취소 불가 동의

const getTermList = async function(params){
    return request({
        url: '/api/terms/list.api',
        method: 'post',
        data: params
    });
};

export {
    getTermList,
}