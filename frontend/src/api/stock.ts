import request from './request';

export const getStockIn = (params?: any) => {
    return request({
        url: '/stock-in',
        method: 'get',
        params,
    });
};

export const createStockIn = (data: any) => {
    return request({
        url: '/stock-in',
        method: 'post',
        data,
    });
};

export const getStockOut = (params?: any) => {
    return request({
        url: '/stock-out',
        method: 'get',
        params,
    });
};

export const createStockOut = (data: any) => {
    return request({
        url: '/stock-out',
        method: 'post',
        data,
    });
};
