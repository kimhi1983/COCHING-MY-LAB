import request from '@/utils/request'



const getMltlnList = async function(params){
    return request({
        url: '/api/mltln/mst/list.api',
        method: 'post',
        data: params
    });
};

const getMltlnMaster = async function (params) {
    return request({
        url: '/api/mltln/mst/get.api',
        method: 'post',
        data: params
    });
};

// CODE(pk) 중복여부
const isHaveCode = async function (params) {
    return request({
        url: '/api/mltln/mst/isHaveCode.api',
        method: 'post',
        data: params
    });
};

const insertMltln = async function (params) {
    return request({
        url: '/api/mltln/mst/insertMltln.api',
        method: 'post',
        data: params
    });
};

const setMltln = async function (params) {
    return request({
        url: '/api/mltln/mst/setMltln.api',
        method: 'post',
        data: params
    });
};

const exportI18nJson = async function (params) {
    return request({
        url: '/api/mltln/mst/exportJson.api',
        method: 'post',
        data: params
    });
};

const uploadFile = async function (formData) {
    return request({
        url: '/api/mltln/mst/uploadFile.api',
        method: 'post',
        data: formData,
        headers: { 'content-type': 'multipart/form-data' }
    });
};


export {    
    getMltlnList,
    getMltlnMaster,
    isHaveCode,
    insertMltln,
    setMltln,
    exportI18nJson,
    uploadFile
}