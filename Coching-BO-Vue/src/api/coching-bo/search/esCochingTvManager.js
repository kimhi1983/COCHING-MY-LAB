import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const getEsCochingTvManageList = async function(params){
    return request({
        url: '/api/search/manager/coching/tv/list.api',
        method: 'post',
        data: params
    });
};

const getEsCochingTvManageOne = async function(params){
  return request({
      url: '/api/search/manager/coching/tv/load.api',
      method: 'post',
      data: params
  });
};

const getEsCochingTvIndices = async function(params){
  return request({
      url: '/api/search/manager/coching/tv/indices.api',
      method: 'post',
      data: params
  });
};

const resetEsCochingTvTable = async function(params){
  return request({
      url: '/api/search/manager/coching/tv/resetTable.api',
      method: 'post',
      data: params,
      timeout: 1000000,
  });
};


const getEsCochingTvCreateEsIndex = async function(params){
  return request({
      url: '/api/search/manager/coching/tv/createIndex.api',
      method: 'post',
      data: params
  });
};


export {
  getEsCochingTvManageList,
  getEsCochingTvManageOne,

  resetEsCochingTvTable,
  getEsCochingTvCreateEsIndex,
  
  getEsCochingTvIndices,
  
}