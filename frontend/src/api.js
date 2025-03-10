// src/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // backend base URL
});

// Optional: Attach token to every request if it exists in localStorage
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
