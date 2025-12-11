import request from './request';

export const getCategories = () => {
    return request({
        url: '/categories',
        method: 'get',
    });
};

export const createCategory = (data: any) => {
    return request({
        url: '/categories',
        method: 'post',
        data,
    });
};

export const updateCategory = (id: number, data: any) => {
    return request({
        url: `/categories/${id}`,
        method: 'put',
        data,
    });
};

export const deleteCategory = (id: number) => {
    return request({
        url: `/categories/${id}`,
        method: 'delete',
    });
};
