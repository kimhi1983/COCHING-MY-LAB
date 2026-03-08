import request from '@/utils/request'



const getPopupList = async function(params){
    return request({
        url: '/api/popup/list.api',
        method: 'post',
        data: params
    });
};

const addPopup = async function(formData){
    return request({
        url: '/api/popup/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const setPopup = async function(formData){
    return request({
        url: '/api/popup/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const getPopup = async function(params) {
    return request({
        url: '/api/popup/get.api',
        method: 'post',
        data: params
    });
}

const getPopupFile = async function(params) {
    return request({
        url: '/api/popup/getPopupFile.api',
        method: 'post',
        data: params
    });
}

const delPopup = async function(params) {
    return request({
        url: '/api/popup/del.api',
        method: 'post',
        data: params
    });
}

const delPopups = async function(params) {
    return request({
        url: '/api/popup/dels.api',
        method: 'post',
        data: params
    });
}

const setPopupOrder = async function(params) {
    return request({
        url: '/api/popup/setOrders.api',
        method: 'post',
        data: params
    });
}
const setPopupState = async function(params) {
    return request({
        url: '/api/popup/setState.api',
        method: 'post',
        data: params
    });
}

export {    
    getPopupList,
    addPopup,
    setPopup,
    getPopup,
    getPopupFile,
    delPopup,
    delPopups,
    setPopupOrder,
    setPopupState
}