import request from '@/utils/request'

const getPopupMasterList = async function(params){
    return request({
        url: '/api/popup/mst/list.api',
        method: 'post',
        data: params
    });
};

const getPopupMaster = async function(params){
    return request({
        url: '/api/popup/mst/get.api',
        method: 'post',
        data: params
    });
};

const setPopupMasterState = async function(params) {
    return request({
        url: '/api/popup/mst/setState.api',
        method: 'post',
        data: params
    });
}

export {
    getPopupMasterList,
    getPopupMaster,
    setPopupMasterState,
}