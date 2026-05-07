import axios from 'axios';

const rawBaseUrl = process.env.REACT_APP_API_BASE_URL;
const BASE_URL =
    rawBaseUrl?.replace(/\/+$/, '') ||
    (process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : '');

if (!BASE_URL && process.env.NODE_ENV === 'production') {
    // CRA injects REACT_APP_* values at build time, so Vercel must define this
    // before building the frontend.
    console.error('Missing REACT_APP_API_BASE_URL. Login requests will be sent to the frontend origin instead of the API.');
}

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
