import ApiClient from "./apiClient"


export const getAllQuestions = () => {
    return ApiClient.get(`/questions/allQuestions`);
}

export const getQuestionsByCategory = (category) => {
    return ApiClient.get(`/questions/category/${category}`);
}

// idempotencyKey (optional): pass the same key for every attempt of the same "add" action,
// so a double click or a retry can never create the question twice.
export const addQuestion = async  (question, idempotencyKey) => {
    const response = await ApiClient.post("/questions/addQuestion", question, {
        headers: idempotencyKey ? { "Idempotency-Key": idempotencyKey } : {}
    });
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