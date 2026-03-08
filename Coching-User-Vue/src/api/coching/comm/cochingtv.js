import request from '@/utils/request'

const getCochigtv = async function(params){
    return request({
        url: '/api/cochingtv/get.api',
        method: 'post',
        data: params
    });
};

const getRecommendedList = async function(params){
    return request({
        url: '/api/cochingtv/recommendedList.api',
        method: 'post',
        data: params
    });
};

const getYoutubeInfo = async function(params){
    return request({
        url: '/api/cochingtv/getYoutubeInfo.api',
        method: 'post',
        data: params
    });
};

export {
    getCochigtv,
    getRecommendedList,
    getYoutubeInfo
}