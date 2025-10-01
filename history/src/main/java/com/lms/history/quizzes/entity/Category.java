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
public class Category {
    private int quizCategoryId;
    private int userId;
    private String quizType;
    private String quizListName;
    private LocalDateTime createAt;

    private String name;
}