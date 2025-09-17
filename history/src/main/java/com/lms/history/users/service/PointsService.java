package com.lms.history.users.service;

import com.lms.history.users.entity.Points;
import com.lms.history.users.repository.PointsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PointsService {

    private final PointsRepository pointsRepository;

    public PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    /**
     * 출석 포인트 적립
     * @param userId 사용자 ID
     * @param attendanceId 출석 기록 ID
     * @param pointChange 적립할 포인트
     * @return 적립 후 총 포인트
     */
    public int addAttendancePoint(int userId, int attendanceId, int pointChange) {
        // 현재 총 포인트 조회
        int currentTotal = getTotalPoints(userId);
        int newTotal = currentTotal + pointChange;

        // 포인트 기록 생성 및 저장
        Points points = new Points(userId, attendanceId, pointChange, newTotal);
        pointsRepository.save(points);

        return newTotal;
    }

    /**
     * 사용자의 현재 총 포인트 조회
     * @param userId 사용자 ID
     * @return 현재 총 포인트
     */
    public int getTotalPoints(int userId) {
        Integer totalPoints = pointsRepository.getTotalPointsByUserId(userId);
        return totalPoints != null ? totalPoints : 0;
    }

    /**
     * 사용자의 포인트 내역 조회
     * @param userId 사용자 ID
     * @return 포인트 내역 리스트
     */
    public List<Points> getPointHistory(int userId) {
        return pointsRepository.findByUserId(userId);
    }
}