package com.vedant.quizService.service;


import com.vedant.quizService.dao.QuizDao;

import com.vedant.quizService.entity.Quiz;
import com.vedant.quizService.entity.QuizQuestionDTO;
import com.vedant.quizService.entity.QuizResponseDto;
import com.vedant.quizService.feign.QuizInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QuizInterface quizInterface;

    public ResponseEntity<Quiz> createQuiz(String category, int numQ, String title) {

        List<Integer> questions = quizInterface.getQuestionforQuiz(category,numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionsIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>(quiz, HttpStatus.CREATED);


    }

    public ResponseEntity<List<QuizQuestionDTO>> getQuizQuestion(Integer id) {

       Quiz quiz = quizDao.findById(id).get();
       List<Integer> questionIds = quiz.getQuestionsIds();
       List<QuizQuestionDTO> questions = quizInterface.getQuestionsfromId(questionIds).getBody();

        return ResponseEntity.ok(questions);
    }

    public ResponseEntity<Integer> calculateResults(Integer id, List<QuizResponseDto> responses) {
        ResponseEntity<Integer> score =quizInterface.getScore(responses);
        return score;
    }

    public List<Integer> getAllQuizIds() {
        return quizDao.findAll()
                .stream().map(quiz -> quiz.getId()).toList();
    }
}
