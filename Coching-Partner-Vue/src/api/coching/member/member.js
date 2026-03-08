import request from '@/utils/request'

const getMemInfo = async function(params){
    return request({
        url: '/api/member/membInfo.api',
        method: 'post',
        data: params
    });
};

const getMembList = async function(params){
    return request({
        url: '/api/member/list.api',
        method: 'post',
        data: params
    });
};

const setUseYn = async function(params){
    return request({
        url: '/api/member/update/useYn.api',
        method: 'post',
        data: params
    });
};

const deleteMemb = async function(params){
    return request({
        url: '/api/member/delete.api',
        method: 'post',
        data: params
    });
};

const withdrawInfo = async function(params){
    return request({
        url: '/api/member/withdrawInfo.api',
        method: 'post',
        data: params
    });
};

const withdrawMember = async function(params){
    return request({
        url: '/api/member/withdraw.api',
        method: 'post',
        data: params
    });
};

const updatePwChngDttm = async function (params) {
    return request({
        url: '/api/member/update/pwChngDttm.api',
        method: 'post',
        data: params
    });
};

const initPasswd = async function (params) {
    return request({
        url: '/api/member/initPasswd.api',
        method: 'post',
        data: params
    });
};

const changePasswd = async function (params) {
    return request({
        url: '/api/member/changePasswd.api',
        method: 'post',
        data: params
    });
};

export {
    getMemInfo,
    getMembList,
    setUseYn,
    deleteMemb,

    withdrawInfo,
    withdrawMember,
    updatePwChngDttm,
    initPasswd,
    changePasswd,
}