import request from '@/utils/request'

const getDashBoardData = async function(params){
    return request({
        url: '/api/dashboard/getData.api',
        method: 'post',
        data: params
    });
};

export {
    getDashBoardData
}