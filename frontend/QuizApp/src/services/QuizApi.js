import ApiClient from "./apiClient";


export const createQuiz = async (dto) => {
        const response = await ApiClient.post("/quiz/create" , dto);
        return response.data;
}


export const getQuiz = async (id) => {
    const response = await ApiClient.get(`/quiz/get/${id}` );
    return response.data;
}


export const getIds = () => {
  return ApiClient.get("/quiz/getAllId");
};