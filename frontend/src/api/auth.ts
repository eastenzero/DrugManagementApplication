import request from './request';

export const login = (data: any) => {
    return request({
        url: '/auth/login',
        method: 'post',
        data,
    });
};

export const register = (data: any) => {
    return request({
        url: '/auth/register',
        method: 'post',
        data,
    });
};
