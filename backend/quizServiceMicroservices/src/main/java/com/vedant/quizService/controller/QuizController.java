package com.vedant.quizService.controller;

import com.vedant.quizService.Dto.LeaderboardDto;
import com.vedant.quizService.Dto.UserDto;
import com.vedant.quizService.entity.Quiz;
import com.vedant.quizService.entity.QuizDto;
import com.vedant.quizService.entity.QuizQuestionDTO;
import com.vedant.quizService.entity.QuizResponseDto;
import com.vedant.quizService.feign.AuthClient;
import com.vedant.quizService.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private AuthClient authClient;

    @GetMapping("getAllId")
    public List<Integer> allQuizIds(){
        return quizService.getAllQuizIds();
    }

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

    @GetMapping("/leaderboard")
    public List<LeaderboardDto> getLeaderboard() {

        List<UserDto> users = authClient.getUsers();

        List<LeaderboardDto> result = new ArrayList<>();

        int rank = 1;

        for (UserDto user : users) {
            result.add(new LeaderboardDto(rank++, user.getUsername(), 0));
        }

        return result;
    }





}
