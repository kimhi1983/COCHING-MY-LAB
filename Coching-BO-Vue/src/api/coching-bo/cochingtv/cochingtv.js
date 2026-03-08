import request from '@/utils/request'

const getCochigtvList = async function(params){
    return request({
        url: '/api/cochingtv/list.api',
        method: 'post',
        data: params
    });
};

const addCochigtv = async function(params){
    return request({
        url: '/api/cochingtv/new.api',
        method: 'post',
        data: params
    });
};

const setCochingtvState = async function(params) {
    return request({
        url: '/api/cochingtv/setState.api',
        method: 'post',
        data: params
    });
};

const delCochingtv = async function(params) {
    return request({
        url: '/api/cochingtv/dels.api',
        method: 'post',
        data: params
    });
};

const setCochingtvOrder = async function(params) {
    return request({
        url: '/api/cochingtv/setOrders.api',
        method: 'post',
        data: params
    });
}

const getYoutubeApiInfo = async function(params) {
    return request({
        url: '/api/cochingtv/getYoutubeApiInfo.api',
        method: 'post',
        data: params
    });
};

export {
    getCochigtvList,
    addCochigtv,
    setCochingtvState,
    delCochingtv,
    setCochingtvOrder,
    getYoutubeApiInfo,
}