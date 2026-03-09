package com.vedant.demo.dao;


import com.vedant.demo.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question,Integer> {
    List<Question> findByCategory(String category);

    @Query(value = "SELECT q.id FROM question q WHERE q.category = :category ORDER BY random() LIMIT :numQ", nativeQuery = true)
    List<Integer> findRandomQuestions(@Param("category") String category, @Param("numQ") int numQ);


}
