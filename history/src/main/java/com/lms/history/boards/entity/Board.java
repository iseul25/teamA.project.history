package com.lms.history.boards.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private int boardId;
    private int userId;
    private String title;
    private String content;
    private String boardType;
    private Date created;
    private Date updated;
    private String imgUrl;
    private String imgDescription;
    private String name;

    // ⬇️ 추가
    private String videoUrl;
}
