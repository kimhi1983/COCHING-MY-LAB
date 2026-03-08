import request from '@/utils/request'

const getBoardList = async function(params){
    return request({
        url: '/api/board/list.api',
        method: 'post',
        data: params
    });
};

const getBoardDetail = async function(params){
    return request({
        url: '/api/board/get.api',
        method: 'post',
        data: params
    });
};

const setBoardDetail = async function(params){
    return request({
        url: '/api/board/set.api',
        method: 'post',
        data: params,
        headers: {'content-type': 'multipart/form-data'}
    });
    
};

const addBoardDetail = async function(formData){
    return request({
        url: '/api/board/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const delBoardDetail = async function(formData){
    return request({
        url: '/api/board/del.api',
        method: 'post',
        data: formData
    });
};

const imageUpload = async function(formData){
    return request({
        url: '/api/board/imageUpload.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const getBoardCmt = async function(params){
    return request({
        url: '/api/board/get/cmt.api',
        method: 'post',
        data: params
    });
};

const delBoardDetails = async function(params) {
    return request({
        url: '/api/board/dels.api',
        method: 'post',
        data: params
    });
};


const getMyBoardList = async function(params){
    return request({
        url: '/api/board/my/list.api',
        method: 'post',
        data: params
    });
};

const getInprBoardList = async function(params){
    return request({
        url: '/api/board/inqr/list.api',
        method: 'post',
        data: params
    });
};

const addInqrBoard = async function(formData){
    return request({
        url: '/api/board/inqr/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const setInqrBoard = async function(formData){
    return request({
        url: '/api/board/inqr/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const getInqrBoard = async function(params){
    return request({
        url: '/api/board/inqr/get.api',
        method: 'post',
        data: params
    });
};

const getInqrBoardReply = async function(params){
    return request({
        url: '/api/board/inqr/reply.api',
        method: 'post',
        data: params
    });
};

const delInqrBoardDetail = async function(formData){
    return request({
        url: '/api/board/inqr/del.api',
        method: 'post',
        data: formData
    });
};

const uploadFile = async function(formData){
    return request({
        url: '/api/board/uploadFile.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};


export {
    getBoardList,
    getBoardDetail,
    setBoardDetail,
    addBoardDetail,
    delBoardDetail,
    getBoardCmt,
    delBoardDetails,

    imageUpload,

    getInprBoardList,
    addInqrBoard,
    setInqrBoard,
    getInqrBoard,
    getInqrBoardReply,
    delInqrBoardDetail,
    uploadFile,
}