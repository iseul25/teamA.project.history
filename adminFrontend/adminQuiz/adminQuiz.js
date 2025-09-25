// 관리자 로그인
async function checkLogin() {
  try {
    const response = await fetch("/api/check-login", {
      credentials: "include"
    });
    const data = await response.json();

    const userDiv = document.getElementById("user");
    const usernameLink = document.getElementById("userName-link");
    // id 수정됨

    if (data.isLoggedIn) {
      // 관리자인지 확인
      if (data.role === "admin") {
        usernameLink.textContent = data.username + "관리자";
        usernameLink.href = "/admin/dashboard.html";
        // 관리자 페이지
      } else {
        usernameLink.textContent = data.username;
        usernameLink.href = "/mypage/mypage.html";
        // 일반 유저 페이지
      }
      userDiv.style.display = "block";
    } else {
      userDiv.style.display = "none";
    }
  } catch (err) {
    console.error("로그인 체크 에러:", err);
  }
}

// quizList.js 불러오기 (DB 연동 전엔 dummyData로 사용) // 프론트에서 실패함
// import { quizList, quizStatusData } from "../../quiz/js/quizList.js";

// 퀴즈 목록 더미데이터 가져옴
const quizList = [
  { id: 1, title: "선사시대 생활도구 퀴즈", category: "선사시대", date: "2025-01-01", completed: true },
  { id: 2, title: "구석기와 신석기 비교", category: "선사시대", date: "2025-01-02", completed: false },
  { id: 3, title: "고조선 8조법 퀴즈", category: "고조선과 여러 나라", date: "2025-01-03", completed: true },
  { id: 4, title: "여러 나라 발전", category: "고조선과 여러 나라", date: "2025-01-04", completed: false },
  { id: 5, title: "삼국시대 전쟁", category: "삼국과 가야", date: "2025-01-05", completed: false },
  { id: 6, title: "가야 문화", category: "삼국과 가야", date: "2025-01-06", completed: true },
  { id: 7, title: "남북극시대 퀴즈1", category: "남북극시대", date: "2025-01-07", completed: false },
  { id: 8, title: "남북극시대 퀴즈2", category: "남북극시대", date: "2025-01-08", completed: true },
  { id: 9, title: "고려시대 건국", category: "고려시대", date: "2025-01-09", completed: false },
  { id: 10, title: "고려시대 문화", category: "고려시대", date: "2025-01-10", completed: true },
  { id: 11, title: "조선 건국", category: "조선시대", date: "2025-01-11", completed: false },
  { id: 12, title: "근대 개항기 퀴즈", category: "근대", date: "2025-01-12", completed: true },
  { id: 13, title: "현대사 민주화 운동", category: "현대", date: "2025-01-13", completed: false }
];

// 더미 퀴즈 현황 데이터 (퀴즈별 사용자 점수)
const quizStatusData = {
  1: [
    { username: "홍길동", score: 100 },
    { username: "신짱구", score: 60 },
    { username: "유리", score: 70 },
    { username: "훈이", score: 50 },
    { username: "수지", score: 80 },
    { username: "김철수", score: 100 },
    { username: "맹구", score: 90 },
    { username: "나미리", score: 80 },
    { username: "채성아", score: 90 },
    { username: "신형만", score: 60 },
    { username: "봉미선", score: 90 },
    { username: "흑곰", score: 90 }
  ],
  2: [
    { username: "김철수", score: 100 },
    { username: "맹구", score: 90 }
  ]
  // 필요한 만큼 추가
};

const tbody = document.getElementById("quizList-body");
const paginationDiv = document.getElementById("quizList-pagination");

const categoryHeader = document.getElementById("categoryHeader");
const categoryMenu = document.getElementById("categoryMenu");
const arrow = categoryHeader.querySelector(".arrow");

// 퀴즈 목록 관련 페이지 변수
let listCurrentPage = 1;
const pageSize = 10;
let currentCategory = "전체";

// 현황 모달 관련 변수
const STATUS_PAGE_SIZE = 10;
let currentStatusPage = 1;
let currentStatusList = [];

// 목록 페이지 렌더링
function renderPage(page) {
  listCurrentPage = page;
  renderQuizList();
}

// 퀴즈 리스트 렌더링
function renderQuizList() {
  tbody.innerHTML = "";

  // 카테고리 필터 적용
  const filteredData =
    currentCategory === "전체"
      ? quizList
      : quizList.filter(q => q.category === currentCategory);

  const totalItems = filteredData.length;
  const start = (listCurrentPage - 1) * pageSize;
  const end = start + pageSize;
  const pageData = filteredData.slice(start, end);

  // 실제 데이터 행
  pageData.forEach((quiz, index) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${start + index + 1}</td>
      <td>${quiz.category}</td>
      <td><a href="/quiz/quizSolve.html?id=${quiz.id}">${quiz.title}</a></td>
      <td>${quiz.date}</td>
      <td>
        <button class="status-btn" data-id="${quiz.id}">현황</button>
        <button class="delete-btn" data-id="${quiz.id}">삭제</button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // 빈 행 채우기 (10행 유지)
  const emptyRows = pageSize - pageData.length;
  for (let i = 0; i < emptyRows; i++) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>&nbsp;</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    `;
    tbody.appendChild(tr);
  }

  // 목록 페이징 버튼
  renderPagination(totalItems);
}

// 목록 페이징 버튼 렌더링
function renderPagination(totalItems) {
  paginationDiv.innerHTML = "";
  const pageCount = Math.ceil(totalItems / pageSize);

  // 이전
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = listCurrentPage === 1;
  prevBtn.addEventListener("click", () => renderPage(listCurrentPage - 1));
  paginationDiv.appendChild(prevBtn);

  // 번호 버튼
  for (let i = 1; i <= pageCount; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === listCurrentPage) btn.classList.add("active");
    btn.addEventListener("click", () => renderPage(i));
    paginationDiv.appendChild(btn);
  }

  // 다음
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = listCurrentPage === pageCount;
  nextBtn.addEventListener("click", () => renderPage(listCurrentPage + 1));
  paginationDiv.appendChild(nextBtn);
}

// 카테고리 드롭다운 이벤트
document.querySelectorAll("#categoryMenu li").forEach(item => {
  item.addEventListener("click", () => {
    currentCategory = item.dataset.category;
    listCurrentPage = 1;
    renderQuizList();
  });
});

// 드롭다운 열기/닫기
categoryHeader.addEventListener("click", (e) => {
  if (e.target.closest("#categoryMenu")) return; 
  categoryMenu.classList.toggle("show");
  arrow.classList.toggle("up");
});

// 카테고리 메뉴 클릭
categoryMenu.querySelectorAll("li").forEach(item => {
  item.addEventListener("click", (e) => {
    e.stopPropagation(); // 버블링 차단
    currentCategory = item.dataset.category;

    categoryMenu.classList.remove("show");
    arrow.classList.remove("up");

    listCurrentPage = 1;
    renderQuizList();
  });
});

// 바깥 클릭 시 닫기
window.addEventListener("click", (e) => {
  if (!categoryHeader.contains(e.target)) {
    categoryMenu.classList.remove("show");
    arrow.classList.remove("up");
  }
});

// 목록 삭제 버튼 이벤트
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("delete-btn")) {
    const quizId = e.target.dataset.id;
    if (confirm("정말 삭제하시겠습니까?")) {
      const index = quizList.findIndex(q => q.id == quizId);
      if (index !== -1) {
        quizList.splice(index, 1);
        renderQuizList();
      }
    }
  }
});

// 현황 모달 처리
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("status-btn")) {
    const id = parseInt(e.target.dataset.id, 10);
    currentStatusList = quizStatusData[id] || [];
    currentStatusPage = 1;
    renderStatusTable(currentStatusPage);
    document.getElementById("status-modal").style.display = "flex";
  }
});

// 현황 모달 테이블
function renderStatusTable(page = 1) {
  const statusBody = document.getElementById("status-body");
  statusBody.innerHTML = "";

  const start = (page - 1) * STATUS_PAGE_SIZE;
  const end = start + STATUS_PAGE_SIZE;
  const pageData = currentStatusList.slice(start, end);

  if (pageData.length === 0) {
    statusBody.innerHTML = `<tr><td></td><td></td><td></td><td></td></tr>`;
  } else {
    pageData.forEach((s, index) => {
      const passed = s.score >= 60 ? "수료" : "미수료";
      const row = `
        <tr>
          <td>${start + index + 1}</td>
          <td>${s.username}</td>
          <td>${s.score}</td>
          <td>${passed}</td>
        </tr>
      `;
      statusBody.insertAdjacentHTML("beforeend", row);
    });
  }

  // 빈 행 맞추기 (현황 모달)
  const emptyRows = STATUS_PAGE_SIZE - pageData.length;
  for (let i = 0; i < emptyRows; i++) {
    statusBody.insertAdjacentHTML("beforeend", `
      <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
    `);
  }

  renderStatusPagination(currentStatusList.length, page);
}


// 현황 모달 페이징
function renderStatusPagination(totalItems, currentPage) {
  const paginationDiv = document.getElementById("status-pagination");
  paginationDiv.innerHTML = "";

  const totalPages = Math.ceil(totalItems / STATUS_PAGE_SIZE);

  // 이전
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = currentPage === 1;
   prevBtn.addEventListener("click", () => {
    currentStatusPage = currentPage - 1;
    renderStatusTable(currentStatusPage);
  });
  paginationDiv.appendChild(prevBtn);

  // 번호 버튼
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPage) btn.classList.add("active");
    btn.addEventListener("click", () => {
      currentStatusPage = i;
      renderStatusTable(currentStatusPage);
    });
    paginationDiv.appendChild(btn);
  }

  // 다음
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.addEventListener("click", () => {
    currentStatusPage = currentPage + 1;
    renderStatusTable(currentStatusPage);
  });
  paginationDiv.appendChild(nextBtn);
}

// 현황 모달 닫기
document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("status-modal");
  const closeBtn = modal.querySelector(".closeBtn");

  closeBtn.addEventListener("click", () => {
    modal.style.display = "none";
  });

  window.addEventListener("click", (e) => {
    if (e.target === modal) modal.style.display = "none";
  });
});

// 페이지 로드 초기 실행
window.onload = () => {
  // checkLogin();
  renderQuizList();
};
