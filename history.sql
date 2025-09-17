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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글(학습내용 포함)';

-- 테이블 데이터 history.board:~0 rows (대략적) 내보내기


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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';

-- 테이블 데이터 history.board_comment:~0 rows (대략적) 내보내기

-- 테이블 history.points 구조 내보내기
CREATE TABLE IF NOT EXISTS `points` (
  `pointId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceId` int DEFAULT NULL,
  `itemId` int DEFAULT NULL,
  `quizId` int DEFAULT NULL,
  `pointChange` int DEFAULT NULL,
  `totalPoint` int DEFAULT NULL,
  PRIMARY KEY (`pointId`) USING BTREE,
  KEY `FK_point_transactions_point_shop` (`itemId`),
  KEY `FK_point_transactions_user` (`userId`),
  KEY `FK_point_transactions_user_attendance` (`attendanceId`),
  KEY `FK_point_transactions_quiz_score` (`quizId`) USING BTREE,
  CONSTRAINT `FK_points_point_shop` FOREIGN KEY (`itemId`) REFERENCES `point_shop` (`itemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_quiz` FOREIGN KEY (`quizId`) REFERENCES `quiz` (`quizId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user_attendance` FOREIGN KEY (`attendanceId`) REFERENCES `user_attendance` (`attendanceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 내역 기술';

-- 테이블 데이터 history.points:~0 rows (대략적) 내보내기

-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `itemId` int NOT NULL AUTO_INCREMENT,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `itemName` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='적립된 포인트를 사용할 수 있는 항목들 기술';

-- 테이블 데이터 history.point_shop:~0 rows (대략적) 내보내기

-- 테이블 history.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quizId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item3` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `selectedItem` int DEFAULT NULL,
  `answer` int DEFAULT NULL,
  `quizScore` int DEFAULT NULL,
  `totalScore` int DEFAULT NULL,
  `earnedPoint` int DEFAULT NULL,
  PRIMARY KEY (`quizId`) USING BTREE,
  KEY `FK_quiz_users` (`userId`),
  CONSTRAINT `FK_quiz_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈항목';

-- 테이블 데이터 history.quiz:~0 rows (대략적) 내보내기

-- 테이블 history.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.users:~4 rows (대략적) 내보내기


-- 테이블 history.user_attendance 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_attendance` (
  `attendanceId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceDate` datetime DEFAULT (now()),
  `pointAdd` int DEFAULT NULL,
  PRIMARY KEY (`attendanceId`) USING BTREE,
  KEY `FK_attendance_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_user_attendance_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=942 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='출석기록';

-- 테이블 데이터 history.user_attendance:~1 rows (대략적) 내보내기


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
