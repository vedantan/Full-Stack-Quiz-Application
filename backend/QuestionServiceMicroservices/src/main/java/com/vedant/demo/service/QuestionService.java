package com.vedant.demo.service;

import com.vedant.demo.dao.QuestionDao;
import com.vedant.demo.entity.Question;
import com.vedant.demo.entity.QuizQuestionDTO;
import com.vedant.demo.entity.QuizResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;
    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return ResponseEntity.ok(questionDao.findAll());
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<List<Question>> getByCategory(String category) {
        try {
            return ResponseEntity.ok(questionDao.findByCategory(category));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Question> addQuestion(Question q) {
        Question savedQuestion = questionDao.save(q);
        URI location = URI.create("/questions/" + savedQuestion.getId());
        return ResponseEntity.created(location).body(savedQuestion);
    }

    public ResponseEntity<List<Integer>> getQuestionForQuiz(String category, Integer noOfQues) {
        List<Integer> q = questionDao.findRandomQuestions(category,noOfQues);
        return ResponseEntity.ok(q);
    }


    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuizQuestionDTO> questionWrapper = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for (Integer i : questionIds){
            questions.add(questionDao.findById(i).get());
        }

        for (Question q : questions){
            QuizQuestionDTO dto = new QuizQuestionDTO();
            dto.setId(q.getId());
            dto.setQuestionTitle(q.getQuestionTitle());
            dto.setOption1(q.getOption1());
            dto.setOption2(q.getOption2());
            dto.setOption3(q.getOption3());
            dto.setOption4(q.getOption4());

            questionWrapper.add(dto);
        }
        return new ResponseEntity<>(questionWrapper, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<QuizResponseDto> responses) {

        int right = 0;

        for (QuizResponseDto resp : responses){
            Question q = questionDao.findById(resp.getId()).get();
            if(resp.getResponse().equals(q.getRightAnswer())){
                right++;
            }

        }
        return ResponseEntity.ok(right);

    }
}
