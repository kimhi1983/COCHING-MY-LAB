import request from '@/utils/request'

const getRawList = async function(params){
    return request({
        url: '/api/raw/list.api',
        method: 'post',
        data: params
    });
};

const getRaw = async function(params){
    return request({
        url: '/api/raw/get.api',
        method: 'post',
        data: params
    });
};

const setRaw = async function(params){
    return request({
        url: '/api/raw/set.api',
        method: 'post',
        data: params
    });
};

const updateRaw = async function(params){
    return request({
        url: '/api/raw/update.api',
        method: 'post',
        data: params
    });
};

const setDetail = async function(formData){
    return request({
        url: '/api/raw/setDetail.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const uploadFile = async function(formData){
    return request({
        url: '/api/raw/uploadFile.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateRawUseYn = async function(params){
    return request({
        url: '/api/raw/updateUseYn.api',
        method: 'post',
        data: params
    });
};

const updateRawDelYn = async function(params){
    return request({
        url: '/api/raw/updateDelYn.api',
        method: 'post',
        data: params
    });
};

const updateRawDetailUseYn = async function(params){
    return request({
        url: '/api/raw/updateDetailUseYn.api',
        method: 'post',
        data: params
    });
};

const getElementList = async function(params){
    return request({
        url: '/api/raw/elmList.api',
        method: 'post',
        data: params
    });
};

const getTypeList = async function(params){
    return request({
        url: '/api/raw/typeList.api',
        method: 'post',
        data: params
    });
};

const getDocList = async function(params){
    return request({
        url: '/api/raw/docList.api',
        method: 'post',
        data: params
    });
};

const getManagerList = async function(params){
    return request({
        url: '/api/raw/managerList.api',
        method: 'post',
        data: params
    });
};

const getDetail = async function(params){
    return request({
        url: '/api/raw/getDetail.api',
        method: 'post',
        data: params
    });
};

const getGarbageList = async function(params){
    return request({
        url: '/api/raw/garbageList.api',
        method: 'post',
        data: params
    });
};

const getCmTypeList = async function(params){
    return request({
        url: '/api/raw/cmTypeList.api',
        method: 'post',
        data: params
    });
};

const updateRawManager = async function(params){
    return request({
        url: '/api/raw/updateRawManager.api',
        method: 'post',
        data: params
    });
};

const uploadRawExcel = async function(formData){
    return request({
        url: '/api/raw/uploadRawExcel.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
}

const getExThumbList = async function(params){
    return request({
        url: '/api/raw/exThumbList.api',
        method: 'post',
        data: params
    });
};

const getPrtThumbList = async function(params){
    return request({
        url: '/api/raw/prtThumbList.api',
        method: 'post',
        data: params
    });
};

const uploadThumbFile = async function(formData){
    return request({
        url: '/api/raw/addThumbFile.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateThumbDelYn = async function(params){
    return request({
        url: '/api/raw/updateThumbDelYn.api',
        method: 'post',
        data: params
    });
};

export {
    getRawList,
    getRaw,
    setRaw,
    updateRaw,
    setDetail,
    uploadFile,
    updateRawUseYn,
    updateRawDelYn,
    updateRawDetailUseYn,
    getElementList,
    getTypeList,
    getDocList,
    getManagerList,
    getDetail,
    getGarbageList,
    getCmTypeList,
    updateRawManager,
    uploadRawExcel,
    getExThumbList,
    getPrtThumbList,
    uploadThumbFile,
    updateThumbDelYn,
}