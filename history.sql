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


-- 테이블 history.points 구조 내보내기
CREATE TABLE IF NOT EXISTS `points` (
  `pointId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `totalPoint` int DEFAULT NULL,
  PRIMARY KEY (`pointId`) USING BTREE,
  KEY `FK_point_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_point_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저의 포인트 대한 정보';


-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `itemId` int NOT NULL AUTO_INCREMENT,
  `imgUrl` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `itemName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='적립된 포인트를 사용할 수 있는 항목들 기술';


-- 테이블 history.point_transactions 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_transactions` (
  `transactionId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `attendanceId` int DEFAULT NULL,
  `itemId` int DEFAULT NULL,
  `scoreId` int DEFAULT NULL,
  `reason` varchar(50) DEFAULT NULL,
  `pointChange` int DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`transactionId`) USING BTREE,
  KEY `FK_point_transactions_user` (`userId`),
  KEY `FK_point_transactions_user_attendance` (`attendanceId`),
  KEY `FK_point_transactions_point_shop` (`itemId`),
  KEY `FK_point_transactions_quiz_score` (`scoreId`),
  CONSTRAINT `FK_point_transactions_point_shop` FOREIGN KEY (`itemId`) REFERENCES `point_shop` (`itemId`),
  CONSTRAINT `FK_point_transactions_quiz_score` FOREIGN KEY (`scoreId`) REFERENCES `quiz_score` (`scoreId`),
  CONSTRAINT `FK_point_transactions_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_point_transactions_user_attendance` FOREIGN KEY (`attendanceId`) REFERENCES `user_attendance` (`attendanceId`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 거래내역 기술';


-- 테이블 history.post 구조 내보내기
CREATE TABLE IF NOT EXISTS `post` (
  `postId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `postType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `imgUrl` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `movieUrl` varchar(50) DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`postId`) USING BTREE,
  KEY `FK_post_user` (`userId`),
  CONSTRAINT `FK_post_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글(학습내용 포함)';


-- 테이블 history.post_comment 구조 내보내기
CREATE TABLE IF NOT EXISTS `post_comment` (
  `commentId` int NOT NULL AUTO_INCREMENT,
  `postId` int NOT NULL DEFAULT '0',
  `userId` int NOT NULL DEFAULT '0',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`commentId`) USING BTREE,
  KEY `FK_post_comment_post` (`postId`),
  KEY `FK_post_comment_user` (`userId`),
  CONSTRAINT `FK_post_comment_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`),
  CONSTRAINT `FK_post_comment_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';


-- 테이블 history.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quizId` int NOT NULL AUTO_INCREMENT,
  `postId` int NOT NULL,
  `imgUrl` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `question` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `questionOption` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`quizId`) USING BTREE,
  KEY `FK_quiz_post` (`postId`),
  CONSTRAINT `FK_quiz_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈항목';


-- 테이블 history.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attemptId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `quizId` int NOT NULL,
  `quizScore` int DEFAULT NULL,
  `selected` int DEFAULT NULL,
  `answer` int DEFAULT NULL,
  `correct` varchar(50) DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`attemptId`) USING BTREE,
  KEY `FK_quiz_attempt_user` (`userId`),
  KEY `FK_quiz_attempt_quiz` (`quizId`),
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quizId`) REFERENCES `quiz` (`quizId`),
  CONSTRAINT `FK_quiz_attempt_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 응시 기록';


-- 테이블 history.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `scoreId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `score` int DEFAULT NULL,
  `pointScore` int DEFAULT NULL,
  PRIMARY KEY (`scoreId`),
  KEY `FK_quiz_score_user` (`userId`),
  CONSTRAINT `FK_quiz_score_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 점수에 대한 항목';


-- 테이블 history.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';


-- 테이블 history.user_attendance 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_attendance` (
  `attendanceId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `attendanceDate` datetime DEFAULT (now()),
  `pointAdd` int DEFAULT NULL,
  PRIMARY KEY (`attendanceId`) USING BTREE,
  KEY `FK_attendance_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_user_attendance_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=922 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='출석기록';


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

