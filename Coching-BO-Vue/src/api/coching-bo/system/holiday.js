import request from '@/utils/request'

const getHolidayList = async function(params){
    return request({
        url: '/api/bo/holiday/list.api',
        method: 'post',
        data: params
    });
};

const insertHoliday = async function(params){
  return request({
      url: '/api/bo/holiday/insert.api',
      method: 'post',
      data: params
  });
};

const updateHoliday = async function(params){
  return request({
      url: '/api/bo/holiday/update.api',
      method: 'post',
      data: params
  });
};

const deleteHoliday = async function(params){
  return request({
      url: '/api/bo/holiday/delete.api',
      method: 'post',
      data: params
  });
};


export {
    getHolidayList,
    insertHoliday,
    updateHoliday,
    deleteHoliday,
}