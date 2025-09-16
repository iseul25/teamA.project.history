package com.lms.history.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_attendance") // 🚩 테이블명을 'user_attendance'로 지정
public class Attend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendanceId") // 🚩 컬럼명을 'attendanceId'로 지정
    private Integer attendanceId;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "attendanceDate") // 🚩 컬럼명을 'attendanceDate'로 지정
    private LocalDateTime attendanceDate; // 🚩 데이터 타입을 'LocalDateTime'으로 변경

    @Column(name = "pointAdd") // 🚩 컬럼명을 'pointAdd'로 지정
    private Integer pointAdd;

    // 🚩 UserService에서 사용할 생성자
    // 이 생성자는 attendanceId와 attendanceDate를 자동으로 처리합니다.
    public Attend(Integer userId, Integer pointAdd) {
        this.userId = userId;
        this.pointAdd = pointAdd;
        this.attendanceDate = LocalDateTime.now();
    }

    // 🚩 JdbcTemplate의 RowMapper가 사용할 생성자
    // 모든 필드를 포함하며, Lombok의 @AllArgsConstructor와 중복되므로 @AllArgsConstructor를 제거합니다.
    public Attend(Integer attendanceId, Integer userId, LocalDateTime attendanceDate, Integer pointAdd) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.pointAdd = pointAdd;
    }
}