import request from '@/utils/request'

const getSysPropList = async function(params){
    return request({
        url: '/api/sysProp/list.api',
        method: 'post',
        data: params
    });
};

const getSysProp = async function(params){
    return request({
        url: '/api/sysProp/get.api',
        method: 'post',
        data: params
    });
};

const setSysProp = async function(params){
    return request({
        url: '/api/sysProp/set.api',
        method: 'post',
        data: params
    });
};

const delSysProp = async function(params){
    return request({
        url: '/api/sysProp/del.api',
        method: 'post',
        data: params
    });
};

const setSysProps = async function(params){
    return request({
        url: '/api/sysProp/sets.api',
        method: 'post',
        data: params
    });
};

const delSysProps = async function(params){
    return request({
        url: '/api/sysProp/dels.api',
        method: 'post',
        data: params
    });
};


export {
    getSysPropList,
    getSysProp,

    setSysProp, 
    setSysProps,
    delSysProp,    
    delSysProps,
}