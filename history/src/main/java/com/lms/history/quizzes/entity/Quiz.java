package com.lms.history.quizzes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    private int quizId;
    private int quizCategoryId;
    private String imgUrl;
    private String question;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private int answer;
    private String commentary;
    private int quizScore;
}
