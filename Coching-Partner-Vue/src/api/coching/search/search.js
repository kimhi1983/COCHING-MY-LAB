import request from '@/utils/request'
import axios from 'axios';
const apiBase = process.env.VUE_APP_API_BASE_URL;

const getStaticIngredientList = async function (params) {
    const reqUrl = `${apiBase}/api/search/static/ingredientAll.api`;
    const response = await axios.get(reqUrl);
  
    return response;
  };

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

const getIngredientAll = async function(params){
    return request({
        url: '/api/search/ingredientAll.api',
        method: 'post',
        data: params
    });
};

const getSearchRawsResult = async function(params){
    return request({
        url: '/api/search/raws.api',
        method: 'post',
        data: params
    });
};

export {
    getSearchProdList,
    getSearchProd,

    getSearchIngredient,
    getSearchIngredients,
    getIngredientAll,
    getStaticIngredientList,

    getSearchRawsResult,
}
