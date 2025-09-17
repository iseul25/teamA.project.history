package com.lms.history.boards.service;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.repository.BoardRepository;
import com.lms.history.users.entity.User;
import org.springframework.stereotype.Service;

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
}
