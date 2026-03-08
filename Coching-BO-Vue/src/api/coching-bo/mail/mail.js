import request from '@/utils/request'

const insertSendMailInfo = async function(formData){
    return request({
        url: '/api/mail/add.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateSendMailInfo = async function(formData){
    return request({
        url: '/api/mail/edit.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};


const getSendMailInfoList = async function(params){
    return request({
        url: '/api/mail/list.api',
        method: 'post',
        data: params,
    });
};

const getSendMailInfo = async function(params){
    return request({
        url: '/api/mail/load.api',
        method: 'post',
        data: params,
    });
};

const getSendMailSendList = async function(params){
    return request({
        url: '/api/mail/sendList.api',
        method: 'post',
        data: params,
    });
};


export {
    insertSendMailInfo,
    updateSendMailInfo,
    getSendMailInfoList,
    getSendMailInfo,
    getSendMailSendList
}