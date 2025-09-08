
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

-- 테이블 history.point(포인트) 구조 내보내기
CREATE TABLE IF NOT EXISTS `point` (
  `point_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `points` int DEFAULT NULL,
  PRIMARY KEY (`point_id`),
  KEY `FK_point_user` (`user_id`),
  CONSTRAINT `FK_point_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저의 포인트 대한 정보';

-- 테이블 데이터 history.point:~0 rows (대략적) 내보내기

-- 테이블 history.point_shop(포인트 상점) 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `img_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `item_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='적립된 포인트를 사용할 수 있는 항목들 기술';

-- 테이블 데이터 history.point_shop:~0 rows (대략적) 내보내기

-- 테이블 history.point_transactions(포인트 사용 내역) 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `item_id` int DEFAULT NULL,
  `points_deducted` int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`) USING BTREE,
  KEY `FK_point_transactions_point_shop` (`item_id`),
  KEY `FK_point_transactions_user` (`user_id`),
  CONSTRAINT `FK_point_transactions_point_shop` FOREIGN KEY (`item_id`) REFERENCES `point_shop` (`item_id`),
  CONSTRAINT `FK_point_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 거래내역 기술';

-- 테이블 데이터 history.point_transactions:~0 rows (대략적) 내보내기

-- 테이블 history.post(게시글) 구조 내보내기
CREATE TABLE IF NOT EXISTS `post` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `img_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `created` date DEFAULT (now()),
  PRIMARY KEY (`post_id`) USING BTREE,
  KEY `FK_post_user` (`user_id`),
  CONSTRAINT `FK_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글(학습내용 포함)';

-- 테이블 데이터 history.post:~0 rows (대략적) 내보내기

-- 테이블 history.post_comment(댓글글) 구조 내보내기
CREATE TABLE IF NOT EXISTS `post_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL DEFAULT '0',
  `user_id` int NOT NULL DEFAULT '0',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_post_comment_post` (`post_id`),
  KEY `FK_post_comment_user` (`user_id`),
  CONSTRAINT `FK_post_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
  CONSTRAINT `FK_post_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';

-- 테이블 데이터 history.post_comment:~0 rows (대략적) 내보내기

-- 테이블 history.quiz(퀴즈) 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT,
  `imgurl` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `option` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `quiz_point` int DEFAULT NULL,
  PRIMARY KEY (`quiz_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈항목';

-- 테이블 데이터 history.quiz:~0 rows (대략적) 내보내기

-- 테이블 history.quiz_attempt(퀴즈 응시 기록록) 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attempt_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `quiz_id` int DEFAULT NULL,
  `selected` int DEFAULT NULL,
  `correct` varchar(50) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `attempt_time` date DEFAULT (now()),
  PRIMARY KEY (`attempt_id`),
  KEY `FK_quizdata_user` (`user_id`),
  KEY `FK_quizdata_quiz` (`quiz_id`),
  CONSTRAINT `FK_quizdata_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`),
  CONSTRAINT `FK_quizdata_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 응시 기록';

-- 테이블 데이터 history.quiz_attempt:~0 rows (대략적) 내보내기

-- 테이블 history.user(사용자 정보) 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.user:~0 rows (대략적) 내보내기

-- 테이블 history.user_activities(관리자/일반유저 활동 기록) 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_activities` (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL DEFAULT '0',
  `activity_type` varchar(50) NOT NULL DEFAULT '0',
  `activity_date` datetime NOT NULL DEFAULT (now()),
  PRIMARY KEY (`activity_id`),
  KEY `FK_user_activities` (`user_id`),
  CONSTRAINT `FK_user_activities` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저 활동 기록';

-- 테이블 데이터 history.user_activities:~0 rows (대략적) 내보내기

-- 테이블 history.user_attendance(출석 기록) 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_attendance` (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `attendance_date` date DEFAULT (now()),
  `attendance` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`attendance_id`),
  KEY `FK_attendance_user` (`user_id`),
  CONSTRAINT `FK_attendance_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=918 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='출석기록';

-- 테이블 데이터 history.user_attendance:~0 rows (대략적) 내보내기

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

