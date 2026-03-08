import request from '@/utils/request'

const setFileOrder = async function(params){
    return request({
        url: '/api/bo/file/setOrder.api',
        method: 'post',
        data: params
    });
};

export {
    setFileOrder
}