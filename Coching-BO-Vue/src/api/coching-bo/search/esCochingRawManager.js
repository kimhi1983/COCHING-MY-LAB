import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const getEsCochingRawManageList = async function(params){
    return request({
        url: '/api/search/manager/coching/raw/list.api',
        method: 'post',
        data: params
    });
};

const getEsCochingRawManageOne = async function(params){
  return request({
      url: '/api/search/manager/coching/raw/load.api',
      method: 'post',
      data: params
  });
};

const getEsCochingRawIndices = async function(params){
  return request({
      url: '/api/search/manager/coching/raw/indices.api',
      method: 'post',
      data: params
  });
};

const resetEsCochingRawTable = async function(params){
  return request({
      url: '/api/search/manager/coching/raw/resetTable.api',
      method: 'post',
      data: params,
      timeout: 1000000,
  });
};


const getEsCochingRawCreateEsIndex = async function(params){
  return request({
      url: '/api/search/manager/coching/raw/createIndex.api',
      method: 'post',
      data: params
  });
};


export {
  getEsCochingRawManageList,
  getEsCochingRawManageOne,

  resetEsCochingRawTable,
  getEsCochingRawCreateEsIndex,
  
  getEsCochingRawIndices,
  
}