import request from '@/utils/request';
import axios from 'axios';

const apiBase = process.env.VUE_APP_API_BASE_URL;

const getCodes = async function(params){
    return request({
        url: '/api/common/code/list.api',
        method: 'post',
        data: params
    });
};

const getEnumCodes = async function(params){
    return request({
        url: '/api/common/code/enum.api',
        method: 'post',
        data: params
    });
};

const getIngredientNames = async function(){
    const reqUrl = `${apiBase}/api/code/static/ingredient/names.api`;
    const response = await axios.get(reqUrl);

    return response;
};

export {
    getCodes,
    getEnumCodes,

    getIngredientNames,
}