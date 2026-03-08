import request from '@/utils/request'

const getCompNmList = async function (params){
    return request({
        url: '/api/company/compNmList.api',
        method: 'post',
        data: params
    });
};

const getCompanyList = async function (params) {
    return request({
        url: '/api/company/list.api',
        method: 'post',
        data: params
    });
};

const getKorAccount = async function (params) {
    return request({
        url: '/api/company/loadKorAccount.api',
        method: 'post',
        data: params
    });
};

const getMembList = async function (params) {
    return request({
        url: '/api/company/memberList.api',
        method: 'post',
        data: params
    });
};


export {
    getCompNmList,
    getCompanyList,
    getKorAccount,
    getMembList
}