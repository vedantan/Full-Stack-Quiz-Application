package com.vedant.quizService.dao;

import com.vedant.quizService.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizDao extends JpaRepository<Quiz,Integer> {

    @Query("SELECT q.id FROM Quiz q")
    List<Integer> findAllQuizIds();
}
