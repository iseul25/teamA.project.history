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
public class CommentReply {
    private int replyId;
    private int commentId;
    private int userId;
    private String reply;
    private LocalDateTime date;

    private String name;
}
