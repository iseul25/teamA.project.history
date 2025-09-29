package com.lms.history.boards.service;

import com.lms.history.boards.entity.BoardStudy;
import com.lms.history.boards.repository.BoardStudyRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BoardStudyService {
    private final BoardStudyRepository boardStudyRepository;

    public BoardStudyService(BoardStudyRepository boardStudyRepository) {
        this.boardStudyRepository = boardStudyRepository;
    }

    // 학습 시작 기록 (startAt과 endAt을 같은 시간으로 초기 기록)
    public void startStudy(int boardId, int userId) {
        Date now = new Date();
        boardStudyRepository.insertStart(boardId, userId, now);
    }

    // 학습 완료 기록 (기존 - studyId로 완료 처리)
    public void completeStudy(int studyId) {
        boardStudyRepository.updateEnd(studyId, new Date());
    }

    // 학습 완료 기록 (새로운 - boardId와 userId로 가장 최근 기록의 endAt 업데이트)
    public void completeStudy(int boardId, int userId) {
        // 해당 사용자의 해당 게시글에 대한 가장 최근 학습 기록을 찾아서 endAt 업데이트
        List<BoardStudy> recentStudies = boardStudyRepository.findRecentByUserAndBoard(userId, boardId);

        if (!recentStudies.isEmpty()) {
            // 가장 최근 기록의 endAt을 현재 시간으로 업데이트
            BoardStudy latestStudy = recentStudies.get(0);
            boardStudyRepository.updateEnd(latestStudy.getStudyId(), new Date());
        } else {
            throw new IllegalStateException("완료 처리할 학습 기록이 없습니다.");
        }
    }

    // 사용자가 해당 게시글을 학습했는지 확인
    public boolean hasCompletedStudy(int boardId, int userId) {
        List<BoardStudy> studies = boardStudyRepository.findByUserAndBoard(userId, boardId);

        // 학습 기록이 있고, startAt과 endAt이 다른 경우 (실제로 학습 완료한 경우)
        return studies.stream().anyMatch(study ->
                study.getEndAt() != null &&
                        !study.getStartAt().equals(study.getEndAt())
        );
    }
}