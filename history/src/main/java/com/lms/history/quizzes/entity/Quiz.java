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
    private Integer quizNumber;      // 추가 (DB에 있는 컬럼)
    private String imgUrl;
    private String question;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private Integer answer;
    private String commentary;
    private Integer quizScore;       // 추가 (DB에 있는 컬럼)
}