import request from '@/utils/request'
const newTerms = async function(params){
    return request({
        url: '/api/terms/new.api',
        method: 'post',
        data: params
    });
};

const setTerms = async function(params){
    return request({
        url: '/api/terms/set.api',
        method: 'post',
        data: params
    });
};

const getList = async function(params){
    return request({
        url: '/api/terms/list.api',
        method: 'post',
        data: params
    });
};

const getTerms = async function(params){
    return request({
        url: '/api/terms/get.api',
        method: 'post',
        data: params
    });
};

export {
    newTerms, 
    setTerms,
    getList,
    getTerms,
}