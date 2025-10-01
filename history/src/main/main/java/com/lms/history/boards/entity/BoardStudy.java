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
public class BoardStudy {
    private int studyId;
    private int boardId;
    private int userId;
    private Date startAt;
    private Date endAt;
}
