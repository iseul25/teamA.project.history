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

-- 테이블 history.point 구조 내보내기
CREATE TABLE IF NOT EXISTS `point` (
  `point_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `points` int DEFAULT NULL,
  `reason` varchar(50) DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`point_id`),
  KEY `FK_point_user` (`user_id`),
  CONSTRAINT `FK_point_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저의 포인트 대한 정보';

-- 테이블 데이터 history.point:~0 rows (대략적) 내보내기

-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(50) DEFAULT NULL,
  `item_description` varchar(50) DEFAULT NULL,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='적립된 포인트를 사용할 수 있는 항목들 기술';

-- 테이블 데이터 history.point_shop:~0 rows (대략적) 내보내기

-- 테이블 history.point_transactions 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `item_id` int DEFAULT NULL,
  `points_deducted` int DEFAULT NULL,
  `transaction_date` datetime DEFAULT (now()),
  PRIMARY KEY (`transaction_id`) USING BTREE,
  KEY `FK_point_transactions_point_shop` (`item_id`),
  KEY `FK_point_transactions_user` (`user_id`),
  CONSTRAINT `FK_point_transactions_point_shop` FOREIGN KEY (`item_id`) REFERENCES `point_shop` (`item_id`),
  CONSTRAINT `FK_point_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 거래내역 기술';

-- 테이블 데이터 history.point_transactions:~0 rows (대략적) 내보내기

-- 테이블 history.post 구조 내보내기
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

-- 테이블 데이터 history.post:~2 rows (대략적) 내보내기
INSERT INTO `post` (`post_id`, `user_id`, `title`, `img_url`, `content`, `created`) VALUES
	(1, 1, '난중일기란', NULL, '난중일기는 충무공 이순신이 임진왜란 시기에 쓴 일기이다', '2025-09-05'),
	(2, 1, '예송논쟁이란', NULL, '예송논쟁은 효종의 어머니인 자의대비가 자식인 효종과 며느리의 상을 맞이해 몇년 상복을 입을까를 두고 벌어진 서인과 남인 간의 정쟁이다. \r\n어떻게 보면 단순히 상복 몇년 입는것으로 싸우는가 싶을정도로, 현대의 시점으로 보면 어이가 없을지경이지만 사실 이 내면엔 복잡한 사연이 있다. \r\n본래 효종은 장남이 아닌 차남이었는데, 형 소현세자가 일찍 죽게되자 그 다음 왕위 계승자로서  왕위에 오른 것이었다.\r\n여기서 적장자를 중시하는 조선시대 사대부의 입장에선 정통성에 문제가 생길 여지가 충분했던 것이었다.\r\n여기서 서인은 효종이 장남이 아니었던 점에 주목해 왕이 아닌 사대부의 예법을 적용해 3년동안 자의대비가 상복을 입어야한다고 주장했고,\r\n남인은 비록 효종이 장남이 아니었을지언정 적자로서 왕위에 올랐기 때문에 사대부가 아닌 왕의 예법을 적용해 1년동안 상복을 입어야 한다고 주장했다.', '2025-09-05');

-- 테이블 history.post_comment 구조 내보내기
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';

-- 테이블 데이터 history.post_comment:~0 rows (대략적) 내보내기
INSERT INTO `post_comment` (`comment_id`, `post_id`, `user_id`, `content`) VALUES
	(1, 2, 2, '서인과 남인 중에 누가 이겼나요?');

-- 테이블 history.quizzess 구조 내보내기
CREATE TABLE IF NOT EXISTS `quizzess` (
  `quiz_id` int NOT NULL AUTO_INCREMENT,
  `imgurl` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `question` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option` varchar(50) DEFAULT NULL,
  `quizcontent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`quiz_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈항목';

-- 테이블 데이터 history.quizzess:~0 rows (대략적) 내보내기

-- 테이블 history.quiz_attempts 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempts` (
  `attempt_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `quiz_id` int DEFAULT NULL,
  `selected` int DEFAULT NULL,
  `correct` varchar(50) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `attempt_time` datetime DEFAULT (now()),
  PRIMARY KEY (`attempt_id`),
  KEY `FK_quizdata_quizzess` (`quiz_id`),
  KEY `FK_quizdata_user` (`user_id`),
  CONSTRAINT `FK_quizdata_quizzess` FOREIGN KEY (`quiz_id`) REFERENCES `quizzess` (`quiz_id`),
  CONSTRAINT `FK_quizdata_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 응시 기록';

-- 테이블 데이터 history.quiz_attempts:~0 rows (대략적) 내보내기

-- 테이블 history.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.user:~2 rows (대략적) 내보내기
INSERT INTO `user` (`user_id`, `user_type`, `name`, `password`, `email`) VALUES
	(1, '관리자', '홍씨', '0000', 'admin@example.com'),
	(2, '유저', '김씨', '0001', 'user@example.com');

-- 테이블 history.user_activities 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_activities` (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL DEFAULT '0',
  `activity_type` varchar(50) NOT NULL DEFAULT '0',
  `activity_date` datetime NOT NULL DEFAULT (now()),
  PRIMARY KEY (`activity_id`),
  KEY `FK_mypage_activities_user` (`user_id`),
  CONSTRAINT `FK_mypage_activities_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저 활동 기록';

-- 테이블 데이터 history.user_activities:~0 rows (대략적) 내보내기

-- 테이블 history.user_attendance 구조 내보내기
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
INSERT INTO `user_attendance` (`attendance_id`, `user_id`, `attendance_date`, `attendance`) VALUES
	(1, 1, '2025-08-30', '출석'),
	(2, 2, '2025-08-30', '출석'),
	(3, 1, '2025-08-31', '출석'),
	(4, 2, '2025-08-31', '결석'),
	(5, 1, '2025-09-01', '출석'),
	(6, 2, '2025-09-01', '출석'),
	(7, 1, '2025-09-02', '출석'),
	(8, 2, '2025-09-02', '결석'),
	(9, 1, '2025-09-03', '출석'),
	(10, 2, '2025-09-03', '출석'),
	(11, 1, '2025-09-04', '결석'),
	(12, 2, '2025-09-04', '결석'),
	(13, 1, '2025-09-05', '결석'),
	(14, 2, '2025-09-05', '결석');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
