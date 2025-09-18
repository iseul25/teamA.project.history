package com.lms.history.boards.service;

import com.lms.history.boards.entity.Comment;
import com.lms.history.boards.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 댓글 생성
    public Comment create(int boardId, int userId, String commentContent) {
        if (commentContent == null || commentContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        Comment comment = new Comment();
        comment.setBoardId(boardId);
        comment.setUserId(userId);
        comment.setComment(commentContent.trim());
        comment.setDate(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // 특정 게시글의 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<Comment> findByBoardId(int boardId) {
        return commentRepository.findByBoardIdOrderByDateDesc(boardId);
    }

    // 댓글 조회 (단일) - 삭제 권한 체크용
    @Transactional(readOnly = true)
    public Comment findById(int commentId) {
        return commentRepository.findById(commentId);
    }

    // 댓글 삭제 (권한 체크 포함)
    public void delete(int commentId, int userId, String userType) {
        Comment comment = findById(commentId);

        // 본인이 작성한 댓글이거나 관리자인 경우만 삭제 가능
        if (comment.getUserId() != userId && !"관리자".equals(userType)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 특정 게시글의 모든 댓글 삭제 (게시글 삭제 시 사용)
    public void deleteByBoardId(int boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
