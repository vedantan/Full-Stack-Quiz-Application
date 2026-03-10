package com.vedant.demo.controller;
import com.vedant.demo.entity.Question;
import com.vedant.demo.entity.QuizQuestionDTO;
import com.vedant.demo.entity.QuizResponseDto;
import com.vedant.demo.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private Environment environment;

    @GetMapping("/allQuestions")
    public ResponseEntity<List<Question>> getQuestions(){
        return questionService.getAllQuestions();
    }
    //localhost:8080/questions/allQuestions

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Question>> getQuestionByCategory(@PathVariable String category){
        return questionService.getByCategory(category);
    }
    //localhost:8080/questions/category/Java

    @PostMapping("/addQuestion")
    public ResponseEntity<Question> addQuestion(@Valid @RequestBody Question q){
        return questionService.addQuestion(q);
    }


    @GetMapping("/generate")
    public ResponseEntity<List<Integer>> getQuestionforQuiz(
            @RequestParam String category , @RequestParam Integer noOfQues){

        return questionService.getQuestionForQuiz(category,noOfQues);
    }

    @PostMapping("/getQuestions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsfromId(@RequestBody List<Integer> questionIds){
        System.out.println("running instance " + environment.getProperty("local.server.port"));
        return questionService.getQuestionsFromId(questionIds);
    }
    //localhost:8080/questions/getQuestions


    @PostMapping("/getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<QuizResponseDto> responses){
        return questionService.getScore(responses);
    }
    //localhost:8080/questions/getScore
//    [
//    { "id": 4, "response": "extends" },
//    { "id": 16, "response": "HashSet" },
//    { "id": 5, "response": "main()" },
//    { "id": 2, "response": "extends" },
//    { "id": 7, "response": "All of the above" }
//    ]


}
