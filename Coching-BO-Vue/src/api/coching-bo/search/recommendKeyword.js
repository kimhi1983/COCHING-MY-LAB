import request from '@/utils/request'

const getRecommendKeywordList = async function(params){
    return request({
        url: '/api/search/recommendKeyword/list.api',
        method: 'post',
        data: params
    });
};

const getRecommendKeyword = async function(params){
    return request({
        url: '/api/search/recommendKeyword/get.api',
        method: 'post',
        data: params
    });
};

const setRecommendKeyword = async function(params){
    return request({
        url: '/api/search/recommendKeyword/set.api',
        method: 'post',
        data: params
    });
};

const delRecommendKeyword = async function(params){
    return request({
        url: '/api/search/recommendKeyword/del.api',
        method: 'post',
        data: params
    });
};

const setRecommendKeywords = async function(params){
    return request({
        url: '/api/search/recommendKeyword/sets.api',
        method: 'post',
        data: params
    });
};

const delRecommendKeywords = async function(params){
    return request({
        url: '/api/search/recommendKeyword/dels.api',
        method: 'post',
        data: params
    });
};

const setRecommendKeywordOrders = async function(params){
    return request({
        url: '/api/search/recommendKeyword/setOrders.api',
        method: 'post',
        data: params
    });
};


export {
    getRecommendKeywordList,
    getRecommendKeyword,

    setRecommendKeyword, 
    setRecommendKeywords,
    delRecommendKeyword,    
    delRecommendKeywords,

    setRecommendKeywordOrders,
}