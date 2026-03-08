import request from '@/utils/request'

const getBoardMasterList = async function(params){
    return request({
        url: '/api/board/mst/list.api',
        method: 'post',
        data: params
    });
};

const getBoardMaster = async function(params){
    return request({
        url: '/api/board/mst/get.api',
        method: 'post',
        data: params
    });
};

const setBoardMaster = async function(params){
    return request({
        url: '/api/board/mst/set.api',
        method: 'post',
        data: params
    });
};

const addBoardMaster = async function(params){
    return request({
        url: '/api/board/mst/new.api',
        method: 'post',
        data: params
    });
};

const checkBoardMasterId = async function(params){
    return request({
        url: '/api/board/mst/checkId.api',
        method: 'post',
        data: params
    });
};

const setBoardMasterState = async function(params) {
    return request({
        url: '/api/board/mst/setState.api',
        method: 'post',
        data: params
    });
};

export {
    getBoardMasterList,
    getBoardMaster,
    setBoardMaster,
    addBoardMaster,
    checkBoardMasterId,

    setBoardMasterState
}