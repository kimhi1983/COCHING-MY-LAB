import request from '@/utils/request'

const getAdminMembList = async function(params){
    return request({
        url: '/api/member/admin/list.api',
        method: 'post',
        data: params
    });
};

const getAdminMemb = async function(params){
    return request({
        url: '/api/member/admin/get.api',
        method: 'post',
        data: params
    });
};

const checkDuplicateId = async function(params){
    return request({
        url: '/api/member/admin/checkDuplicateId.api',
        method: 'post',
        data: params
    });
};

const insertAdminMemb = async function(params){
    return request({
        url: '/api/member/admin/set.api',
        method: 'post',
        data: params
    });
};

const updateAdminMemb = async function(params){
    return request({
        url: '/api/member/admin/update.api',
        method: 'post',
        data: params
    });
};

const setAdminState = async function(params) {
    return request({
        url: '/api/member/admin/setState.api',
        method: 'post',
        data: params
    });
};



export {
    getAdminMembList,
    getAdminMemb,
    checkDuplicateId,
    insertAdminMemb,
    updateAdminMemb,
    setAdminState
}