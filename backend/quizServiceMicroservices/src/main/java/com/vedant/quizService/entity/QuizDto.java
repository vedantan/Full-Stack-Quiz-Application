package com.vedant.quizService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizDto {
    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("numQuestion")
    private Integer numQuestion;

    @JsonProperty("title")
    private String title;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getNumQuestion() {
        return numQuestion;
    }

    public void setNumQuestion(Integer numQuestion) {
        this.numQuestion = numQuestion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
