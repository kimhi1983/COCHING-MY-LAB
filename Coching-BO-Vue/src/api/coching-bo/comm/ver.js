import request from '@/utils/request'

const getVerList = async function(params){
    return request({
        url: '/api/ver/verlist.api',
        method: 'post',
        data: params
    });
};
const updateVersion = async function(params){
    return request({
        url: '/api/ver/updateVer.api',
        method: 'post',
        data: params
    });
};

const insertVersion = async function(params){
    return request({
        url: '/api/ver/insertVer.api',
        method: 'post',
        data: params
    });
};


export {
     getVerList,
     updateVersion,
     insertVersion

}