package com.vedant.quizService.controller;

import com.vedant.quizService.entity.Quiz;
import com.vedant.quizService.entity.QuizDto;
import com.vedant.quizService.entity.QuizQuestionDTO;
import com.vedant.quizService.entity.QuizResponseDto;
import com.vedant.quizService.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<Quiz> quiz(@RequestBody QuizDto quizdto){
        return quizService.createQuiz(quizdto.getCategoryName(),quizdto.getNumQuestion() ,quizdto.getTitle());
    }
    //localhost:8080/quiz/create?category=Java&numQ=5&title=JQuiz

    @GetMapping("/get/{id}")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestion(@PathVariable Integer id){
        return quizService.getQuizQuestion(id);
    }
    //localhost:8080/quiz/get/1

    @PostMapping("/submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Integer id , @RequestBody List<QuizResponseDto> responses){
        return quizService.calculateResults(id,responses);
    }
    //localhost:8080/quiz/submit/1
//    { "id": 4, "response": "extends" },
//    { "id": 16, "response": "HashSet" },
//    { "id": 5, "response": "main()" },
//    { "id": 2, "response": "extends" },
//    { "id": 7, "response": "All of the above" }







}
