import request from '@/utils/request'

const getRawList = async function(params){
    return request({
        url: '/api/raw/list.api',
        method: 'post',
        data: params
    });
};

const getRaw = async function(params){
    return request({
        url: '/api/raw/get.api',
        method: 'post',
        data: params
    });
};

const getDetail = async function(params){
    return request({
        url: '/api/raw/getDetail.api',
        method: 'post',
        data: params
    });
};


export {
    getRawList,
    getRaw,
    getDetail,
}