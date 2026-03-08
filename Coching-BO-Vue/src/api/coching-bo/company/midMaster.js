import request from '@/utils/request'

const getMidList = async function(params){
    return request({
        url: '/api/midManage/mst/list.api',
        method: 'post',
        data: params
    });
};

const getMidMaster = async function (params) {
    return request({
        url: '/api/midManage/mst/get.api',
        method: 'post',
        data: params
    });
};

const checkDuplicateData = async function (params) {
    return request({
        url: '/api/midManage/mst/checkDuplicateData.api',
        method: 'post',
        data: params
    });
};

const insertMid = async function (params) {
    return request({
        url: '/api/midManage/mst/insertMid.api',
        method: 'post',
        data: params
    });
};

const setMid = async function (params) {
    return request({
        url: '/api/midManage/mst/setMid.api',
        method: 'post',
        data: params
    });
};

export {
    getMidList,
    checkDuplicateData,
    insertMid,
    getMidMaster,
    setMid
}