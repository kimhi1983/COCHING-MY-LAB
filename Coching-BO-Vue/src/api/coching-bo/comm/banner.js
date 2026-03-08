import request from '@/utils/request'

const getBannerList = async function(params){
    return request({
        url: '/api/banner/list.api',
        method: 'post',
        data: params
    });
};

const addBanner = async function(formData){
    return request({
        url: '/api/banner/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const setBanner = async function(formData){
    return request({
        url: '/api/banner/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const getBanner = async function(params) {
    return request({
        url: '/api/banner/get.api',
        method: 'post',
        data: params
    });
}

const getBannerFile = async function(params) {
    return request({
        url: '/api/banner/getBannerFile.api',
        method: 'post',
        data: params
    });
}

const delBanner = async function(params) {
    return request({
        url: '/api/banner/del.api',
        method: 'post',
        data: params
    });
}

const delBanners = async function(params) {
    return request({
        url: '/api/banner/dels.api',
        method: 'post',
        data: params
    });
}

const setBannerOrder = async function(params) {
    return request({
        url: '/api/banner/setOrders.api',
        method: 'post',
        data: params
    });
}

const setBannerState = async function(params) {
    return request({
        url: '/api/banner/setState.api',
        method: 'post',
        data: params
    });
}

const getBannerAdList = async function(params){
    return request({
        url: '/api/banner/ad/list.api',
        method: 'post',
        data: params
    });
};

const addBannerAd = async function(formData){
    return request({
        url: '/api/banner/ad/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const setBannerAd = async function(formData){
    return request({
        url: '/api/banner/ad/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};


const getBannerAd = async function(params) {
    return request({
        url: '/api/banner/ad/get.api',
        method: 'post',
        data: params
    });
}

const delBannerAd = async function(params) {
    return request({
        url: '/api/banner/ad/del.api',
        method: 'post',
        data: params
    });
}

const setBannerAdState = async function(params) {
    return request({
        url: '/api/banner/ad/setState.api',
        method: 'post',
        data: params
    });
}

const setBannerAdOrder = async function(params) {
    return request({
        url: '/api/banner/ad/setOrders.api',
        method: 'post',
        data: params
    });
}

const delBannerAds = async function(params) {
    return request({
        url: '/api/banner/ad/dels.api',
        method: 'post',
        data: params
    });
}

export {
    getBannerList,
    addBanner,
    setBanner,
    getBanner,
    getBannerFile,
    delBanner,
    delBanners,
    setBannerOrder,
    setBannerState,

    getBannerAdList,
    addBannerAd,
    setBannerAd,
    getBannerAd,
    delBannerAd,
    setBannerAdState,
    setBannerAdOrder,
    delBannerAds,
}