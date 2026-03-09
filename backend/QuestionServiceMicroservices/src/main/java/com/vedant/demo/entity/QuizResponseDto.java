package com.vedant.demo.entity;

public class QuizResponseDto {

    private Integer id;
    private String response;

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
        this.response = response;   // FIX
    }

    public QuizResponseDto() {
    }

    public QuizResponseDto(Integer id, String response) {
        this.id = id;
        this.response = response;   // FIX
    }
}