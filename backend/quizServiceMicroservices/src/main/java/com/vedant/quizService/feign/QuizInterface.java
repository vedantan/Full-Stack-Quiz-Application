package com.vedant.quizService.feign;

import com.vedant.quizService.entity.QuizQuestionDTO;
import com.vedant.quizService.entity.QuizResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("QUESTION-SERVICE")
public interface QuizInterface {

    @GetMapping("/api/questions/generate")
    public ResponseEntity<List<Integer>> getQuestionforQuiz( @RequestParam String category , @RequestParam Integer noOfQues);

    @PostMapping("/api/questions/getQuestions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsfromId(@RequestBody List<Integer> questionIds);

    @PostMapping("/api/questions/getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<QuizResponseDto> responses);

}
