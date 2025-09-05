package com.lms.history.users.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int user_id;        // 기본 키 (PK), 데이터베이스에서 `user_id` 컬럼과 매핑
    private String user_type;    // 관리자 또는 일반 유저 구분
    private String name;        // 이름
    private String password;    // 암호화된 비밀번호
    private String email;       // 이메일
}
