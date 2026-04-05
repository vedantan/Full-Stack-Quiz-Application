import ApiClient from "./apiClient"


export const generateToken = async (payload) => {
    const resp = await ApiClient.post("/auth/login", payload);
    return resp.data;
}


export const signupUser = async (payload) => {
    console.log("inside the signup api" , payload)
    const response = await ApiClient.post("/auth/signup", payload);
    return response.data;
}
