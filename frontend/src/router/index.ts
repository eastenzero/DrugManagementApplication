import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../store/user';

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { guest: true },
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue'),
        meta: { guest: true },
    },
    {
        path: '/',
        component: () => import('../layout/MainLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/Dashboard.vue'),
                meta: { requiresAuth: true },
            },
            {
                path: 'categories',
                name: 'Categories',
                component: () => import('../views/CategoryList.vue'),
                meta: { requiresAuth: true },
            },
            {
                path: 'drugs',
                name: 'Drugs',
                component: () => import('../views/DrugList.vue'),
                meta: { requiresAuth: true },
            },
            {
                path: 'stock-in',
                name: 'StockIn',
                component: () => import('../views/StockInList.vue'),
                meta: { requiresAuth: true },
            },
            {
                path: 'stock-out',
                name: 'StockOut',
                component: () => import('../views/StockOutList.vue'),
                meta: { requiresAuth: true },
            },
            {
                path: 'inventory',
                name: 'Inventory',
                component: () => import('../views/Inventory.vue'),
                meta: { requiresAuth: true },
            },
        ],
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, _from, next) => {
    const userStore = useUserStore();
    const isAuthenticated = !!userStore.token;

    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
    } else if (to.meta.guest && isAuthenticated) {
        next('/dashboard');
    } else {
        next();
    }
});

export default router;
