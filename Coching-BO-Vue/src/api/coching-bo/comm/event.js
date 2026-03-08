import request from '@/utils/request'

const getList = async function(params){
    return request({
        url: '/api/event/list.api',
        method: 'post',
        data: params
    });
};

const getDetail = async function(params){
    return request({
        url: '/api/event/get.api',
        method: 'post',
        data: params
    });
};

const setEvent = async function(formData){
    return request({
        url: '/api/event/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const newEvent = async function(formData){
    return request({
        url: '/api/event/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const delEvent = async function(params){
    return request({
        url: '/api/event/del.api',
        method: 'post',
        data: params,
    });
};

export {
    getList,
    getDetail,
    setEvent,
    newEvent,
    delEvent
}