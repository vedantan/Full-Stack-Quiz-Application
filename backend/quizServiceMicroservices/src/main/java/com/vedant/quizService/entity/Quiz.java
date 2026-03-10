package com.vedant.quizService.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    @ElementCollection
    private List<Integer> questionsIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getQuestionsIds() {
        return questionsIds;
    }

    public void setQuestionsIds(List<Integer> questionsIds) {
        this.questionsIds = questionsIds;
    }

    public Quiz() {
    }

    public Quiz(Integer id, String title, List<Integer> questionsIds) {
        this.id = id;
        this.title = title;
        this.questionsIds = questionsIds;
    }
}
