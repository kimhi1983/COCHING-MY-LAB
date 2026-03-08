import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const getLabMasterList = async function (params) {
  return request({
      url: '/api/rnd/labMaster/list.api',
      method: 'post',
      data: params,
  });
};

const getLabMasterDetail = async function (params) {
  return request({
      url: '/api/rnd/labMaster/get.api',
      method: 'post',
      data: params,
  });
};

const addLabMaster = async function (params) {
  return request({
      url: '/api/rnd/labMaster/add.api',
      method: 'post',
      data: params,
  });
};

const setLabMaster = async function (params) {
  return request({
      url: '/api/rnd/labMaster/set.api',
      method: 'post',
      data: params,
  });
};

const deleteLabMaster = async function (params) {
  return request({
      url: '/api/rnd/labMaster/del.api',
      method: 'post',
      data: params,
  });
};

const getLabAiResList = async function (params) {
  return request({
      url: '/api/rnd/aiLabRes/list.api',
      method: 'post',
      data: params,
  });
};

const getAiPrscResult = async function (params) {
  return request({
      url: '/api/rnd/aiPrscResultV1.api',
      method: 'post',
      data: params,
      timeout: 1000000,
  });
};



export {
  getLabMasterList,
  getLabMasterDetail,
  addLabMaster,
  setLabMaster,
  deleteLabMaster,
  getLabAiResList,
  getAiPrscResult,
}