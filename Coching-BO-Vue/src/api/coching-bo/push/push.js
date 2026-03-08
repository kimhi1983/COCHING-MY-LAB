import request from '@/utils/request'

const insertSendPushInfo = async function(formData){
    return request({
        url: '/api/push/add.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateSendPushInfo = async function(formData){
    return request({
        url: '/api/push/edit.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};


const getSendPushInfoList = async function(params){
    return request({
        url: '/api/push/list.api',
        method: 'post',
        data: params,
    });
};

const getSendPushInfo = async function(params){
    return request({
        url: '/api/push/load.api',
        method: 'post',
        data: params,
    });
};

const getSendPushSendList = async function(params){
    return request({
        url: '/api/push/sendList.api',
        method: 'post',
        data: params,
    });
};


export {
    insertSendPushInfo,
    updateSendPushInfo,
    getSendPushInfoList,
    getSendPushInfo,
    getSendPushSendList
}