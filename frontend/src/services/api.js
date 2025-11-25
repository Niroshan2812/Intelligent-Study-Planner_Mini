import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const studentService = {
    create: (data) => api.post('/students', data),
    getAll: () => api.get('/students'),
};

export const subjectService = {
    create: (data) => api.post('/subjects', data),
    getByStudent: (studentId) => api.get(`/subjects/student/${studentId}`),
};

export const examService = {
    create: (data) => api.post('/exams', data),
    getByStudent: (studentId) => api.get(`/exams/student/${studentId}`),
};

export const availabilityService = {
    create: (data) => api.post('/availability', data),
    getByStudent: (studentId) => api.get(`/availability/student/${studentId}`),
};

export const scheduleService = {
    generate: (studentId) => api.post(`/schedule/generate/${studentId}`),
};

export default api;
