import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const getIngredientList = async function (params) {
  const reqUrl = `${apiBase}/api/code/static/ingredient/names.api`;
  const response = await axios.get(reqUrl);

  return response;
};

const getEsIngredientList = async function(params){
  return request({
      url: '/api/search/ingredients.api',
      method: 'post',
      data: params
  });
};

const getEsIngredient = async function(params){
  return request({
      url: '/api/search/ingredient.api',
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

const getSearchRaw = async function(params){
  return request({
      url: '/api/search/raw.api',
      method: 'post',
      data: params
  });
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



export {
  getIngredientList,
  getEsIngredient,
  getEsIngredientList,

  getSearchRawList,
  getSearchRaw,

  getSearchProdList,
  getSearchProd,

  getSearchTvList,
  getSearchTv,
}