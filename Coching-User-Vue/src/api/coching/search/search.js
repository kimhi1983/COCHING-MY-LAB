import request from '@/utils/request'

const getSearchProdList = async function(params){
    return request({
        url: '/api/search/products.api',
        method: 'post',
        data: params
    });
};

const getSearchProd = async function(params){
    return request({
        url: '/api/search/product.api',
        method: 'post',
        data: params
    });
};

const getSearchIngredient = async function(params){
    return request({
        url: '/api/search/ingredient.api',
        method: 'post',
        data: params
    });
};

const getSearchIngredients = async function(params){
    return request({
        url: '/api/search/ingredients.api',
        method: 'post',
        data: params
    });
};

const getSearchRawList = async function(params){
    return request({
        url: '/api/search/raws.api',
        method: 'post',
        data: params
    });
};

const getSearchRawMainList = async function(params){
    return request({
        url: '/api/search/raws/main.api',
        method: 'post',
        data: params
    });
};

const getSearchRaw = async function(params){
    return request({
        url: '/api/search/raw.api',
        method: 'post',
        data: params
    });
};

const getSearchTvList = async function(params){
    return request({
        url: '/api/search/tvs.api',
        method: 'post',
        data: params
    });
};

const getSearchTv = async function(params){
    return request({
        url: '/api/search/tv.api',
        method: 'post',
        data: params
    });
};

const getRecommendKeywords = async function(params){
    return request({
        url: '/api/search/recommendKeyword/list.api',
        method: 'post',
        data: params
    });
};

const getSuggestionList = async function(params){
    return request({
        url: '/api/search/suggestion.api',
        method: 'post',
        data: params
    });
};

const getIngredientNationLimitList = async function(params){
    return request({
        url: '/api/search/ingd/nationLimits.api',
        method: 'post',
        data: params
    });
};

const getIngredientNationExpLimitList = async function(params){ 
    return request({
        url: '/api/search/ingd/nationExpLimits.api',
        method: 'post',
        data: params
    });
};

export {
    getSearchProdList,
    getSearchProd,

    getSearchIngredient,
    getSearchIngredients,

    getSearchRawList,
    getSearchRawMainList,
    getSearchRaw,

    getSearchTvList,
    getSearchTv,

    getRecommendKeywords,

    getSuggestionList,
    getIngredientNationLimitList,
    getIngredientNationExpLimitList,
}
