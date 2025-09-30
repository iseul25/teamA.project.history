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
  `videoUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `imgDescription` varchar(100) DEFAULT NULL,
  `date` date DEFAULT (now()),
  PRIMARY KEY (`boardId`) USING BTREE,
  KEY `FK_board_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_board_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글(학습내용 포함)';

-- 테이블 데이터 history.board:~29 rows (대략적) 내보내기
INSERT INTO `board` (`boardId`, `userId`, `boardType`, `title`, `content`, `videoUrl`, `imgUrl`, `imgDescription`, `date`) VALUES
	(38, 1, '고조선과 여러 나라', '고려시대', '고려시대', NULL, NULL, '', '2025-09-23'),
	(39, 1, '삼국과 가야', '삼국과 가야', '삼국과 가야', NULL, NULL, '', '2025-09-23'),
	(53, 1, '공지사항', 'LMS 이용 방법 안내', '처음 메인화면에 들어오시면 오른쪽 중앙 하단부분에 회원가입 버튼을 누르시면 회원가입을 진행할 수 있습니다.\r<br/>회원가입 후 로그인을 하시면 학습목록에서 학습게시물들을 열람 하실 수 있습니다.\r<br/>학습 게시물을 보고 학습을 마친 후 게시물 안 하단에 있는 퀴즈 버튼을 누르지 않으면 게시물에서 학습을 다 하여도 인정되지 않아 퀴즈를 풀지 않은 것으로 처리됩니다.\r<br/>버튼을 누르시고 나면 학습한 내용 관련 퀴즈를 푸실 수 있습니다. \r<br/>아직 학습하지 않은 내용은 퀴즈를 풀 수 없습니다.', NULL, '', '', '2025-09-29'),
	(54, 1, '공지사항', '포인트 상점 안내', '포인트는 출석하기와 퀴즈풀기를 통해 포인트를 얻을 수 있습니다.\r\n출석은 +10p 퀴즈는 한 문제당 +10p (60점 이상 포인트 지급)를 통해 얻을 수 있습니다.\r\n얻은 포인트를 통해 포인트 상점에서 원하는 상품을 구매하실 수 있습니다.\r\n상품을 구매하시면 포인트는 그만큼 차감되고 마이페이지에서 구입한 상품을 보실 수 있습니다.\r\n사용/환불도 마이페이지 구매내역에서 진행하실 수 있습니다.', NULL, NULL, '', '2025-09-29'),
	(55, 1, '공지사항', '학습 및 퀴즈 일정 안내', '학습 주제\r\n조선의 건국과 건국 이념\r\n학습 영상 : "이성계와 위화도 회군"\r\n영상 링크 : https://youtu.be/RtCjbhORaY8?si=aiangxhYZ1Y4bcOM\r\n퀴즈 주제 및 활동\r\n조선의 건국과 건국 이념\r\n객관식 퀴즈 10문제\r\n참고사항\r\n학습 게시물 활동은 학습을 한 이후 밑에 퀴즈 버튼을 눌러야 과정 이수로 인정됩니다.\r\n퀴즈는 60점 이상 넘어야 포인트가 지급됩니다.\r\n학습한 내용이 아닌 퀴즈는 학습하기를 통해 학습을 한 이후에 풀 수 있습니다.', NULL, '', '', '2025-09-29'),
	(56, 1, '공지사항', '신규 컨텐츠 업로드 안내', '학습을 도와줄 새로운 학습 게시물이 추가 되었습니다.\r\n\r\n업로드 정보\r\n학습 주제 : 조선의 정치 제도\r\n주요 내용 : 조선의 중앙 집권 체제, 의정부와 6조, 삼사(사헌부, 사간원, 홍문관)의 역할, 과거 제도 설명\r\n\r\n영상 : 링크\r\n\r\n퀴즈 주제 : 조선의 정치 제도\r\n객관식 10문제\r\n\r\n학습 자료, 퀴즈 문제 업로드', NULL, NULL, '', '2025-09-29'),
	(57, 1, '공지사항', 'LMS 시스템 점검 안내', '학습 시스템 점검이 다음 시간에 진행됩니다.\r\n\r\n점검 시간 : **월 **일 *시 ~ *시\r\n해당 시간에는 로그인, 학습 게시물 열람, 퀴즈 풀기가 불가능합니다.\r\n\r\n학습에 불편 없도록 점검 전까지 이용해주세요. 감사합니다.', NULL, NULL, '', '2025-09-29'),
	(58, 1, '조선시대', '조선의 건국과 건국 이념', '고려 말, 나라는 전쟁과 정치 싸움으로 혼란스러웠다.\r\n백성들은 힘들고, 외국(특히 명나라)과의 외교 문제도 복잡했다. 그런 상황에서 이성계라는 장군이 등장한다.\r\n그는 위화도에서 군대를 돌려 왕의 명령을 거부하고 돌아와 권력을 잡게 된다. 이걸 위화도 회군이라고 부른다.\r\n그 후 이성계는 고려를 무너뜨리고 1392년에 조선을 세운다. 조선을 세울 때 단순히 왕만 바꾼 것이 아닌 나라 전체의 운영 방식을 바꾸려고 하였다. 고려는 불교 중심이었는데, 조선은 유교(성리학)를 바탕으로 나라를 다스리는 걸 목표로 하였다. 유교는 \'효도, 예의, 바른 정치\'를 중요하게 여기는 사상이었다.\r\n조선은 새로운 시대였고, 새로운 규칙과 제도도 필요했다. 이성계와 함께 조선을 설계한 인물이 정도전인데, 그는 법과 정치제도를 정비하고 수도도 한양(서울)으로 옮겼다. 이때부터 지금까지 서울이 우리나라 수도로 이어지고 있다.\r\n* 조선의 건국은 단순한 왕 교체가 아닌 나라의 생각과 시스템을 완전히 바꾸는 일이었다. *', 'https://www.youtube.com/embed/RtCjbhORaY8', '', '', '2025-09-29'),
	(59, 1, '조선시대', '조선의 정치 제도', '조선은 왕이 중심이 되는 나라였다. 하지만 왕 혼자 모든걸 결정하는건 아니었다. 왕의 결정을 돕고 조언하는 조직들이 있었다. 그중 가장 중요한 것이 의정부와 6조이다.\r\n의정부 : 고위 관리들이 모여 왕에게 중요한 정책을 논의하고 조언하는 회의 기구\r\n6조 : 실제로행정 업무를 처리하는 부서, 예를 들어 인사 문제를 맡는 이조, 형벌을 다루는 형조 같은 부서들\r\n또한, 왕이 실수하거나 잘못된 결정을 하지 않도록 감시하는 기관도 있었다. 바로 사헌부, 사간원, 홍문관, 이 세 기관을 함쳐서 삼사(三司)라고 한다. 지금으로 치면 왕을 견제하고 비판하는 감시기관이라고 볼 수 있다.\r\n관리(관직자)가 되려면 과거 시험을 봐야했다. 열심히 공부해서 시험에 붙은 사람만이 나라 일을 할 수 있었다. 지금으로 따지면 공부원 시험과도 비슷하다.\r\n* 조선의 정치 제도는 \'왕 중심이지만, 견제와 협력이 있는 체제\' 였다 *', NULL, '', '', '2025-09-29'),
	(60, 1, '조선시대', '조선의 왕들 - 왕으로 보는 조선의 역사', '조선이라는 나라는 고려가 무너지고 새롭게 세워진 나라이다. 그 시작을 만든 사람이 바로 이성계, 조선의 첫 번째 왕이다. 원래 그는 고려의 장군이었지만, 나라가 혼란스럽고 몽골과 명나라 사이에서 갈팡질팡하는 고려의 상황을 보고 위화도 회군이라는 큰 결정을 하게 됀다. 군대를 돌려 왕의 명령을 어기고 수도로 돌아왔다. 결국 고려는 무너지고, 이성계는 1392년에 조선이라는 이름으로 다시 나라를 세웠다. 그리고 수도를 한양(지금의 서울)으로 옮겼다. 하지만 정치를 주도하던 정도전과의 갈등, 아들들의 왕위 다툼 등으로인해서 태조는 오래 왕 자리에 있지 않고 물러났다.\r\n태조 이성계는 새로운 나라의 문을 연 왕이다. (조선을 세운 왕)\r\n\r\n	세종대왕, 1418~1450, 훈민정음 창제 / 과학 발명 (장영실 등) / 측우기, 자격루 / 집현전 설치\r\n	세종대왕은 조선의 4번째 왕이다. 조선의 역사에서 가장 위대한 왕으로 꼽힌다. 왜냐, 백성들을 굉장히 많이 생각한 왕이었기 때문이다. 그중에서도 가장 유명한 업적은 바로 한글(훈민정음)의 창제이다. 예전에는 한자만 썼기 때문에 글을 배우는것이 많이 어려웠다. 그러한 이유 때문에 백성들은 대부분 글을 읽거나 쓰지 못했다. 세종대왕은 "백성이 글을 몰라 억울한 일을 당하면 안 된다" 라고 생각하고, 누구나 쉽게 배울 수 있는 우리말 글자를 만든것이다. 그게 지금 우리가 사용하고 있는 한글이다. 또한 세종대왕은 장영실이라는 뛰어난 과학자를 등용해서 해시계(양구일구), 물시계(자격루), 비를 재는 측우기 등을 만들게 하였다. 그것은 농사짓는 백성들에게 정말 유용한 도구였다.\r\n세종대왕은 백성들을 많이 생각해주는 자상한 왕이었다. (한글을 만든 백성의 왕)\r\n\r\n	세조, 1455~1468, 수양대군, 조카 단종 폐위 / 군사 강화, 법 정비 / 경국대전 편찬 시작\r\n	세조는 세종대왕의 아들이지만, 정당한 왕위 계승자는 아니었다. 원래 세종의 손자인 단종이 왕이었는데, 세조는 동생들을 모아 힘으로 단종을 몰아내고 왕이 되었다. 그래서 세조는 권력을 위해 가족까지 몰아낸 냉혹한 인물로 평가되고 있다. 한편으로는 나라를 다스리는 제도와 법을 정비한 왕이기도 하다. 특히 경국대전이라는 법률책을 만들기 시작했고, 군사제도도 강화하였다.\r\n세조는 무서운 이미지지만 행정력이 강했던 왕이었다. (권력을 잡은 실용 행정가)\r\n	\r\n	성종, 1469~1494, 경국대전 완성 / 유교 정치 완성 / 홍문관 설치\r\n	성종은 세조의 손자이다. 조선의 법과 제도를 정비해서, 유교 정치를 제대로 실현한 왕으로 평가된다. 세조가 시작한 경국대전을 완성했고, 나라에 필요한 기록과 책들도 많이 만들었다. 성종 때는 흥문관이라는 기관도 생겼다. 젊은 학자들이 모여서 왕에게 조언도 하고, 학문을 연구하였다. 이 시기는 조선의 정치와 문화가 안정된 시기라고 볼 수 있다.\r\n성종은 유교 정치와 문화 발전의 기반을 다진 왕이다. (유교 정치를 완성한 왕)\r\n\r\n	정조, 1776~1800, 규장각 설치 / 실학 장려 / 수원 화성 건설 / 탕평책 추진\r\n	정조는 조선 후기의 대표적인 개혁 군주이다. 그는 아버지인 사도세자가 억울하게 죽은 일을 마음에 품고 있었고, 그런 아픔 속에서도 아주 똑똑하고 강한 왕이 되었다. 정조는 규장각이라는 학문 기관을 만들고, 인재를 고루 등용하려는 탕평책을 펼쳤다. 또, 실학자들을 지원하면서 현실을 바꾸려는 공부(실학)를 장려하였다. 수원 화성을 만든것도 정조의 업적이다. 굉장히 아름답고 튼튼한 성이다.\r\n정조는 개혁, 실학, 인재를 사랑하는 왕이었다. (실학을 장려한 개혁 군주)', NULL, NULL, '', '2025-09-29'),
	(61, 1, '조선시대', '세종대왕과 과학 문화의 발전', '세종대왕은 조선에서 가장 존경받는 왕이었다. 그 이유는 단 하나, 백성을 정말로 위했기 때문이었다. 그 당시에는 한자만 썼기때문에 글을 배우기 정말 어려웠다. 그래서 대부분의 백성들은 글을 몰랐고, 억울한 일을 당해도 호소할 길이 없었다. 세종대왕은 백성들이 스스로 읽고 쓸 수 있는 새로운 문자, 바로 훈민정음(한글)을 만들었다. 이건 정말 대단한 일이었다. 전 세계적으로도 왕이 백성을 위해 글자를 만든 예는 거의 없다. 또한 세종은 과학기술도 중요하게 생각하였다. 장영실 같은 천재 과학자를 적극 등용하여 발명품들을 만들게 하였다.\r\n양부일구(해시계) : 해 그림자를 통해 시간을 알려주는 시계\r\n자격루(물시계) : 일정한 간격으로 물이 떨어지면서 시간을 알려주는 시계\r\n측우기 : 비의 양을 측정하는 기구 (세계 최초의 강수량 측정 도구)\r\n이 발명품들은 농사를 짓는 데 큰 도움이 되었다. 날씨와 시간을 정확히 아는 것이 정말 중요하였기 때문이다.\r\n* 세종은 글자와 과학을 백성을 돕는 진짜 리더였다 *', NULL, '', '', '2025-09-29'),
	(62, 1, '조선시대', '조선을 지킨 사람 - 임진왜란과 이순신', '조선이 세워지고 200년쯤 지난 후, 큰 전쟁이 일어났다. 바로 임진왜란(1592)이다. 일본의 도요토미 히데요시가 조선을 침략한 전쟁이었다. 준비가 부족했던 조선은 처음에 크게 당했지만, 곧 의병(스스로 나선 백성들의 군대)과 명나라의 도움으로 저항하기 사작하였다. 이때 가장 활약한 인물이 이순신 장군이다. 이순신은 거북선을 이용해 바다에서 일본 군대를 막아내며, 조선 수군의 힘을 보여주었다. 특히 한산도 대첩 등 여러 해전에서 연승하면서 전세를 뒤집었다. 그는 "신에게는 아직 열두 척의 배가 남아 있사옵니다" 라는 말로도 유명하다.(명량 해전때)\r\n전쟁이 끝난 후, 조선은 군사 제도를 더 튼튼하게 고치고 국방의 중요성을 깨닫게 된다.\r\n* 임진왜란은 조선에 큰 피해를 줬지만, 이순신과 백성의 용기로 나라를 지킨 사건이었다 *', NULL, '', '', '2025-09-29'),
	(63, 1, '조선시대', '백성들의 삶과 조선의 신분제', '조선 시대는 지금보다 훨씬 계급이 엄격한 사회였다. 사람들은 태어날 때부터 신분이 정해져 있었고, 그 신분에 따라 직업과 권리가 달랐다.\r\n\r\n주요 신분\r\n양반 : 정치, 학문, 문화를 주도하는 계급. 나라의 중요한 일을 맡음\r\n중인 : 기술자, 의사, 역관(통역사) 등 전문 직업을 가진 중간 계층\r\n상민 : 농사짓고 세금 내는 일반 백성. 가장 인구가 많았음\r\n천민 : 가장 낮은 신분. 대개 힘든 일을 하며 차별을 많이 받음\r\n\r\n하지만 조선은 농사를 아주 중요하게 여긴 나라였다. 그래서 백성들이 농사를 잘 지을수 있도록 농서(농사책)도 만들고, 이앙법(모를 옮겨심는 농법)도 보급했다. 또한 시장(장터)도 발달하였다. 사람들이 직접 물건을 사고파는 공간에서 상업이 조금씩 자라기 시작하였다.\r\n* 조선의 백성들은 엄격한 신분제 안에서도 열심히 살아가며 사회를 떠받쳤던 존재였다. *', NULL, NULL, '', '2025-09-29'),
	(64, 1, '조선시대', '조선 후기와 실학의 등장', '조선 후기로 가면서 사회는 점점 바뀌기 시작했다. 양반 중심의 사회가 한계를 드러내고, 백성들의 현실적인 문제들이 쌓여갔다.\r\n이때 등장한 새로운 학문이 바로 실학(實學)이었다. 실학은 말 그대로 "실제 생활에 도움이 되는 공부" 라는 뜻이다. 책 속에서만 배우는 게 아니라, 현실 문제를 어떻게 해결할지 고민하는 학문이었다.\r\n대표적인 실학자들이다.\r\n\r\n정약용 : 목민심서라는 책을 써서, 백성을 잘 다스리는 방법을 연구함\r\n박지원 : 청나라에 다녀온 후 상공업과 기술 발전의 중요성을 강조함\r\n홍대용 : 지구와 우주의 원리를 연구하며 과학과 사상의 발전을 꿈꾼 학자\r\n\r\n이 실학자들은 지금으로 치면 정치 개혁가이자 사회 연구자 같은 역할을 하였다.\r\n* 조선 후기는 새로운 변화와 실용적인 지식이 싹튼 시기였다. *', NULL, NULL, '', '2025-09-29'),
	(65, 1, '조선시대', '조선의 과거제도와 교육 제도', '조선에서는 나라를 다스릴 관리(관료)를 뽑기 위해 시험을 봤다. 이 시험을 과거제도라고 한다.\r\n오늘날의 공무원 시험처럼, 열심히 공부해서 합격하면 높은 벼슬도 할 수 있었다. 대표적으로는 문과와 무과, 그리고 기술직 시험인 잡과가 있었다.\r\n문과 : 유교 경전을 잘 아는 사람을 뽑는 시험\r\n무과 : 무술과 전략 등 군사에 능한 사람을 뽑는 시험\r\n잡과 : 의사, 천문학자, 역관(통역사) 등 기술직 시험\r\n\r\n하지만 신분의 벽이 완전히 없어지진 않았다. 양반이 아니면 시험을 보기조차 어려운 경우도 많았다. 조선에는 교육기관도 있었다.\r\n\r\n서당 : 마을 아이들이 한문가 유교를 배우는 곳\r\n향교 : 지방의 관립 학교\r\n성균관 : 서울에 있는 최고 교육기관, 관리가 되기 위한 곳\r\n\r\n* 조선은 시험을 통해 능력을 인정하려 했지만, 신분 차별이 여전히 남아 있었던 사회이다. *', NULL, NULL, '', '2025-09-29'),
	(66, 1, '조선시대', '조선의 수도 - 한양의 도시 구조와 발전', '조선이 처음 세워졌을 때 수도는 한양(지금의 서울)이었다.\r\n이성계와 정도전은 한양이 지리적으로 중심에 있고, 산과 강이 있어 방어와 교통이 모두 유리하다고 생각하였다. \r\n한양은 유교적인 질서에 따라 계획적으로 설계된 도시였다. \r\n중앙에는 왕이 사는 경복궁이 있었고, 도시는 북악산을 등지고 남쪽을 바라보는 구조였다. \r\n사방에는 4대문(흥인지문, 숭례문, 숙정문, 돈의문)이 세워졌고, 중심 거리인 종로에는 상점과 관청이 있었다. \r\n한양에는 육의전이라는 정부가 운영한 큰 상점들이 있었고, 저자거리(시장)에서는 일반 백성들이 물건을 사고 팔 수 있었다.\r\n\r\n* 조선의 한양은 단순한 수도가 아니라, 왕권과 유교질서를 도시 설계에 담은 상징적인 도시였다. *', NULL, '', '', '2025-09-29'),
	(67, 1, '조선시대', '조선의 법과 범죄 처벌 제도', '조선은 나라를 안정적으로 운영하기 위해 법을 아주 중요하게 여겼다. 조선의 대표적인 법전은 바로 경국대전이다. 경국대전은 조선의 행정, 형벌, 예법 등을 정리한 조선의 헌법 같은 책이다.\r\n범죄를 저지른 사람에게는 신분과 죄의 무게에 따라 처벌이 달라졌다.\r\n형벌의 종류는 다음과 같다.\r\n\r\n태형 : 회초리로 때리는 형벌 (가벼운 범죄)\r\n장형 : 곤장을 치는 형벌 (태형보다 무거움)\r\n도형 : 일정 기간 노동형\r\n유배 : 멀리 시골이나 외딴섬으로 쫓아내는 벌\r\n사형 : 가장 무거운 형벌\r\n\r\n또한 조선에는 신문고 라는 제도가 있었다. 억울한 백성들이 북을 치면, 직접 억울함을 왕에게 알릴 수 있었다. 하지만 실제로 이 제도를 쓰기에는 쉽지 않았다.\r\n* 조선은 법으로 나라를 다스리려 했지만, 신분과 권력의 차이가 법의 공정함을 가로막기도 했다. *', NULL, NULL, '', '2025-09-29'),
	(68, 1, '조선시대', '조선의 예술과 놀이 문화', '조선은 엄격한 신분 사회였지만, 사람들은 나름의 방식으로 예술과 놀이를 즐기며 살아갔다.\r\n서민층은 말로 전해지던 이야기를 글로 적은 한글 소설을 읽기 시작했고, 귀로 듣는 극 형태인 판소리도 유행했다. 예를 들어 <춘향전>, <흥부전>, <심청전> 같은 이야기들이 이 시기에 만들어지고 퍼졌다. 탈춤, 줄타기, 풍물놀이 같은 민속 예술도 마을 잔치나 명절 때 많이 공연되었고, 백성들의 스트레스를 풀어주는 역할을 하였다. 반면 양반층은 시를 짓고 글씨(서예)를 쓰거나 그림을 그리는 것을 취미로 삼았다. 김흥도, 신윤복 같은 화가들은 사람들의 일상이나 풍속을 담은 그림을 남기기도 했다.\r\n* 조선의 예술은 신분에 따라 내용과 형식은 달랐지만, 모두 삶을 표현하고 즐기려는 공통된 마음이 담겨있었다. *', NULL, NULL, '', '2025-09-29'),
	(72, 1, '선사시대', '구석기 시대 사람들의 생활', '불의 사용과 이동 생활, 사냥과 채집 중심의 생존 방식.\r<br/>구석기 시대 사람들은 이동하며 살았고, 사냥과 채집으로 먹을 것을 구했습니다.\r<br/>불을 사용해 음식을 익히고 추위를 막았으며, 뗀석기를 사용했습니다.', 'https://www.youtube.com/embed/UEpxN7RPTDU', '', '', '2025-09-30'),
	(73, 1, '공지사항', '줄바꿈', '줄바꿈\r<br/>되는지\r<br/>확인', NULL, NULL, '', '2025-09-30'),
	(74, 1, '선사시대', '줄바꿈', '줄바꿈\r<br/>되는지\r<br/>확인', NULL, NULL, '', '2025-09-30'),
	(75, 1, '선사시대', '신석기 시대와 농경의 시작', '빗살무늬토기, 정착 생활, 가축 사육의 등장.\r<br/>신석기 시대에는 빗살무늬토기를 사용하며 농사를 짓기 시작했습니다.\r<br/>가축을 기르고 정착 생활을 하며, 마을 공동체가 형성되었습니다.', 'https://www.youtube.com/embed/-xQt67LpABY', '', '', '2025-09-30'),
	(76, 1, '선사시대', '청동기 시대와 고인돌', '청동기 사용, 계급 사회 형성, 대표적 무덤 양식인 고인돌.\r<br/>청동기 도구가 사용되면서 권력을 가진 집단이 생겨났습니다. \r<br/>계급이 분화되고, 지배층의 무덤인 고인돌이 만들어졌습니다.', NULL, '', '', '2025-09-30'),
	(77, 1, '선사시대', '한국 최초의 국가, 고조선의 탄생', '단군 신화, 고조선의 성립과 의미.\r<br/>단군 신화로 알려진 고조선은 우리 민족 최초의 국가입니다. \r<br/>고조선은 사회 질서를 세우고 법을 통해 사람들을 다스렸습니다.', 'https://www.youtube.com/embed/tCVa6aUWeiE', '', '', '2025-09-30'),
	(78, 1, '선사시대', '철기 시대와 고조선의 멸망', '철기 사용의 확산, 위만조선과 한 무제의 침입.\r<br/>철기의 사용으로 무기가 발달하면서 국가 간 전쟁이 활발해졌습니다. \r<br/>위만조선은 한나라와의 전쟁에서 패배하며 멸망했습니다.', 'https://www.youtube.com/embed/Y9IqxpsJTl0', '', '', '2025-09-30'),
	(79, 1, '선사시대', '선사시대 유물과 유적의 의미', '뗀석기, 간석기, 빗살무늬토기, 고인돌 등은 선사시대 사람들의 생활을 보여주는 중요한 자료입니다. \r<br/>지금은 박물관이나 유적지에서 볼 수 있습니다.', NULL, '', '', '2025-09-30'),
	(80, 1, '선사시대', '선사시대에서 국가로의 전환', '구석기 → 신석기 → 청동기 → 철기 → 고조선으로 이어지는 발전 과정.\r<br/>구석기 → 신석기 → 청동기 → 철기로 이어지는 흐름 속에서 사람들은 점점 정착하고 농업과 무기를 발전시켰습니다. \r<br/>이 과정을 거쳐 고조선과 같은 국가가 등장했습니다.', NULL, '', '', '2025-09-30'),
	(81, 1, '선사시대', '우리 생활 속에 남아 있는 선사시대 흔적', '고인돌 세계문화유산, 박물관 전시, 현대 문화재 속 선사 흔적 소개.\r<br/>세계문화유산으로 지정된 고인돌, 다양한 박물관 전시, 체험학습 프로그램 등을 통해 오늘날에도 선사시대의 흔적을 접할 수 있습니다.', NULL, '', '', '2025-09-30');

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
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 항목';

-- 테이블 데이터 history.board_comment:~3 rows (대략적) 내보내기
INSERT INTO `board_comment` (`commentId`, `boardId`, `userId`, `comment`, `date`) VALUES
	(40, 39, 1, 'ㅁㅁㅁㅁㅁ', '2025-09-29');

-- 테이블 history.board_study 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_study` (
  `studyId` int NOT NULL AUTO_INCREMENT,
  `boardId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `startAt` timestamp NULL DEFAULT (now()),
  `endAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`studyId`),
  KEY `FK_board_study_board` (`boardId`),
  KEY `FK_board_study_users` (`userId`),
  CONSTRAINT `FK_board_study_board` FOREIGN KEY (`boardId`) REFERENCES `board` (`boardId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_board_study_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='각 사용자 별 특정 학습 게시글의 참여 시작과 완료 시간을 기록한 항목';

-- 테이블 데이터 history.board_study:~37 rows (대략적) 내보내기
INSERT INTO `board_study` (`studyId`, `boardId`, `userId`, `startAt`, `endAt`) VALUES
	(44, 58, 1, '2025-09-29 08:38:27', '2025-09-29 08:38:27'),
	(45, 58, 1, '2025-09-29 08:39:00', '2025-09-29 08:39:00'),
	(46, 62, 1, '2025-09-29 08:40:48', '2025-09-29 08:40:48'),
	(47, 61, 1, '2025-09-29 08:41:03', '2025-09-29 08:41:03'),
	(48, 60, 1, '2025-09-29 08:41:10', '2025-09-29 08:41:10'),
	(49, 59, 1, '2025-09-29 08:41:26', '2025-09-29 08:41:26'),
	(50, 58, 1, '2025-09-29 08:41:33', '2025-09-29 08:41:33'),
	(51, 62, 1, '2025-09-29 08:43:14', '2025-09-29 08:43:14'),
	(52, 63, 1, '2025-09-29 08:43:17', '2025-09-29 08:43:17'),
	(53, 65, 1, '2025-09-29 08:43:21', '2025-09-29 08:43:21'),
	(54, 66, 1, '2025-09-29 08:46:00', '2025-09-29 08:46:00'),
	(55, 62, 1, '2025-09-29 08:46:30', '2025-09-29 08:46:30'),
	(56, 63, 1, '2025-09-29 08:46:37', '2025-09-29 08:46:37'),
	(57, 59, 1, '2025-09-29 08:46:41', '2025-09-29 08:46:41'),
	(58, 58, 1, '2025-09-29 08:46:44', '2025-09-29 08:46:44'),
	(59, 66, 1, '2025-09-29 08:46:48', '2025-09-29 08:46:48'),
	(60, 59, 64, '2025-09-29 08:47:25', '2025-09-29 08:47:25'),
	(61, 58, 64, '2025-09-29 08:47:28', '2025-09-29 08:47:28'),
	(62, 64, 64, '2025-09-29 08:47:33', '2025-09-29 08:47:33'),
	(63, 66, 64, '2025-09-29 08:47:37', '2025-09-29 08:47:37'),
	(68, 72, 1, '2025-09-30 00:05:49', '2025-09-30 00:05:49'),
	(69, 74, 1, '2025-09-30 00:06:34', '2025-09-30 00:06:34'),
	(70, 72, 1, '2025-09-30 00:11:30', '2025-09-30 00:11:30'),
	(71, 72, 1, '2025-09-30 00:11:43', '2025-09-30 00:11:43'),
	(72, 75, 1, '2025-09-30 00:11:51', '2025-09-30 00:11:51'),
	(73, 76, 1, '2025-09-30 00:12:22', '2025-09-30 00:12:22'),
	(74, 77, 1, '2025-09-30 00:12:37', '2025-09-30 00:12:37'),
	(75, 78, 1, '2025-09-30 00:12:54', '2025-09-30 00:12:54'),
	(76, 79, 1, '2025-09-30 00:13:11', '2025-09-30 00:13:11'),
	(77, 80, 1, '2025-09-30 00:13:32', '2025-09-30 00:13:32'),
	(78, 80, 1, '2025-09-30 00:13:41', '2025-09-30 00:13:41'),
	(79, 81, 1, '2025-09-30 00:13:46', '2025-09-30 00:13:46'),
	(80, 74, 1, '2025-09-30 00:14:06', '2025-09-30 00:14:06'),
	(83, 72, 1, '2025-09-30 00:14:20', '2025-09-30 00:14:20'),
	(84, 72, 1, '2025-09-30 00:20:23', '2025-09-30 00:20:23'),
	(85, 72, 1, '2025-09-30 00:20:45', '2025-09-30 00:20:45'),
	(86, 72, 1, '2025-09-30 00:21:06', '2025-09-30 00:21:06'),
	(87, 72, 1, '2025-09-30 00:21:19', '2025-09-30 00:21:19'),
	(88, 72, 1, '2025-09-30 00:21:31', '2025-09-30 00:21:31'),
	(89, 75, 1, '2025-09-30 00:21:47', '2025-09-30 00:21:47'),
	(90, 75, 1, '2025-09-30 00:21:58', '2025-09-30 00:21:58'),
	(91, 75, 1, '2025-09-30 00:22:07', '2025-09-30 00:22:07'),
	(92, 75, 1, '2025-09-30 00:22:17', '2025-09-30 00:22:17'),
	(93, 72, 1, '2025-09-30 00:22:23', '2025-09-30 00:22:23'),
	(94, 72, 1, '2025-09-30 00:22:43', '2025-09-30 00:22:43'),
	(95, 75, 1, '2025-09-30 00:22:47', '2025-09-30 00:22:47'),
	(96, 76, 1, '2025-09-30 00:22:58', '2025-09-30 00:22:58'),
	(97, 76, 1, '2025-09-30 00:23:08', '2025-09-30 00:23:08'),
	(98, 77, 1, '2025-09-30 00:23:12', '2025-09-30 00:23:12'),
	(99, 77, 1, '2025-09-30 00:23:20', '2025-09-30 00:23:20'),
	(100, 78, 1, '2025-09-30 00:23:25', '2025-09-30 00:23:25'),
	(101, 79, 1, '2025-09-30 00:23:32', '2025-09-30 00:23:32'),
	(102, 80, 1, '2025-09-30 00:23:42', '2025-09-30 00:23:42'),
	(103, 81, 1, '2025-09-30 00:23:50', '2025-09-30 00:23:50'),
	(104, 81, 1, '2025-09-30 00:23:56', '2025-09-30 00:23:56'),
	(105, 80, 1, '2025-09-30 00:23:58', '2025-09-30 00:23:58');

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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='답글에 대한 항목';

-- 테이블 데이터 history.comment_reply:~1 rows (대략적) 내보내기

-- 프로시저 history.CreateQuiz 구조 내보내기
DELIMITER //
CREATE PROCEDURE `CreateQuiz`()
    COMMENT '퀴즈생성 루틴'
BEGIN
    DECLARE v_quizCategoryId INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;

	 -- 1. 관리자가 n시대 제n회 퀴즈문항 카테고리 생성
    INSERT INTO quiz_category (userId, quizType, quizListName)
    VALUES (1, '조선시대', '제1회 퀴즈문항');
    
    -- 방금 생성된 카테고리 ID 가져오기
    SET v_quizCategoryId = LAST_INSERT_ID();
    
    -- 2. n시대 관련 퀴즈 문항들 생성 (최소 3개)
    
    -- 퀴즈 1
    INSERT INTO quiz (quizCategoryId, imgUrl, question, item1, item2, item3, item4, answer, quizScore) 
    VALUES (
        v_quizCategoryId, 
        NULL, 
        '조선을 건국한 왕의 본명은?', 
        '이성계', 
        '이방원', 
        '이성은', 
        '이승만',
        1,
        10
    );
    
    -- 퀴즈 2
    INSERT INTO quiz (quizCategoryId, imgUrl, question, item1, item2, item3, item4, answer, quizScore) 
    VALUES (
        v_quizCategoryId, 
        NULL, 
        '조선의 수도는 어디였습니까?', 
        '개경', 
        '한양', 
        '경주', 
        '평양',
        2,
        10
    );
    
    -- 퀴즈 3
    INSERT INTO quiz (quizCategoryId, imgUrl, question, item1, item2, item3, item4, answer, quizScore) 
    VALUES (
        v_quizCategoryId, 
        NULL, 
        '세종대왕의 업적에 해당되는 것은?', 
        '훈민정음', 
        '목민심서', 
        '팔만대장경', 
        '난중일기',
        1,
        10
    );
    

    COMMIT;
    
    -- 결과 확인용 SELECT
    SELECT 
        v_quizCategoryId as createdCategoryId,
        '퀴즈가 성공적으로 생성되었습니다.' as message,
        (SELECT COUNT(*) FROM quiz WHERE quizCategoryId = v_quizCategoryId) as totalQuizCount;
        
END//
DELIMITER ;

-- 테이블 history.points 구조 내보내기
CREATE TABLE IF NOT EXISTS `points` (
  `pointId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceId` int DEFAULT NULL,
  `itemId` int DEFAULT NULL,
  `scoreId` int DEFAULT NULL,
  `pointChange` int DEFAULT NULL,
  `totalPoint` int DEFAULT NULL,
  `createAt` datetime DEFAULT (now()),
  PRIMARY KEY (`pointId`) USING BTREE,
  KEY `FK_point_transactions_point_shop` (`itemId`),
  KEY `FK_point_transactions_user` (`userId`),
  KEY `FK_point_transactions_user_attendance` (`attendanceId`),
  KEY `FK_point_transactions_quiz_score` (`scoreId`) USING BTREE,
  CONSTRAINT `FK_points_point_shop_item` FOREIGN KEY (`itemId`) REFERENCES `point_shop` (`itemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_quiz_score` FOREIGN KEY (`scoreId`) REFERENCES `quiz_score` (`scoreId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_points_user_attendance` FOREIGN KEY (`attendanceId`) REFERENCES `user_attendance` (`attendanceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트 내역 기술';

-- 테이블 데이터 history.points:~18 rows (대략적) 내보내기
INSERT INTO `points` (`pointId`, `userId`, `attendanceId`, `itemId`, `scoreId`, `pointChange`, `totalPoint`, `createAt`) VALUES
	(35, 41, 951, NULL, NULL, 10, 10, '2025-09-25 11:56:58'),
	(37, 44, 953, NULL, NULL, 10, 10, '2025-09-25 11:56:58'),
	(46, 44, 958, NULL, NULL, 10, 20, '2025-09-25 11:56:58'),
	(47, 41, 959, NULL, NULL, 10, 20, '2025-09-25 11:56:58'),
	(59, 41, 966, NULL, NULL, 10, 30, '2025-09-25 11:56:58'),
	(67, 41, 970, NULL, NULL, 10, 40, '2025-09-25 11:56:58'),
	(72, 41, 971, NULL, NULL, 10, 50, '2025-09-25 11:56:58'),
	(90, 41, 985, NULL, NULL, 10, 60, '2025-09-25 11:56:58'),
	(100, 41, 988, NULL, NULL, 10, 70, '2025-09-25 11:56:58'),
	(135, 41, 990, NULL, NULL, 10, 80, '2025-09-26 10:08:36'),
	(136, 41, NULL, 28, NULL, -20, 60, '2025-09-26 10:08:51'),
	(137, 41, NULL, 28, NULL, 20, 80, '2025-09-26 10:08:59'),
	(138, 41, NULL, 28, NULL, -20, 60, '2025-09-26 10:09:13'),
	(139, 41, NULL, 28, NULL, 0, 60, '2025-09-26 10:09:26'),
	(140, 41, NULL, 28, NULL, 0, 60, '2025-09-26 10:09:36'),
	(141, 44, NULL, 28, NULL, -20, 0, '2025-09-26 10:19:11'),
	(142, 64, 991, NULL, NULL, 10, 10, '2025-09-26 10:22:32'),
	(143, 64, NULL, 28, NULL, -10, 0, '2025-09-26 10:23:54'),
	(144, 64, NULL, 28, NULL, 0, 0, '2025-09-26 12:21:12'),
	(145, 1, 992, NULL, NULL, 10, 10, '2025-09-26 15:57:16'),
	(146, 64, 993, NULL, NULL, 10, 10, '2025-09-29 08:42:00'),
	(147, 64, NULL, 28, NULL, -10, 0, '2025-09-29 08:42:17'),
	(148, 41, 994, NULL, NULL, 10, 70, '2025-09-29 10:54:44'),
	(149, 64, 995, NULL, NULL, 10, 10, '2025-09-30 09:03:20');

-- 테이블 history.point_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `point_shop` (
  `itemId` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `imgUrl` varchar(255) DEFAULT NULL,
  `itemName` varchar(50) DEFAULT NULL,
  `cost` int DEFAULT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트샵 항목 기술';

-- 테이블 데이터 history.point_shop:~25 rows (대략적) 내보내기
INSERT INTO `point_shop` (`itemId`, `category`, `brand`, `imgUrl`, `itemName`, `cost`) VALUES
	(28, 'drink', 'compose', '/img/c5.png', '컴포즈 커피 5천원권', 10),
	(29, 'drink', 'compose', '/img/c10.png', '컴포즈 커피 1만원권', 40),
	(30, 'drink', 'compose', '/img/c20.png', '컴포즈 커피 2만원권', 600),
	(31, 'drink', 'compose', '/img/c30.png', '컴포즈 커피 3만원권', 800),
	(32, 'drink', 'compose', '/img/c50.png', '컴포즈 커피 5만원권', 1000),
	(33, 'drink', 'mega', '/img/m5.png', '메가커피 5천원권', 200),
	(34, 'drink', 'mega', '/img/m10.png', '메가커피 1만원권', 400),
	(35, 'drink', 'mega', '/img/m20.png', '메가커피 2만원권', 600),
	(36, 'drink', 'mega', '/img/m30.png', '메가커피 3만원권', 800),
	(37, 'drink', 'mega', '/img/m50.png', '메가커피 5만원권', 1000),
	(38, 'drink', 'starbucks', '/img/s20.png', '스타벅스 2만원권', 600),
	(39, 'drink', 'starbucks', '/img/s30.png', '스타벅스 3만원권', 800),
	(40, 'drink', 'starbucks', '/img/s50.png', '스타벅스 5만원권', 1000),
	(41, 'C-store', 'cu', '/images/store/3a414f80-ec5b-465e-92d3-0e5dd11d2b71.png', 'CGV 1만원권', 400),
	(42, 'C-store', 'gs25', '/images/store/0caa5cb1-c824-4012-b92d-03f9626886b7.png', 'GS25 1만원권', 400),
	(43, 'C-store', '7-Eleven', '/images/store/38d7318d-b4b7-476f-a9c7-1e1c85aecde2.png', '세븐일레븐 1만원권', 400),
	(44, 'C-store', 'emart24', '/images/store/53169095-3c56-4bd1-a909-4e8b6c47c409.png', '이마트24 1만원권', 400),
	(45, 'drink', 'ediya', '/images/store/92241e44-691e-4c86-9f68-0c01e4c70246.png', '이디야 1만원권', 400),
	(46, 'drink', 'twosome', '/images/store/ee41d1f2-3e2d-4a91-8014-a23154216ef8.png', '투썸플레이스 2만원권', 600),
	(47, 'movie', 'cgv', '/images/store/c23242e9-521e-4ec2-ba9e-b930ba6e1372.png', 'CGV 5만원권', 1000),
	(48, 'movie', 'Megabox', '/images/store/7ff32eba-c85b-477f-9031-68ad42febffe.png', '메가박스 5만원권', 1000),
	(49, 'movie', 'Lotte', '/images/store/7e5fde10-d44b-488e-b4e3-965774c2fa67.png', '롯데시네마 영화관람권', 450),
	(50, 'gift-card', 'Kyobo', '/images/store/ea99d9bc-9166-42ef-ae1d-b4f03e2d587e.png', '교보문고 전자책 상품권 1만원권', 400),
	(51, 'gift-card', 'culture', '/images/store/1e80eab5-c003-492c-ac57-0f2e105bc24d.png', '컬쳐랜드 문화상품권 1만원권', 400),
	(52, 'gift-card', 'Booknlife', '/images/store/4ca4c3d5-1867-4541-b167-24a9f508fbfc.png', '북앤라이프 도서문화상품권 1만원권', 400);

-- 테이블 history.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quizId` int NOT NULL AUTO_INCREMENT,
  `quizCategoryId` int DEFAULT NULL,
  `quizNumber` int DEFAULT NULL,
  `imgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item1` varchar(100) DEFAULT NULL,
  `item2` varchar(100) DEFAULT NULL,
  `item3` varchar(100) DEFAULT NULL,
  `item4` varchar(100) DEFAULT NULL,
  `answer` int DEFAULT NULL,
  `quizScore` int DEFAULT NULL,
  PRIMARY KEY (`quizId`) USING BTREE,
  KEY `FK_quiz_quiz_category` (`quizCategoryId`),
  CONSTRAINT `FK_quiz_quiz_category` FOREIGN KEY (`quizCategoryId`) REFERENCES `quiz_category` (`quizCategoryId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 각 문항에 대한 항목';

-- 테이블 데이터 history.quiz:~3 rows (대략적) 내보내기
INSERT INTO `quiz` (`quizId`, `quizCategoryId`, `quizNumber`, `imgUrl`, `question`, `item1`, `item2`, `item3`, `item4`, `answer`, `quizScore`) VALUES
	(10, 7, NULL, NULL, '조선을 건국한 왕의 본명은?', '이성계', '이방원', '이성은', '이승만', 1, 10),
	(11, 7, NULL, NULL, '조선의 수도는 어디였습니까?', '개경', '한양', '경주', '평양', 2, 10),
	(12, 7, NULL, NULL, '세종대왕의 업적에 해당되는 것은?', '훈민정음', '목민심서', '팔만대장경', '난중일기', 1, 10);

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 응시에 대한 항목';

-- 테이블 데이터 history.quiz_attempt:~0 rows (대략적) 내보내기

-- 테이블 history.quiz_category 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_category` (
  `quizCategoryId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `quizType` varchar(50) DEFAULT NULL,
  `quizListName` varchar(50) DEFAULT NULL,
  `createAt` datetime DEFAULT (now()),
  PRIMARY KEY (`quizCategoryId`),
  KEY `FK_quiz_category_users` (`userId`),
  CONSTRAINT `FK_quiz_category_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 카테고리 분류에 관한 항목';

-- 테이블 데이터 history.quiz_category:~1 rows (대략적) 내보내기
INSERT INTO `quiz_category` (`quizCategoryId`, `userId`, `quizType`, `quizListName`, `createAt`) VALUES
	(7, 1, '조선시대', '제1회 퀴즈문항', '2025-09-30 09:20:50');

-- 테이블 history.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `scoreId` int NOT NULL AUTO_INCREMENT,
  `quizCategoryId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `totalScore` int DEFAULT NULL,
  `earnedPoint` int DEFAULT NULL,
  `pass` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`scoreId`),
  KEY `FK_quiz_score_quiz` (`quizCategoryId`) USING BTREE,
  KEY `FK_quiz_score_users` (`userId`),
  CONSTRAINT `FK_quiz_score_quiz_category` FOREIGN KEY (`quizCategoryId`) REFERENCES `quiz_category` (`quizCategoryId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 채점 및 포인트 환산에 관한 항목';

-- 테이블 데이터 history.quiz_score:~0 rows (대략적) 내보내기

-- 테이블 history.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='관리자/일반유저';

-- 테이블 데이터 history.users:~5 rows (대략적) 내보내기
INSERT INTO `users` (`userId`, `userType`, `name`, `password`, `email`) VALUES
	(1, '관리자', '관리자', '111111', 'admin@lms.com'),
	(41, '일반유저', '박도재', '111111', 'qkrehwo@gmail.com'),
	(44, '일반유저', '김철수', '111111', 'aaa'),
	(45, '일반유저', '이영희', '111111', 'bbb'),
	(64, '일반유저', '이슬', '000000', 'ccc@naver.com');

-- 테이블 history.user_attendance 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_attendance` (
  `attendanceId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `attendanceDate` datetime DEFAULT (now()),
  `pointAdd` int DEFAULT NULL,
  PRIMARY KEY (`attendanceId`) USING BTREE,
  KEY `FK_attendance_user` (`userId`) USING BTREE,
  CONSTRAINT `FK_user_attendance_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=996 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='출석기록';

-- 테이블 데이터 history.user_attendance:~12 rows (대략적) 내보내기
INSERT INTO `user_attendance` (`attendanceId`, `userId`, `attendanceDate`, `pointAdd`) VALUES
	(951, 41, '2025-09-17 16:52:56', 10),
	(953, 44, '2025-09-17 17:13:42', 10),
	(958, 44, '2025-09-18 11:40:28', 10),
	(959, 41, '2025-09-18 15:45:15', 10),
	(966, 41, '2025-09-19 10:23:01', 10),
	(970, 41, '2025-09-22 16:24:19', 10),
	(971, 41, '2025-09-23 11:58:25', 10),
	(985, 41, '2025-09-24 13:59:42', 10),
	(988, 41, '2025-09-25 09:49:59', 10),
	(990, 41, '2025-09-26 10:08:36', 10),
	(991, 64, '2025-09-26 10:22:32', 10),
	(992, 1, '2025-09-26 15:57:16', 10),
	(993, 64, '2025-09-29 08:41:54', 10),
	(994, 41, '2025-09-29 10:54:38', 10),
	(995, 64, '2025-09-30 09:03:20', 10);

-- 뷰 history.v_quiz_secure 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_quiz_secure` (
	`quizCategoryId` INT NULL,
	`quizNumber` INT NULL,
	`question` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`item1` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`item2` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`item3` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`item4` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci'
);

-- 뷰 history.v_users_secure 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_users_secure` (
	`userType` VARCHAR(1) NULL COLLATE 'utf8mb4_0900_ai_ci',
	`maskedName` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`maskedEmail` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci'
);

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

-- 트리거 history.tr_quiz_attempt_before_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `tr_quiz_attempt_before_insert` BEFORE INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE quiz_answer INT;
    DECLARE quiz_score_value INT;
    
    -- quiz 테이블에서 해당 quizId의 answer를 조회
    SELECT answer
    INTO quiz_answer
    FROM quiz 
    WHERE quizId = NEW.quizId;
    
	-- selected와 answer가 같으면 quizScore를, 다르면 0점을 earnedScore에 설정
    IF NEW.selected = quiz_answer THEN
        SET NEW.earnedScore = quiz_score_value;
        SET NEW.earnedScore = 0;
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
    DECLARE pass_status VARCHAR(50) DEFAULT '미수료';
    DECLARE score_exists INT DEFAULT 0;
    
    -- 해당 사용자와 퀴즈 카테고리의 총점 계산
    SELECT COALESCE(SUM(earnedScore), 0) INTO total_score
    FROM quiz_attempt
    WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    
    -- 획득 포인트 계산 (총점의 1배)
    SET earned_point = total_score * 1;
    
    -- 수료 여부 판단: totalScore가 60 이상이면 '수료', 미만이면 '미수료'
    IF total_score >= 60 THEN
        SET pass_status = '수료';
    ELSE
        SET pass_status = '미수료';
    END IF;
    
    -- quiz_score 테이블에 해당 레코드가 있는지 확인
    SELECT COUNT(*) INTO score_exists
    FROM quiz_score
    WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    
    -- 레코드가 존재하면 업데이트, 없으면 삽입
    IF score_exists > 0 THEN
        UPDATE quiz_score 
        SET totalScore = total_score, 
            earnedPoint = earned_point,
            pass = pass_status
        WHERE userId = NEW.userId AND quizCategoryId = NEW.quizCategoryId;
    ELSE
        INSERT INTO quiz_score (quizCategoryId, userId, totalScore, earnedPoint, pass)
        VALUES (NEW.quizCategoryId, NEW.userId, total_score, earned_point, pass_status);
    END IF;
    
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_quiz_secure`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_quiz_secure` AS select `quiz`.`quizCategoryId` AS `quizCategoryId`,`quiz`.`quizNumber` AS `quizNumber`,`quiz`.`question` AS `question`,`quiz`.`item1` AS `item1`,`quiz`.`item2` AS `item2`,`quiz`.`item3` AS `item3`,`quiz`.`item4` AS `item4` from `quiz` WITH CASCADED CHECK OPTION
;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_users_secure`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_users_secure` AS select `users`.`userType` AS `userType`,(case when ((`users`.`name` is null) or (`users`.`name` = '')) then NULL when (char_length(`users`.`name`) = 1) then `users`.`name` when (char_length(`users`.`name`) = 2) then concat(left(`users`.`name`,1),'*') else concat(left(`users`.`name`,1),repeat('*',(char_length(`users`.`name`) - 1))) end) AS `maskedName`,(case when ((`users`.`email` is null) or (`users`.`email` = '')) then NULL when (not((`users`.`email` like '%@%'))) then concat(left(`users`.`email`,2),repeat('*',(char_length(`users`.`email`) - 2))) else concat(left(`users`.`email`,2),repeat('*',(char_length(substring_index(`users`.`email`,'@',1)) - 2)),right(`users`.`email`,(char_length(`users`.`email`) - char_length(substring_index(`users`.`email`,'@',1))))) end) AS `maskedEmail` from `users` WITH CASCADED CHECK OPTION
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

