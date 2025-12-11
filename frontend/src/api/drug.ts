import request from './request';

export const getDrugs = (params?: any) => {
    return request({
        url: '/drugs',
        method: 'get',
        params,
    });
};

export const createDrug = (data: any) => {
    return request({
        url: '/drugs',
        method: 'post',
        data,
    });
};

export const updateDrug = (id: number, data: any) => {
    return request({
        url: `/drugs/${id}`,
        method: 'put',
        data,
    });
};

export const deleteDrug = (id: number) => {
    return request({
        url: `/drugs/${id}`,
        method: 'delete',
    });
};
