package com.vedant.quizService.entity;

public class QuizResponseDto {

    private Integer id;
    private String response;

    public QuizResponseDto() {
    }

    public QuizResponseDto(Integer id, String response) {
        this.id = id;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "QuizResponseDto{" +
                "id=" + id +
                ", response='" + response + '\'' +
                '}';
    }
}