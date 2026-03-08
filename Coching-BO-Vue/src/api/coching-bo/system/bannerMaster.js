import request from '@/utils/request'

const getBannerMasterList = async function(params){
    return request({
        url: '/api/banner/mst/list.api',
        method: 'post',
        data: params
    });
};

const getBannerMaster = async function(params){
    return request({
        url: '/api/banner/mst/get.api',
        method: 'post',
        data: params
    });
};

const setBannerMasterState = async function(params) {
    return request({
        url: '/api/banner/mst/setState.api',
        method: 'post',
        data: params
    });
}

export {
    getBannerMasterList,
    getBannerMaster,
    setBannerMasterState,
}