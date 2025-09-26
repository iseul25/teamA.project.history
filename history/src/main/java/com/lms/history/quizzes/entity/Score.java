package com.lms.history.quizzes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Score {
    private int scoreId;
    private int quizCategoryId;
    private int userId;
    private int totalScore;
    private int earnedPoint;
    private String pass;

    private String name;
}
