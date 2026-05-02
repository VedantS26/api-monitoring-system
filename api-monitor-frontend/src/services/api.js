import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const api = axios.create({
    baseURL: BASE_URL,
});

// attach JWT token to every request automatically
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});




// ─── Auth ─────────────────────────────────────────────────────
export const register = (email, password) =>
    api.post('/api/auth/register', { email, password });

export const login = (email, password) => {
    
    return api.post('/api/auth/login', { email, password });
}

// ─── Endpoints ────────────────────────────────────────────────
export const getEndpoints = () =>
    api.get('/api/endpoints');

export const addEndpoint = (data) =>
    api.post('/api/endpoints', data);

export const deleteEndpoint = (id) =>
    api.delete(`/api/endpoints/${id}`);

export const updateEndpoint = (id, data) =>
    api.patch(`/api/endpoints/${id}`, data);

// ─── Dashboard ────────────────────────────────────────────────
export const getDashboard = () =>
    api.get('/api/dashboard');

export const getLogs = (id) =>
    api.get(`/api/endpoints/${id}/logs`);

export const getUptime = (id, days) =>
    api.get(`/api/endpoints/${id}/uptime?days=${days}`);

export default api;