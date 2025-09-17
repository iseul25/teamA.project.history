package com.lms.history.users.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Points 엔티티 - points 테이블과 매핑
 * 테이블 구조:
 * - pointId (int, AUTO_INCREMENT, PRIMARY KEY)
 * - userId (int, FK to users.userId)
 * - attendanceId (int, FK to user_attendance.attendanceId, nullable)
 * - itemId (int, FK to point_shop.itemId, nullable)
 * - quizId (int, FK to quiz.quizId, nullable)
 * - pointChange (int) - 포인트 변화량
 * - totalPoint (int) - 해당 거래 후 총 포인트
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Points {
    // Getter/Setter
    private Integer pointId;
    private Integer userId;
    private Integer attendanceId;    // nullable
    private Integer itemId;         // nullable
    private Integer quizId;         // nullable
    private Integer pointChange;    // 포인트 변화량
    private Integer totalPoint;     // 총 포인트

    // 출석 포인트용 생성자
    public Points(Integer userId, Integer attendanceId, Integer pointChange, Integer totalPoint) {
        this.userId = userId;
        this.attendanceId = attendanceId;
        this.pointChange = pointChange;
        this.totalPoint = totalPoint;
    }

}