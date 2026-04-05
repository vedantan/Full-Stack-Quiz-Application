import React, { useState } from "react";
import { addQuestion } from "../services/QuestionApi";
import { notify } from "../utils/Toast";
import Input from "../components/Input";

import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  Grid,
  Container
} from "@mui/material";

function AddQuestion() {

  const [showForm, setShowForm] = useState(false);
  const [errors, setErrors] = useState({});
  const [question, setQuestion] = useState({
    questionTitle: "",
    option1: "",
    option2: "",
    option3: "",
    option4: "",
    rightAnswer: "",
    difficultylevel: "",
    category: ""
  });

  const handleChange = (e) => {
    setQuestion({
      ...question,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addQuestion(question);
      notify.success("Question added successfully");
      setErrors({});
      setShowForm(false);
    } catch (err) {
      if (err.response && err.response.data) {
        setErrors(err.response.data);
        notify.error(err.response.data);
      }
    }
  };

  return (
    <Container maxWidth="md">

      <Box
        sx={{
          mt: 6,
          display: "flex",
          justifyContent: "center"
        }}
      >

        {!showForm && (
          <Button
            variant="contained"
            size="large"
            sx={{
              px: 5,
              py: 1.5,
              borderRadius: 2,
              fontWeight: 600
            }}
            onClick={() => setShowForm(true)}
          >
            Add Question
          </Button>
        )}

      </Box>

      {showForm && (

        <Card
          elevation={6}
          sx={{
            mt: 1,
            borderRadius: 3,
            backgroundColor:"#fff9ff",
          }}
        >
          <CardContent>

            <Typography
              variant="h5"
              fontWeight="bold"
              sx={{ mb: 3 }}
            >
              Add Question
            </Typography>

            <form onSubmit={handleSubmit}>

              <Input
                name="questionTitle"
                //placeholder="Question"
                caption="Question"
                value={question.questionTitle}
                onChange={handleChange}
                error={errors.questionTitle}
                width="100%"
              />

              <Grid container spacing={2}>

                <Grid item xs={12} md={6}>
                  <Input
                    name="option1"
                    caption="Option 1"
                    value={question.option1}
                    onChange={handleChange}
                    error={errors.option1}
                    width="100%"
                  />
                </Grid>

                <Grid item xs={12} md={6}>
                  <Input
                    name="option2"
                    caption="Option 2"
                    value={question.option2}
                    onChange={handleChange}
                    error={errors.option2}
                    width="100%"
                  />
                </Grid>

                <Grid item xs={12} md={6}>
                  <Input
                    name="option3"
                    caption="Option 3"
                    value={question.option3}
                    onChange={handleChange}
                    error={errors.option3}
                    required={true}
                    width="100%"
                  />
                </Grid>

                <Grid item xs={12} md={6}>
                  <Input
                    name="option4"
                    caption="Option 4"
                    value={question.option4}
                    onChange={handleChange}
                    error={errors.option4}
                    width="100%"
                  />
                </Grid>

              </Grid>

              <Grid container spacing={2} sx={{ mt: 1 }}>

                <Grid item xs={12} md={4}>
                  <Input
                    name="rightAnswer"
                    caption="Right Answer"
                    value={question.rightAnswer}
                    onChange={handleChange}
                    error={errors.rightAnswer}
                    width="100%"
                  />
                </Grid>

                <Grid item xs={12} md={4}>
                  <Input
                    name="difficultylevel"
                    caption="Difficulty Level"
                    value={question.difficultylevel}
                    onChange={handleChange}
                    error={errors.difficultylevel}
                    width="100%"
                  />
                </Grid>

                <Grid item xs={12} md={4}>
                  <Input
                    name="category"
                    caption="Category"
                    value={question.category}
                    onChange={handleChange}
                    error={errors.category}
                    width="100%"
                  />
                </Grid>

              </Grid>

              <Box
                sx={{
                  mt: 4,
                  display: "flex",
                  justifyContent: "flex-end",
                  gap: 2
                }}
              >

                <Button
                  variant="outlined"
                  color="secondary"
                  onClick={() => setShowForm(false)}
                >
                  Cancel
                </Button>

                <Button
                  variant="contained"
                  type="submit"
                  sx={{
                    px: 4,
                    fontWeight: 600
                  }}
                >
                  Submit
                </Button>

              </Box>

            </form>

          </CardContent>
        </Card>

      )}

    </Container>
  );
}

export default AddQuestion;