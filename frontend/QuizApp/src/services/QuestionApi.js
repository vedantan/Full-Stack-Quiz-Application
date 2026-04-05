import ApiClient from "./apiClient"


export const getAllQuestions = () => {
    return ApiClient.get(`/questions/allQuestions`);
}

export const getQuestionsByCategory = (category) => {
    return ApiClient.get(`/questions/category/${category}`);
}

export const addQuestion = async  (question) => {
    const response = await ApiClient.post("/questions/addQuestion", question);
    return response.data;
};

export const generateQuestionListForQuiz = async (category, noOfQuestion) => {
    return await ApiClient.get("/questions/generate", {
        params: {
            category: category,
            noOfQues: noOfQuestion
        }
    });
};

export const getQuestionsFromIds = async (questionIds) => {
  return await ApiClient.post("/questions/getQuestions", questionIds);
};