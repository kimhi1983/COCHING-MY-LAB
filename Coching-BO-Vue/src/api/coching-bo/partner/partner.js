import request from '@/utils/request'

const getPtnNameList = async function (params){
    return request({
        url: '/api/partner/ptnNmList.api',
        method: 'post',
        data: params
    });
};

const getPartnerList = async function (params) {
    return request({
        url: '/api/partner/list.api',
        method: 'post',
        data: params
    });
};

const getMembList = async function (params) {
    return request({
        url: '/api/partner/memberList.api',
        method: 'post',
        data: params
    });
};

const getPartner = async function (params) {
    return request({
        url: '/api/partner/load.api',
        method: 'post',
        data: params
    });
};

const updatePartner = async function (params) {
    return request({
        url: '/api/partner/update.api',
        method: 'post',
        data: params
    });
};


export {
    getPtnNameList,
    getPartnerList,
    getMembList,
    getPartner,
    updatePartner,
}