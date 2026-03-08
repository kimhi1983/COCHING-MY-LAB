import request from '@/utils/request'

const getMyFavoriteList = async function(params){
    return request({
        url: '/api/favorite/my/list.api',
        method: 'post',
        data: params
    });
};

const toggleMyFavorite = async function(params){
    return request({
        url: '/api/favorite/my/toggle.api',
        method: 'post',
        data: params
    });
};

const removeMyFavorite = async function(params){
    return request({
        url: '/api/favorite/my/del.api',
        method: 'post',
        data: params
    });
};


export {
    getMyFavoriteList,
    toggleMyFavorite,
    removeMyFavorite,
}