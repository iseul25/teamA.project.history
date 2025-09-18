// 로그인
async function checkLogin() {
  try {
    const response = await fetch("/api/check-login", {
      credentials: "include"
    });
    const data = await response.json();

    const userDiv = document.getElementById("user");
    const usernameLink = document.getElementById("userName-link");

    if (data.isLoggedIn) {
      if (data.role === "admin") {
        // 관리자
        usernameLink.textContent = "관리자";
        usernameLink.href = "/admin/dashboard.html";

        // 헤더 변경
        document.getElementById("last-header").textContent = "작업";

        // 관리자용 버튼 보이기
        document.getElementById("quiz-btn-box").style.display = "block";
      } else {
        // 일반 유저
        usernameLink.textContent = data.username;
        usernameLink.href = "/mypage/mypage.html";

        // 퀴즈출제 버튼 숨기기
        document.getElementById("quiz-btn-box").style.display = "none";
      }
      userDiv.style.display = "block";
    } else {
      userDiv.style.display = "none";
      document.getElementById("quiz-btn-box").style.display = "none";
    }
  } catch (err) {
    console.error("로그인 체크 에러:", err);
  }
}

// 페이지 로드시 실행
window.onload = () => {
  // 강제로 관리자 화면 보기
  // document.getElementById("last-header").textContent = "작업";
  // document.getElementById("quiz-btn-box").style.display = "block";
  
  // 위에 주석처리 하고 밑에 주석 풀면 유저 화면
  checkLogin();
  renderTable(currentPage);
}

// 사이드바 메인메뉴 클릭 시 서브메뉴 토글
document.querySelectorAll(".mainMenu").forEach(menu => {
  menu.addEventListener("click", () => {
    const parent = menu.parentElement; // li.submenuBox
    const submenu = parent.querySelector(".submenu");

    if (submenu.classList.contains("show")) {
      // 이미 열려있는 경우 → 클릭한 메뉴만 닫기
      submenu.style.maxHeight = submenu.scrollHeight + "px"; // 현재 높이로 먼저 세팅
      submenu.offsetHeight; // 강제로 리플로우 → transition 인식
      submenu.classList.remove("show");
      submenu.style.maxHeight = "0px"; // 0으로 애니메이션
      parent.classList.remove("open"); // 화살표 회전 해제
    } else {
      // 닫혀 있으면 → 그 메뉴만 열기
      submenu.classList.add("show");
      submenu.style.maxHeight = submenu.scrollHeight + "px";
      parent.classList.add("open"); // 화살표 회전
    }
  });
});

// 퀴즈 홈에서 버튼 눌렀을 때 연결
// URL에서 category 값 읽기
const params = new URLSearchParams(window.location.search);
const category = params.get("category") || "전체";
document.querySelector(".list-title").textContent = `${category}`;

// 제목에 표시해보기 (테스트용)
// document.body.insertAdjacentHTML("afterbegin", `<h2>${category} 퀴즈 목록</h2>`);

// 나중에 이 category 값으로 백엔드에 요청
// fetch(`/api/quizzes?category=${category}`)

// 테스트용 더미데이터 (13개)
const dummyData = [
  { id: 1, title: "선사시대 생활도구 퀴즈", date: "2025-01-01", completed: true },
  { id: 2, title: "구석기와 신석기 비교", date: "2025-01-02", completed: false },
  { id: 3, title: "고조선 8조법 퀴즈", date: "2025-01-03", completed: true },
  { id: 4, title: "여러 나라 발전", date: "2025-01-04", completed: false },
  { id: 5, title: "삼국시대 전쟁", date: "2025-01-05", completed: false },
  { id: 6, title: "가야 문화", date: "2025-01-06", completed: true },
  { id: 7, title: "남북극시대 퀴즈1", date: "2025-01-07", completed: false },
  { id: 8, title: "남북극시대 퀴즈2", date: "2025-01-08", completed: true },
  { id: 9, title: "고려시대 건국", date: "2025-01-09", completed: false },
  { id: 10, title: "고려시대 문화", date: "2025-01-10", completed: true },
  { id: 11, title: "조선 건국", date: "2025-01-11", completed: false },
  { id: 12, title: "근대 개항기 퀴즈", date: "2025-01-12", completed: true },
  { id: 13, title: "현대사 민주화 운동", date: "2025-01-13", completed: false }
];

// 페이징 변수
const pageSize = 10;
let currentPage = 1;

// 테이블 렌더링
function renderTable(page = 1) {
  const tbody = document.getElementById("list-body");
  tbody.innerHTML = "";

  // 선택된 카테고리 값에 맞게 필터링
  const filteredData = category === "전체"
    ? dummyData
    : dummyData.filter(q => q.title.includes(category));

  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  const pageData = filteredData.slice(start, end);

  pageData.forEach((quiz) => {
    const tr = document.createElement("tr");

    // 관리자 여부에 따라 다르게 표시
    if (document.getElementById("last-header").textContent === "작업") {
      tr.innerHTML = `
        <td>${quiz.id}</td>
        <td>${quiz.title}</td>
        <td>관리자</td>
        <td>
          <button class="editBtn" data-id="${quiz.id}">수정</button>
          <button class="deleteBtn" data-id="${quiz.id}">삭제</button>
        </td>
      `;
    } else {
      tr.innerHTML = `
        <td>${quiz.id}</td>
        <td>${quiz.title}</td>
        <td>관리자</td>
        <td>${quiz.completed ? "수료" : "미수료"}</td>
      `;
    }
    tbody.appendChild(tr);
  });

  // 빈 행 맞추기
  const emptyRows = pageSize - pageData.length;
  for (let i = 0; i < emptyRows; i++) {
    const tr = document.createElement("tr");
    if (document.getElementById("last-header").textContent === "작업") {
      tr.innerHTML = `<td>&nbsp;</td><td></td><td></td><td></td>`;
    } else {
      tr.innerHTML = `<td>&nbsp;</td><td></td><td></td><td></td>`;
    }
    tbody.appendChild(tr);
  }

  renderPagination(dummyData.length, page);
}

// 페이징 버튼
function renderPagination(totalItems, currentPage) {
  const paginationDiv = document.getElementById("pagination");
  paginationDiv.innerHTML = "";

  const totalPages = Math.ceil(totalItems / pageSize);

  // ◀ 이전 버튼
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = currentPage === 1;
  prevBtn.addEventListener("click", () => renderTable(currentPage - 1)); // renderProducts → renderTable
  paginationDiv.appendChild(prevBtn);

  // 페이지 번호 버튼
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPage) btn.classList.add("active");
    btn.addEventListener("click", () => renderTable(i)); // renderProducts → renderTable
    paginationDiv.appendChild(btn);
  }

  // ▶ 다음 버튼
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.addEventListener("click", () => renderTable(currentPage + 1)); // renderProducts → renderTable
  paginationDiv.appendChild(nextBtn);
}

// 사이드바에서 카테고리 클릭 → 퀴즈 목록 페이지로 이동
document.querySelectorAll(".quiz-link").forEach(link => {
  link.addEventListener("click", (e) => {
    e.preventDefault();
    const category = link.dataset.category;
    // 퀴즈 목록 페이지 주소에 맞게 수정 (파일명 맞추기)
    window.location.href = `/quiz/quizList.html?category=${encodeURIComponent(category)}`;
  });
});
