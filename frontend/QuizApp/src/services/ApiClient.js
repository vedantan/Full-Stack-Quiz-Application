import axios from "axios";
import { attachIdempotencyKey, shouldRetry, retryDelayMs } from "./idempotency";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";


const ApiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true
});

ApiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  // ❗ Skip token for auth APIs
  if (token && !config.url.includes("/auth")) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // Create-style POSTs get an Idempotency-Key so they can be retried safely
  return attachIdempotencyKey(config);
});

// RESPONSE INTERCEPTOR → handle expired token
ApiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }

    // Safe retry: only requests that carry an Idempotency-Key are retried, with the SAME key,
    // so the server returns the first result instead of creating a duplicate.
    if (shouldRetry(error)) {
      const config = error.config;
      config.__retryCount = (config.__retryCount || 0) + 1;
      return new Promise((resolve) => setTimeout(resolve, retryDelayMs(error))).then(() =>
        ApiClient(config)
      );
    }

    return Promise.reject(error);

  }

);

export default ApiClient;


// export async function fetchData(endpoint, params = {}) {
//   try {
//     const url = `${API_BASE_URL}${endpoint}`;
//     const response = await axios.get(url, {
//       params: params
//     });

//     return response.data;

//   } catch (error) {
//     console.error("API Error:", error);
//     return null;
//   }
// }


// export async function postData(endpoint, data = {}) {
//   try {
//     const url = `${API_BASE_URL}${endpoint}`;

//     const response = await axios.post(url, data);

//     return response.data;

//   } catch (error) {
//     console.error("API Error:", error);
//     return null;
//   }
// }