import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const resetHwBrandTable = async function(params){
  return request({
      url: '/api/search/manager/coching/hw/resetHwBrandTable.api',
      method: 'post',
      data: params,
      timeout: 1000000,
  });
};


export {
  resetHwBrandTable
}