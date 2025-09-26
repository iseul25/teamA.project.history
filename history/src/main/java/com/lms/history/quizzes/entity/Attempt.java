package com.lms.history.quizzes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attempt {
    private int attemptId;
    private int userId;
    private int quizCategoryId;
    private int quizId;
    private int selected;
    private int earnedScore;
    private LocalDateTime attemptAt;

    private String name;
}
