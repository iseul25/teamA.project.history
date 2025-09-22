package com.lms.history.boards.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private int commentId;
    private int boardId;
    private int userId;
    private String comment;
    private LocalDateTime date;

    private String name;

}
