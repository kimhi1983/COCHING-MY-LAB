import request from '@/utils/request'

const getAllimMessageList = async function(params){
    return request({
        url: '/api/message/allim/list.api',
        method: 'post',
        data: params
    });
};

const getAllimMessageCount = async function(params){
    return request({
        url: '/api/message/allim/count.api',
        method: 'post',
        data: params
    });
};

const getAllimMessageDetail = async function(params){
    return request({
        url: '/api/message/allim/get.api',
        method: 'post',
        data: params
    });
};

const updateViewCountForAllimMessage = async function(params){
    return request({
        url: '/api/message/allim/click.api',
        method: 'post',
        data: params
    });
};

const setPushToken = async function(params){
    return request({
        url: '/api/message/allim/setPushToken.api',
        method: 'post',
        data: params,
    });
};

const getUserMessageList = async function(params){
    return request({
        url: '/api/message/list.api',
        method: 'post',
        data: params
    });
};

const getUserMessageDetail = async function(params){
    return request({
        url: '/api/message/get.api',
        method: 'post',
        data: params
    });
};

const sendUserMessage = async function(params){
    return request({
        url: '/api/message/send.api',
        method: 'post',
        data: params
    });
};

const removeUserMessage = async function(params){
    return request({
        url: '/api/message/del.api',
        method: 'post',
        data: params
    });
};



export {
    getAllimMessageList,
    getAllimMessageCount,
    getAllimMessageDetail,
    updateViewCountForAllimMessage,
    setPushToken,

    getUserMessageList,
    getUserMessageDetail,
    sendUserMessage,
    removeUserMessage,
}