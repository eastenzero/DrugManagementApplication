import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../store/user';

const service = axios.create({
    baseURL: 'http://localhost:8080/api', // Adjust as needed or use import.meta.env.VITE_API_URL
    timeout: 5000,
});

// Request interceptor
service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore();
        if (userStore.token) {
            config.headers['Authorization'] = `Bearer ${userStore.token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor
service.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        console.error('Request Error:', error);
        const message = error.response?.data?.message || error.message || 'Request failed';
        ElMessage.error(message);

        // Handle 401 Unauthorized
        if (error.response?.status === 401) {
            const userStore = useUserStore();
            userStore.logout();
            window.location.reload();
        }

        return Promise.reject(error);
    }
);

export default service;
