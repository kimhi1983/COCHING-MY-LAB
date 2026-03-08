import request from '@/utils/request'

const getCodeMasters = async function(params){
    return request({
        url: '/api/common/code/mst/list.api',
        method: 'post',
        data: params
    });
};

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

const getAddressList = async function(params){
    return request({
        url: '/api/common/code/address/list.api',
        method: 'post',
        data: params
    });
};

const getBizSectorList = async function(params){
    return request({
        url: '/api/common/code/bizSctInfo/list.api',
        method: 'post',
        data: params
    });
};

const getSigunguList = async function(params){
    return request({
        url: '/api/common/code/zipcode/sigunguList.api',
        method: 'post',
        data: params
    });
};

const setCodeMasterState = async function(params) {
    return request({
        url: '/api/common/code/mst/setState.api',
        method: 'post',
        data: params
    });
};

const setCodeState = async function(params) {
    return request({
        url: '/api/common/code/setState.api',
        method: 'post',
        data: params
    });
};

const setCodeOrder = async function(params) {
    return request({
        url: '/api/common/code/setOrders.api',
        method: 'post',
        data: params
    });
};

export {
    getCodeMasters,
    getCodes,

    getEnumCodes,
    getAddressList,
    getBizSectorList,
    getSigunguList,

    setCodeMasterState,
    setCodeState,
    setCodeOrder,
}