import request from '@/utils/request'

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

const getRawCmTypeList = async function(params){
    return request({
        url: '/api/common/code/raw/cmTypeList.api',
        method: 'post',
        data: params
    });
};

export {
    getCodes,
    getEnumCodes,
    getRawCmTypeList,
}