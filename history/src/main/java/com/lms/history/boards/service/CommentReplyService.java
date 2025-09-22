package com.lms.history.boards.service;

import com.lms.history.boards.entity.CommentReply;
import com.lms.history.boards.repository.CommentReplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentReplyService {
    private final CommentReplyRepository replyRepository;

    public CommentReplyService(CommentReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    public List<CommentReply> findByCommentId(int commentId) {
        return replyRepository.findByCommentId(commentId);
    }

    public CommentReply create(int commentId, int userId, String reply) {
        if (reply == null || reply.trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용을 입력해주세요.");
        }

        CommentReply commentReply = new CommentReply();
        commentReply.setCommentId(commentId);
        commentReply.setUserId(userId);
        commentReply.setReply(reply.trim());

        return replyRepository.save(commentReply);
    }

    public void delete(int replyId, int userId, String userType) {
        // 관리자는 모든 답글 삭제 가능, 일반 사용자는 본인 답글만
        if (!"관리자".equals(userType)) {
            // 본인 답글인지 확인하는 로직 필요 (생략)
        }
        replyRepository.deleteById(replyId);
    }

    public void deleteByCommentId(int commentId) {
        replyRepository.deleteByCommentId(commentId);
    }
}
