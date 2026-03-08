import request from '@/utils/request'

const getList = async function (params){
    return request({
        url: '/api/raw/list.api',
        method: 'post',
        data: params
    });
};

const getRaw = async function (params) {
    return request({
        url: '/api/raw/get.api',
        method: 'post',
        data: params
    });
};

const getCmTypeList = async function (params) {
    return request({
        url: '/api/raw/getCmTypeList.api',
        method: 'post',
        data: params
    });
};

const setRawState = async function (params) {
    return request({
        url: '/api/raw/setState.api',
        method: 'post',
        data: params
    });
};

export {
    getList,
    getRaw,
    getCmTypeList,

    setRawState
}