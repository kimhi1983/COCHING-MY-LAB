import request from '@/utils/request'

const getNotiList = async function(params){
    return request({
        url: '/api/notification/list.api',
        method: 'post',
        data: params
    });
};

const updateChkYn = async function(params){
    return request({
        url: '/api/notification/updateChkYn.api',
        method: 'post',
        data: params
    });
};

export {
	getNotiList,
    updateChkYn,
}