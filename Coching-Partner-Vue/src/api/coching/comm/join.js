import request from '@/utils/request'

const userJoin = async function(params){
    return request({
        url: '/api/join/userJoin.api',
        method: 'post',
        data: params
    });
};

const userUpdate = async function(params){
    return request({
        url: '/api/join/userUpdate.api',
        method: 'post',
        data: params
    });
};

const chkDuplicate = async function(params){
    return request({
        url: '/api/join/chkDuplicate.api',
        method: 'post',
        data: params
    });
};


export {
    userJoin,
    userUpdate,
    chkDuplicate,
}