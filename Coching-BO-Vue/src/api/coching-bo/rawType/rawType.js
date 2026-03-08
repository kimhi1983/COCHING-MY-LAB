import request from '@/utils/request'

const getList = async function (params){
    return request({
        url: '/api/rawType/list.api',
        method: 'post',
        data: params
    });
};

const getRawType = async function (params) {
    return request({
        url: '/api/rawType/get.api',
        method: 'post',
        data: params
    });
};

const setRawType = async function (params) {
    return request({
        url: '/api/rawType/new.api',
        method: 'post',
        data: params
    });
};

const updateRawType = async function (params) {
    return request({
        url: '/api/rawType/set.api',
        method: 'post',
        data: params
    });
};

const setRawTypeState = async function (params) {
    return request({
        url: '/api/rawType/setState.api',
        method: 'post',
        data: params
    });
};


export {
    getList,
    getRawType,
    setRawType,
    updateRawType,
    setRawTypeState,
}