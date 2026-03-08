import request from '@/utils/request'

const getBinList = async function(params){
    return request({
        url: '/api/binManage/mst/list.api',
        method: 'post',
        data: params
    });
};

const getBinMaster = async function (params) {
    return request({
        url: '/api/binManage/mst/get.api',
        method: 'post',
        data: params
    });
};

const checkCompNmAndDate = async function (params) {
    return request({
        url: '/api/binManage/mst/checkCompNmAndDate.api',
        method: 'post',
        data: params
    });
};

const insertBin = async function (params) {
    return request({
        url: '/api/binManage/mst/insertBin.api',
        method: 'post',
        data: params
    });
};

const setBin = async function (params) {
    return request({
        url: '/api/binManage/mst/setBin.api',
        method: 'post',
        data: params
    });
};


export {
    getBinList,
    getBinMaster,
    checkCompNmAndDate,
    insertBin,
    setBin

}