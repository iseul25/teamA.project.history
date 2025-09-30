package com.lms.history.boards.service;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.repository.BoardRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public List<Board> findByBoardType(String boardType) {
        return boardRepository.findByBoardType(boardType);
    }

    // JpaRepository가 없는 경우의 페이징 처리
    public List<Board> findByBoardTypeWithPaging(String boardType, int page, int pageSize) {
        // 이 메서드가 Repository의 findByBoardTypeWithPaging을 호출해야 합니다
        return boardRepository.findByBoardTypeWithPaging(boardType, page, pageSize);
    }

    // 총 개수를 구하는 메서드
    public long countByBoardType(String boardType) {
        return boardRepository.countByBoardType(boardType);
    }

    // 페이징 정보를 계산하는 메서드
    public int getTotalPages(String boardType, int size) {
        long totalElements = countByBoardType(boardType);
        return (int) Math.ceil((double) totalElements / size);
    }

    public Board create(Board board, int userId) {
        // 제목 비어있는지 체크
        if (board.getTitle() == null || board.getTitle().trim().isEmpty()) {
            throw new IllegalStateException("제목을 입력해주세요.");
        }

        // 내용 비어있는지 체크
        if (board.getContent() == null || board.getContent().trim().isEmpty()) {
            throw new IllegalStateException("내용을 입력해주세요.");
        }

        return boardRepository.save(board, userId);
    }

    public Board findById(int boardId) {
        return boardRepository.findById(boardId);
    }

    public void update(Board board) {
        boardRepository.update(board);
    }

    public void delete(int boardId) {
        boardRepository.deleteById(boardId);
    }

    public Board findByTitleAndType(String title, String boardType) {
        return boardRepository.findByTitleAndType(title, boardType);
    }

}
