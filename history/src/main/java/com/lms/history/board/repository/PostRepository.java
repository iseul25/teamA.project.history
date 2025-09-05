// board/repository/PostRepository.java
package com.lms.history.board.repository;

import com.lms.history.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
