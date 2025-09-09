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

-- 테이블 데이터 history.points:~3 rows (대략적) 내보내기
INSERT INTO `points` (`pointId`, `userId`, `totalPoint`) VALUES
	(1, 2, 2120),
	(2, 3, 3080),
	(3, 5, 4190);

-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `itemId` int NOT NULL AUTO_INCREMENT,
  `imgUrl` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `itemName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='적립된 포인트를 사용할 수 있는 항목들 기술';

-- 테이블 데이터 history.point_shop:~3 rows (대략적) 내보내기
INSERT INTO `point_shop` (`itemId`, `imgUrl`, `itemName`, `cost`) VALUES
	(1, NULL, '딸기우유', 990),
	(2, NULL, '초코우유', 980),
	(3, NULL, '바나나우유', 970);

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

-- 테이블 데이터 history.point_transactions:~9 rows (대략적) 내보내기
INSERT INTO `point_transactions` (`transactionId`, `userId`, `attendanceId`, `itemId`, `scoreId`, `reason`, `pointChange`, `date`) VALUES
	(17, 2, NULL, 1, NULL, '상점이용', -990, '2025-09-09'),
	(18, 3, NULL, 2, NULL, '상점이용', -980, '2025-09-09'),
	(19, 5, NULL, 3, NULL, '상점이용', -970, '2025-09-09'),
	(20, 2, 918, NULL, NULL, '출석체크', 10, '2025-09-09'),
	(21, 3, 919, NULL, NULL, '출석체크', 10, '2025-09-09'),
	(22, 5, 920, NULL, NULL, '출석체크', 10, '2025-09-09'),
	(23, 2, NULL, NULL, 7, '퀴즈응시', 100, '2025-09-09'),
	(24, 3, NULL, NULL, 8, '퀴즈응시', 50, '2025-09-09'),
	(25, 5, NULL, NULL, 9, '퀴즈응시', 150, '2025-09-09');

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

-- 테이블 데이터 history.post:~4 rows (대략적) 내보내기
INSERT INTO `post` (`postId`, `userId`, `postType`, `title`, `imgUrl`, `movieUrl`, `content`, `date`) VALUES
	(9, 1, '공지사항', '오늘의 학습내용과 퀴즈 업로드', NULL, NULL, '금일 학습과 퀴즈 업로드를 완료하였습니다. 확인해주세요.', '2025-09-09'),
	(10, 4, '학습게시글', '난중일기란', NULL, NULL, '이순신이 임진왜란과 정유재란 당시의 기록을 일기의 형태로 기록한 사료이다.', '2025-09-09'),
	(11, 4, '학습게시글', '예송논쟁이란', NULL, NULL, '서인과 남인들이 상복입는 기간을 매개로 벌인 정쟁이다.', '2025-09-09'),
	(12, 4, '퀴즈', '제1회 퀴즈문항', NULL, NULL, '4문항 출제. 획득 포인트는 득점 X10 입니다.', '2025-09-09');

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

-- 테이블 데이터 history.post_comment:~5 rows (대략적) 내보내기
INSERT INTO `post_comment` (`commentId`, `postId`, `userId`, `comment`) VALUES
	(13, 10, 2, '잘 읽었습니다.'),
	(14, 11, 3, '서인과 남인 중에 누가 이겼나요?'),
	(15, 11, 5, '서인이 이김 ㅅㄱ.'),
	(16, 11, 3, 'ㄴ너한테 물은거 아님 ㅗㅗ'),
	(17, 11, 4, '친구끼리 싸우는거 아닙니다. ㅡㅡ');

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

-- 테이블 데이터 history.quiz:~4 rows (대략적) 내보내기
INSERT INTO `quiz` (`quizId`, `postId`, `imgUrl`, `question`, `questionOption`) VALUES
	(1, 12, NULL, '난중일기는 어느 시기에 작성되었는가?', '1.임진왜란 2.병자호란 3.계유정난 4.홍경래의난'),
	(2, 12, NULL, '난중일기는 누가 작성하였는가?', '1.권율 2.류성룡 3.이항복 4.이순신'),
	(3, 12, NULL, '난중일기에서 자주 언급되는 천지(天只)는 누구를 가리키는 말인지 맞추시오', '1.임금 2.친구 3.어머니 4.아내'),
	(4, 12, NULL, '예송논쟁은 상복입는 기간을 둘러싼 ㅇ과 ㅁ간의 논쟁이다. 여기서 ㅇ과 ㅁ을 각각 맞추시오.', '1.동인,서인  2.남인,북인 3.동인,북인 4.서인,남인');

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

-- 테이블 데이터 history.quiz_attempt:~12 rows (대략적) 내보내기
INSERT INTO `quiz_attempt` (`attemptId`, `userId`, `quizId`, `quizScore`, `selected`, `answer`, `correct`, `date`) VALUES
	(15, 2, 1, 2, 1, 1, '정답', '2025-09-09'),
	(16, 2, 2, 3, 4, 4, '정답', '2025-09-09'),
	(17, 2, 3, 5, 4, 3, '오답', '2025-09-09'),
	(18, 2, 4, 5, 4, 4, '정답', '2025-09-09'),
	(19, 3, 1, 2, 3, 1, '오답', '2025-09-09'),
	(20, 3, 2, 3, 3, 4, '오답', '2025-09-09'),
	(21, 3, 3, 5, 3, 3, '정답', '2025-09-09'),
	(22, 3, 4, 5, 3, 4, '오답', '2025-09-09'),
	(23, 5, 1, 2, 1, 1, '정답', '2025-09-09'),
	(24, 5, 2, 3, 4, 4, '정답', '2025-09-09'),
	(25, 5, 3, 5, 3, 3, '정답', '2025-09-09'),
	(26, 5, 4, 5, 4, 4, '정답', '2025-09-09');

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

-- 테이블 데이터 history.quiz_score:~3 rows (대략적) 내보내기
INSERT INTO `quiz_score` (`scoreId`, `userId`, `score`, `pointScore`) VALUES
	(7, 2, 10, 100),
	(8, 3, 5, 50),
	(9, 5, 15, 150);

-- 테이블 history.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.user:~5 rows (대략적) 내보내기
INSERT INTO `user` (`userId`, `userType`, `name`, `password`, `email`) VALUES
	(1, '관리자', '김씨', '0000', 'eee@naver.com'),
	(2, '일반유저', '이씨', '0001', 'eef@naver.com'),
	(3, '일반유저', '박씨', '0002', 'eeg@naver.com'),
	(4, '관리자', '홍씨', '0003', 'eeh@naver.com'),
	(5, '일반유저', '성씨', '0004', 'eei@naver.com');

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

-- 테이블 데이터 history.user_attendance:~3 rows (대략적) 내보내기
INSERT INTO `user_attendance` (`attendanceId`, `userId`, `attendanceDate`, `pointAdd`) VALUES
	(918, 2, '2025-09-08 00:00:00', 10),
	(919, 3, '2025-09-08 00:00:00', 10),
	(920, 5, '2025-09-08 00:00:00', 10);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
