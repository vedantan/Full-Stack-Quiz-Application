import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";

import {
    Container,
    Typography,
    Card,
    CardContent,
    Grid,
    Divider,
    Chip,
    Box
} from "@mui/material";

import Dropdown from "../components/Dropdown";
import { getQuestionsByCategory, getAllQuestions, generateQuestionListForQuiz, getQuestionsFromIds } from "../services/QuestionApi";
import { notify } from "../utils/Toast";

function Question() {

    const [questions, setQuestions] = useState([]);
    const [category, setCategory] = useState("");
    const categories = ["Java", "Python"];

    const [allQuestions, setAllQuestions] = useState([]);

    const [quizIds, setQuizIds] = useState([]);
    const [noOfQuestion, setNoOfQuestion] = useState(5);
    const [quizQuestions, setQuizQuestions] = useState([]);

    //load quiz from ids generated via generateQuiz
    const loadQuizQuestions = async (ids) => {
        try {
            const response = await getQuestionsFromIds(ids);
            setQuizQuestions(response.data);
        } catch (error) {
            notify.error(`Error loading quiz questions ${error}`)
        }
    };

    // Category Questions
    useEffect(() => {
        if (!category) return;

        const getQuestions = async () => {
            try {
                const response = await getQuestionsByCategory(category);
                setQuestions(response.data);
                notify.success(`Questions fetched for ${category} category`)
            } catch (error) {
                notify.error(`Error fetching questions: ${error}`)
            }
        };
        getQuestions();
    }, [category]);

    // All Questions
    useEffect(() => {
        const seeQuestion = async () => {
            try {
                const response = await getAllQuestions();
                setAllQuestions(response.data);
                notify.success("All questions Fetched");
            } catch (error) {
                notify.error(`Error fetching List of All questions: ${error}`)
            }
        };
        seeQuestion();
    }, []);

    //generate Quiz returns list of random ids 
    const generateQuiz = async () => {
        if (!category) {
            notify.warning("Please select category");
            return;
        }
        try {
            const response = await generateQuestionListForQuiz(category, noOfQuestion);
            const ids = response.data;
            toast.success("Question List generated");
            setQuizIds(ids);
            // fetch questions using IDs
            loadQuizQuestions(ids);
        } catch (error) {
            console.error("Error generating quiz:", error);
        }
    };

    return (

        <Container maxWidth="md" sx={{ mt: 5 }}>

            <Typography
                variant="h4"
                fontWeight="bold"
                gutterBottom
            >
                Question Bank
            </Typography>

            <Typography
                variant="body1"
                sx={{ mb: 3, color: "gray" }}
            >
                Browse all questions or filter by category
            </Typography>

            <Box sx={{ mb: 4 }}>

                <Dropdown
                    options={categories}
                    value={category}
                    onChange={setCategory}
                    caption="Select Category:"
                    placeholder="-- Select Category --"
                    width={180}
                    height={40}
                />

            </Box>

            <Box sx={{ mt: 2 }}>

                <button onClick={generateQuiz}>
                    Generate Quiz
                </button>

            </Box>

            {quizIds.length > 0 && (

                <Box sx={{ mt: 3 }}>

                    <Typography variant="h6">
                        Generated Question IDs
                    </Typography>

                    {quizIds.map((id) => (
                        <Typography key={id}>
                            Question ID: {id}
                        </Typography>
                    ))}

                </Box>

            )}

            {quizQuestions.length > 0 && (

                <Box sx={{ mt: 5 }}>

                    <Typography variant="h5" gutterBottom>
                        Quiz Questions
                    </Typography>

                    {quizQuestions.map((q) => (

                        <Card key={q.id} sx={{ mb: 3 }}>

                            <CardContent>

                                <Typography variant="h6">
                                    {q.questionTitle}
                                </Typography>

                                <Box sx={{ mt: 2 }}>

                                    <Typography>• {q.option1}</Typography>
                                    <Typography>• {q.option2}</Typography>
                                    <Typography>• {q.option3}</Typography>
                                    <Typography>• {q.option4}</Typography>

                                </Box>

                            </CardContent>

                        </Card>

                    ))}

                </Box>

            )}
            {!(quizQuestions.length > 0) &&
                <Grid container spacing={3} direction="column">

                    {(category ? questions : allQuestions).map((q) => (

                        <Grid item xs={12} key={q.id}>

                            <Card
                                elevation={2}
                                sx={{
                                    borderRadius: 3,
                                    width: "100%",
                                    "&:hover": {
                                        transform: "scale(1.01)",
                                        transition: "0.2s"
                                    }
                                }}
                            >

                                <CardContent>

                                    <Typography
                                        variant="h6"
                                        fontWeight="600"
                                        gutterBottom
                                    >
                                        {q.questionTitle}
                                    </Typography>

                                    <Divider sx={{ mb: 2 }} />

                                    <Typography>• {q.option1}</Typography>
                                    <Typography>• {q.option2}</Typography>
                                    <Typography>• {q.option3}</Typography>
                                    <Typography>• {q.option4}</Typography>

                                    <Box sx={{ mt: 2 }}>
                                        <Chip label={q.category || "General"} color="primary" size="small" />
                                    </Box>

                                </CardContent>

                            </Card>

                        </Grid>

                    ))}

                </Grid>
            }

        </Container>

    );
}

export default Question;