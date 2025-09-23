-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        8.4.5 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- history 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `history` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `history`;

-- 테이블 history.board 구조 내보내기
CREATE TABLE IF NOT EXISTS `board` (
  `boardId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `boardType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `imgDescription` varchar(100) DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`boardId`) USING BTREE,
  KEY `FK_board_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_board_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글(학습내용 포함)';

-- 테이블 데이터 history.board:~15 rows (대략적) 내보내기


-- 테이블 history.board_comment 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_comment` (
  `commentId` int NOT NULL AUTO_INCREMENT,
  `boardId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`commentId`) USING BTREE,
  KEY `FK_post_comment_user` (`userId`),
  KEY `FK_post_comment_post` (`boardId`) USING BTREE,
  CONSTRAINT `FK_board_comment_board` FOREIGN KEY (`boardId`) REFERENCES `board` (`boardId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_board_comment_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';

-- 테이블 데이터 history.board_comment:~1 rows (대략적) 내보내기


-- 테이블 history.comment_reply 구조 내보내기
CREATE TABLE IF NOT EXISTS `comment_reply` (
  `replyId` int NOT NULL AUTO_INCREMENT,
  `commentId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`replyId`),
  KEY `FK_board_reply_board_comment` (`commentId`),
  KEY `FK_board_reply_users` (`userId`),
  CONSTRAINT `FK_board_reply_board_comment` FOREIGN KEY (`commentId`) REFERENCES `board_comment` (`commentId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_board_reply_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='답글에 대한 항목';

-- 테이블 데이터 history.comment_reply:~0 rows (대략적) 내보내기

-- 테이블 history.points 구조 내보내기
CREATE TABLE IF NOT EXISTS `points` (
  `pointId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceId` int DEFAULT NULL,
  `itemId` int DEFAULT NULL,
  `scoreId` int DEFAULT NULL,
  `pointChange` int DEFAULT NULL,
  `totalPoint` int DEFAULT NULL,
  PRIMARY KEY (`pointId`) USING BTREE,
  KEY `FK_point_transactions_point_shop` (`itemId`),
  KEY `FK_point_transactions_user` (`userId`),
  KEY `FK_point_transactions_user_attendance` (`attendanceId`),
  KEY `FK_point_transactions_quiz_score` (`scoreId`) USING BTREE,
  CONSTRAINT `FK_points_point_shop_item` FOREIGN KEY (`itemId`) REFERENCES `point_shop` (`itemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_quiz_score` FOREIGN KEY (`scoreId`) REFERENCES `quiz_score` (`scoreId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user_attendance` FOREIGN KEY (`attendanceId`) REFERENCES `user_attendance` (`attendanceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 내역 기술';

-- 테이블 데이터 history.points:~6 rows (대략적) 내보내기


-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `itemId` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `imgUrl` varchar(255) DEFAULT NULL,
  `itemName` varchar(50) DEFAULT NULL,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트샵 항목 기술';

-- 테이블 데이터 history.point_shop:~5 rows (대략적) 내보내기


-- 테이블 history.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quizId` int NOT NULL AUTO_INCREMENT,
  `quizCategoryId` int DEFAULT NULL,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item1` varchar(100) DEFAULT NULL,
  `item2` varchar(100) DEFAULT NULL,
  `item3` varchar(100) DEFAULT NULL,
  `item4` varchar(100) DEFAULT NULL,
  `answer` int DEFAULT NULL,
  `commentary` text,
  `quizScore` int DEFAULT NULL,
  PRIMARY KEY (`quizId`) USING BTREE,
  KEY `FK_quiz_quiz_category` (`quizCategoryId`),
  CONSTRAINT `FK_quiz_quiz_category` FOREIGN KEY (`quizCategoryId`) REFERENCES `quiz_category` (`quizCategoryId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 각 문항에 대한 항목';

-- 테이블 데이터 history.quiz:~10 rows (대략적) 내보내기

-- 테이블 history.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attemptId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `quizCategoryId` int DEFAULT NULL,
  `quizId` int DEFAULT NULL,
  `selected` int DEFAULT NULL,
  `earnedScore` int DEFAULT NULL,
  `attemptAt` datetime DEFAULT NULL,
  PRIMARY KEY (`attemptId`),
  KEY `FK_quiz_attempt_quiz_question` (`quizId`),
  KEY `FK_quiz_attempt_users` (`userId`),
  KEY `FK_quiz_attempt_quiz_category` (`quizCategoryId`),
  CONSTRAINT `FK_quiz_attempt_quiz_category` FOREIGN KEY (`quizCategoryId`) REFERENCES `quiz_category` (`quizCategoryId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz_question` FOREIGN KEY (`quizId`) REFERENCES `quiz` (`quizId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 응시에 대한 항목';

-- 테이블 데이터 history.quiz_attempt:~10 rows (대략적) 내보내기

-- 테이블 history.quiz_category 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_category` (
  `quizCategoryId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `quizType` varchar(50) DEFAULT NULL,
  `quizListName` varchar(50) DEFAULT NULL,
  `createAt` date DEFAULT (now()),
  PRIMARY KEY (`quizCategoryId`),
  KEY `FK_quiz_category_users` (`userId`),
  CONSTRAINT `FK_quiz_category_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 카테고리 분류에 관한 항목';

-- 테이블 데이터 history.quiz_category:~0 rows (대략적) 내보내기

-- 테이블 history.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `scoreId` int NOT NULL AUTO_INCREMENT,
  `quizCategoryId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `totalScore` int DEFAULT NULL,
  `earnedPoint` int DEFAULT NULL,
  PRIMARY KEY (`scoreId`),
  KEY `FK_quiz_score_quiz` (`quizCategoryId`) USING BTREE,
  KEY `FK_quiz_score_users` (`userId`),
  CONSTRAINT `FK_quiz_score_quiz_category` FOREIGN KEY (`quizCategoryId`) REFERENCES `quiz_category` (`quizCategoryId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 채점 및 포인트 환산에 관한 항목';

-- 테이블 데이터 history.quiz_score:~1 rows (대략적) 내보내기

-- 테이블 history.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.users:~5 rows (대략적) 내보내기


-- 테이블 history.user_attendance 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_attendance` (
  `attendanceId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceDate` datetime DEFAULT (now()),
  `pointAdd` int DEFAULT NULL,
  PRIMARY KEY (`attendanceId`) USING BTREE,
  KEY `FK_attendance_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_user_attendance_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=972 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='출석기록';

-- 테이블 데이터 history.user_attendance:~5 rows (대략적) 내보내기


-- 트리거 history.quiz_attempt_before_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `quiz_attempt_before_insert` BEFORE INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE quiz_answer INT;
    DECLARE quiz_score_value INT;
    
    -- quiz 테이블에서 해당 quizId의 answer와 quizScore를 조회
    SELECT answer, quizScore 
    INTO quiz_answer, quiz_score_value
    FROM quiz 
    WHERE quizId = NEW.quizId;
    
    -- selected와 answer가 같으면 quizScore를, 다르면 0을 earnedScore에 설정
    IF NEW.selected = quiz_answer THEN
        SET NEW.earnedScore = quiz_score_value;
    ELSE
        SET NEW.earnedScore = 0;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 history.tr_cleanup_quiz_score_after_attempt_delete 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `tr_cleanup_quiz_score_after_attempt_delete` AFTER DELETE ON `quiz_attempt` FOR EACH ROW BEGIN
-- 삭제된 quiz_attempt와 같은 userId, quizCategoryId를 가진 다른 시도가 있는지 확인
    DECLARE attempt_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO attempt_count
    FROM quiz_attempt 
    WHERE userId = OLD.userId 
      AND quizCategoryId = OLD.quizCategoryId;
    
    -- 해당 사용자의 해당 퀴즈 카테고리에 대한 시도가 더 이상 없다면 quiz_score도 삭제
    IF attempt_count = 0 THEN
        DELETE FROM quiz_score 
        WHERE userId = OLD.userId 
          AND quizCategoryId = OLD.quizCategoryId;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 history.tr_quiz_score_management 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `tr_quiz_score_management` AFTER INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE total_score INT DEFAULT 0;
    DECLARE earned_point INT DEFAULT 0;
    DECLARE score_exists INT DEFAULT 0;
    
    -- 해당 사용자와 퀴즈 카테고리의 총점 계산
    SELECT COALESCE(SUM(earnedScore), 0) INTO total_score
    FROM quiz_attempt
    WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    
    -- 획득 포인트 계산 (총점의 1배)
    SET earned_point = total_score * 1;
    
    -- quiz_score 테이블에 해당 레코드가 있는지 확인
    SELECT COUNT(*) INTO score_exists
    FROM quiz_score
    WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    
    -- 레코드가 존재하면 업데이트, 없으면 삽입
    IF score_exists > 0 THEN
        UPDATE quiz_score 
        SET totalScore = total_score, 
            earnedPoint = earned_point
        WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    ELSE
        INSERT INTO quiz_score (quizCategoryId, userId, totalScore, earnedPoint)
        VALUES (NEW.quizCategoryId, NEW.userId, total_score, earned_point);
    END IF;
    
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
