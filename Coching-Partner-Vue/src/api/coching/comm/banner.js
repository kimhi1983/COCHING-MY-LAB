import request from '@/utils/request'

const getBannerList = async function(params){
    return request({
        url: '/api/banner/list.api',
        method: 'post',
        data: params
    });
};

const setBannerClick = async function(params){
    return request({
        url: '/api/banner/click.api',
        method: 'post',
        data: params
    });
};

export {
    getBannerList,
    setBannerClick,
}