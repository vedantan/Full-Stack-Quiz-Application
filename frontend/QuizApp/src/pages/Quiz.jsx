import React, { useEffect, useState } from "react";
import { Container, Card, CardContent, Typography, Box } from "@mui/material";
import Button from "../components/Button";
import Input from "../components/Input";
import { createQuiz, getQuiz ,getIds} from "../services/QuizApi";
import { notify } from "../utils/Toast";
import Dropdown from "../components/Dropdown";

function Quiz() {
  const [form, setForm] = useState({
    title: "",
    categoryName: "",
    numQuestion: "",
  });

  const [questions, setQuestions] = useState([]);

  const [showForm, setShowForm] = useState(false);
  const [showFormForGetQuiz, setShowFormForGetQuiz] = useState(false);
  const [showQuestions, setShowQuestions] = useState(false);

const [list, setList] = useState([]);
const [selectedId, setSelectedId] = useState("");

useEffect(() => {

  const fetchIds = async () => {
    try {
      const response = await getIds();
      console.log(response.data);
      setList(response.data);
    } catch (error) {
      console.error("Error fetching ids:", error);
    }
  };

  fetchIds();

}, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleCreateClick = () => {
    setShowForm(true);
    setShowFormForGetQuiz(false);
    setQuestions([]);
  };

  const handleGetQuiz = () => {
    setShowFormForGetQuiz(true);
    setShowForm(false)
    setQuestions([]);
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = { ...form, numQuestion: Number(form.numQuestion) };

    try {
      const resp = await createQuiz(payload);
      console.log("Quiz created:", resp);
      setForm({ title: "", categoryName: "", numQuestion: "" });
      setShowForm(false);
    } catch (error) {
      console.error("Error creating quiz:", error);
      notify.error(`Error creating quiz: ${error}`)
    }
  };

  const handleGetQuizById = async (e) => {
    e.preventDefault();

    try {
      const resp = await getQuiz(selectedId);
      setQuestions(resp); // store questions
      setShowQuestions(true);
      setShowFormForGetQuiz(false);
      setSelectedId("");
      notify.success("Quiz fetched successfully");
    } catch (error) {
      console.error(error);
      notify.error(`Error getting quiz: ${error}`);
    }
  };

  return (
    <Container maxWidth="sm">
      <Card
        elevation={6}
        sx={{ mt: 6, borderRadius: 3, backgroundColor: "#f9fafb", textAlign: "center" }}
      >
        <CardContent>
          <Typography variant="h5" fontWeight="bold" sx={{ mb: 4 }}>
            {showForm ? "Create Quiz" : showFormForGetQuiz ? "Get Quiz" : "Quiz Page"}
          </Typography>

          {/* Initial Create Quiz Button */}
          {(!showForm && !showFormForGetQuiz && !showQuestions) && (
            <Box sx={{ display: "flex", justifyContent: "center", gap: 3 }}>
              <Button
                caption="Create Quiz"
                variant="contained"
                height={40}
                width={150}
                onClick={handleCreateClick}
              />

              <Button
                caption="Get Quiz"
                variant="contained"
                height={40}
                width={150}
                onClick={handleGetQuiz}
              />

            </Box>
          )}

          {/* Form Fields */}
          {showForm && (
            <form onSubmit={handleSubmit}>
              <Input
                name="title"
                value={form.title}
                onChange={handleChange}
                caption="Quiz Title"
                required={true}
              />

              <Input
                name="categoryName"
                value={form.categoryName}
                onChange={handleChange}
                caption="Category Name"
                required={true}
              />

              <Input
                name="numQuestion"
                type="number"
                value={form.numQuestion}
                onChange={handleChange}
                caption="Number of Questions"
                required={true}
              />

              <Box sx={{ display: "flex", justifyContent: "center", gap: 3 }}>
                <Button
                  type="submit" // important for HTML5 validation
                  caption="Submit"
                  variant="contained"
                  height={40}
                  width={150}
                />

                <Button
                  caption="Cancel"
                  variant="outlined"
                  height={40}
                  width={150}
                  onClick={() => setShowForm(false)}
                />
              </Box>
            </form>
          )}

          {showFormForGetQuiz && (
            <form onSubmit={handleGetQuizById}>

              <Dropdown
                value={selectedId}
                caption="Quiz Id"
                options={list}
                onChange={(value) => setSelectedId(value)}
                width={120}
              />

              <Box sx={{ display: "flex", justifyContent: "center", gap: 3 ,marginTop:5}}>
                <Button
                  type="submit"
                  caption="Get"
                  variant="contained"
                  height={40}
                  width={150}
                />

                <Button
                  caption="Cancel"
                  variant="outlined"
                  height={40}
                  width={150}
                  onClick={() => setShowFormForGetQuiz(false)}
                />
              </Box>

            </form>


          )}

          {showQuestions && questions.length > 0 && (
            <>
              <Box sx={{ mt: 4, textAlign: "left" }}>
                {questions.map((q, index) => (
                  <Card key={q.id} sx={{ mb: 2, p: 2 }}>
                    <Typography variant="h6">
                      {index + 1}. {q.questionTitle}
                    </Typography>

                    <Box sx={{ ml: 2, mt: 1 }}>
                      <Typography>A. {q.option1}</Typography>
                      <Typography>B. {q.option2}</Typography>
                      <Typography>C. {q.option3}</Typography>
                      <Typography>D. {q.option4}</Typography>
                    </Box>
                  </Card>
                ))}
              </Box>

              <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                <Button
                  caption="Cancel"
                  variant="outlined"
                  height={40}
                  width={150}
                  onClick={() => {
                    setShowQuestions(false);
                    setShowFormForGetQuiz(true);
                    setQuestions([]);
                  }}
                />
              </Box>
            </>
          )}
        </CardContent>
      </Card>
    </Container>
  );
}

export default Quiz;