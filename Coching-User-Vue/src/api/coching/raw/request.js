import request from '@/utils/request'

const getRequestList = async function(params){
    return request({
        url: '/api/raw/request/list.api',
        method: 'post',
        data: params
    });
};

const getReceiveList = async function(params){
    return request({
        url: '/api/raw/request/receiveList.api',
        method: 'post',
        data: params
    });
};

const getRequest = async function(params) {
    return request({
        url: '/api/raw/request/get.api',
        method: 'post',
        data: params
    });
}

const setRequest = async function(params){
    return request({
        url: '/api/raw/request/set.api',
        method: 'post',
        data: params
    });
};

const updateStatus = async function(params){
    return request({
        url: '/api/raw/request/updateStatus.api',
        method: 'post',
        data: params
    });
};

const getReply = async function(params) {
    return request({
        url: '/api/raw/request/getReply.api',
        method: 'post',
        data: params
    });
}

const setReply = async function(formData){
    return request({
        url: '/api/raw/request/setReply.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateReplyDelYn = async function(params){
    return request({
        url: '/api/raw/request/updateReplyDelYn.api',
        method: 'post',
        data: params
    });
};

export {
    getRequestList,
    getReceiveList,
    getRequest,
    setRequest,
    updateStatus,
    getReply,
    setReply,
    updateReplyDelYn,
}