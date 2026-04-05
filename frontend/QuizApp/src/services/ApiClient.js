import axios from "axios";
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

  return config;
});

// RESPONSE INTERCEPTOR → handle expired token
ApiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
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