import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
    const token = ref<string | null>(localStorage.getItem('token'));
    const username = ref<string | null>(localStorage.getItem('username'));

    function setToken(newToken: string) {
        token.value = newToken;
        localStorage.setItem('token', newToken);
    }

    function setUsername(newUsername: string) {
        username.value = newUsername;
        localStorage.setItem('username', newUsername);
    }

    function logout() {
        token.value = null;
        username.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('username');
    }

    return {
        token,
        username,
        setToken,
        setUsername,
        logout,
    };
});
