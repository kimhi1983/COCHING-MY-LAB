import request from '@/utils/request'

const getMyInfo = async function(params){
    return request({
        url: '/api/member/myInfo.api',
        method: 'post',
        data: params
    });
};

const updateMemb = async function(params){
    return request({
        url: '/api/member/updateMemb.api',
        method: 'post',
        data: params
    });
};

const updatePartner = async function(params){
    return request({
        url: '/api/member/updatePartner.api',
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

const uploadProfileFile = async function(formData){
    return request({
        url: '/api/member/setProfileFile.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

export {
    getMyInfo,
    updateMemb,
    updatePartner,
    deleteMemb,

    withdrawInfo,
    withdrawMember,
    updatePwChngDttm,
    uploadProfileFile,
}