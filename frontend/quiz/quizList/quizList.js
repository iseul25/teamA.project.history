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
window.onload = async () => {
  // 강제로 관리자 화면 보기
  document.getElementById("last-header").textContent = "작업";
  document.getElementById("quiz-btn-box").style.display = "block";

  // 위에 주석처리 하고 밑에 주석 풀면 유저 화면
  await checkLogin();
  renderTable(listCurrentPage);
  // renderQuizList()
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

// 화면 제목에 표시할 값 (접미어 제거)
const cleanCategory = category.replace("-학습", "").replace("-퀴즈", "");
document.addEventListener("DOMContentLoaded", () => {
  const listTitleEl = document.querySelector(".list-title");
  if (listTitleEl) {
    listTitleEl.textContent =
      category === "전체" ? "전체 퀴즈 목록" : `${cleanCategory}`;
  }
});

// 제목에 표시해보기 (테스트용)
// document.body.insertAdjacentHTML("afterbegin", `<h2>${category} 퀴즈 목록</h2>`);

// 나중에 이 category 값으로 백엔드에 요청
// fetch(`/api/quizzes?category=${category}`)

// 테스트용 더미데이터 (13개)
const dummyData = [
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

const tbody = document.getElementById("list-body");

function renderQuizList() {
  tbody.innerHTML = ""; // 초기화

  const filteredData = category === "전체"
    ? dummyData
    : dummyData.filter(q => q.category === cleanCategory);

  filteredData.forEach((quiz, index) => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${index + 1}</td>
      <td>
        <a href="javascript:void(0);" onclick="goToQuiz(${quiz.id})">${quiz.title}</a>
      </td>
      <td>관리자</td>
      <td>${quiz.completed ? "완료" : "미완료"}</td>
    `;

    tbody.appendChild(tr);
  });
}

// 목록에서 제목 클릭 시 실행
function goToQuiz(quizId) {
  window.location.href = `/quiz/quizSolve.html?id=${quizId}`;
}



// 페이징 변수
const pageSize = 10;
let listCurrentPage = 1;

const STATUS_PAGE_SIZE = 10;  // 한 페이지당 10명
let currentStatusPage = 1;    // 모달 현재 페이지
let currentStatusList = [];   // 현재 열람 중인 퀴즈 현황 데이터

// 테이블 렌더링
function renderTable(page = 1) {
  const tbody = document.getElementById("list-body");
  tbody.innerHTML = "";

  // 선택된 카테고리 값에 맞게 필터링
  const filteredData = category === "전체"
    ? dummyData
    : dummyData.filter(q => q.category === cleanCategory);

  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  const pageData = filteredData.slice(start, end);

  pageData.forEach((quiz, index) => {
    const tr = document.createElement("tr");

    // 화면에 표시할 번호
    let displayNumber;

    if (category === "전체") {
      // 전체 목록이면 원래 등록된 id 사용
      displayNumber = quiz.id;
    } else {
      // 특정 카테고리 필터일 때는 새로 1번부터 시작
      displayNumber = (page - 1) * pageSize + (index + 1);
    }

    // 관리자 여부에 따라 다르게 표시
    if (document.getElementById("last-header").textContent === "작업") {
      tr.innerHTML = `
        <td>${displayNumber}</td>
        <td>
        <a href="javascript:void(0);" onclick="goToQuiz(${quiz.id})">${quiz.title}</a>
        </td>
        <td>
          <button class="statusBtn" data-id="${quiz.id}">보기</button>
        </td>
        <td>
          <button class="deleteBtn" data-id="${quiz.id}">삭제</button>
        </td>
      `;
    } else {
      tr.innerHTML = `
        <td>${displayNumber}</td>
        <td>
        <a href="javascript:void(0);" onclick="goToQuiz(${quiz.id})">${quiz.title}</a>
        </td>
        <td>관리자</td>
        <td>${quiz.completed ? "수료" : "미수료"}</td>
      `;
    }
    tbody.appendChild(tr);
  });

  // 현황 버튼 이벤트
  document.querySelectorAll(".statusBtn").forEach(btn => {
    btn.addEventListener("click", () => {
      const id = parseInt(btn.dataset.id, 10);
      currentStatusList = quizStatusData[id] || [];
      currentStatusPage = 1; // 모달 열 때 항상 1페이지부터
      renderStatusTable(currentStatusPage);
      document.getElementById("status-modal").style.display = "flex";
    });
  });

  // 모달 테이블 렌더링
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

    // 빈 행 맞추기 (10줄 유지)
    const emptyRows = STATUS_PAGE_SIZE - pageData.length;
    for (let i = 0; i < emptyRows; i++) {
      statusBody.insertAdjacentHTML("beforeend", `
      <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
    `);
    }

    renderStatusPagination(currentStatusList.length, page);
  }

  // 모달 페이지네이션
  function renderStatusPagination(totalItems, currentPage) {
    const paginationDiv = document.getElementById("status-pagination");
    paginationDiv.innerHTML = "";

    const totalPages = Math.ceil(totalItems / STATUS_PAGE_SIZE);

    // ◀ 이전 버튼
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "◀";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => renderStatusTable(currentPage - 1));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement("button");
      btn.textContent = i;
      if (i === currentPage) btn.classList.add("active");
      btn.addEventListener("click", () => renderStatusTable(i));
      paginationDiv.appendChild(btn);
    }

    // ▶ 다음 버튼
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "▶";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => renderStatusTable(currentPage + 1));
    paginationDiv.appendChild(nextBtn);
  }

  // 삭제 버튼 이벤트 등록
  document.querySelectorAll(".deleteBtn").forEach(btn => {
    btn.addEventListener("click", () => {
      const id = parseInt(btn.dataset.id, 10);

      // 확인창 띄우기
      if (confirm("정말 삭제하시겠습니까?")) {
        const index = dummyData.findIndex(q => q.id === id);
        if (index !== -1) {
          dummyData.splice(index, 1);
          alert("퀴즈가 삭제되었습니다.");  // 안내 메시지
          renderTable(listCurrentPage);        // 다시 테이블 새로고침
        }
      }
    });
  });

  // 빈 행 맞추기
  const emptyRows = pageSize - pageData.length;
  console.log("pageData.length:", pageData.length);
  console.log("pageSize:", pageSize);
  console.log("빈 행 개수:", emptyRows);
  for (let i = 0; i < emptyRows; i++) {
    console.log("빈 행 추가됨", i);
    const tr = document.createElement("tr");
    if (document.getElementById("last-header").textContent === "작업") {
      tr.innerHTML = `<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>`;
    } else {
      tr.innerHTML = `<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>`;
    }
    tbody.appendChild(tr);
  }

  renderPagination(filteredData.length, page);
}

// 모달 닫기
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


// 목록 페이징 버튼
function renderPagination(totalItems, listCurrentPageParam) {
  const paginationDiv = document.getElementById("pagination");
  paginationDiv.innerHTML = "";

  const totalPages = Math.ceil(totalItems / pageSize);

  // ◀ 이전 버튼
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = listCurrentPageParam === 1;
  prevBtn.addEventListener("click", () => {
    listCurrentPage = listCurrentPage - 1;   // ✅ 전역 갱신
    renderTable(listCurrentPage);
  });
  paginationDiv.appendChild(prevBtn);

  // 페이지 번호 버튼
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === listCurrentPageParam) btn.classList.add("active");
    btn.addEventListener("click", () => {
      listCurrentPage = i;                   // ✅ 전역 갱신
      renderTable(listCurrentPage);
    });
    paginationDiv.appendChild(btn);
  }

  // ▶ 다음 버튼
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = listCurrentPageParam === totalPages;
  nextBtn.addEventListener("click", () => {
    listCurrentPage = listCurrentPage + 1;   // ✅ 전역 갱신
    renderTable(listCurrentPage);
  });
  paginationDiv.appendChild(nextBtn);
}

// 퀴즈 출제 모달
const form = document.getElementById("quiz-form");
const pagesContainer = document.getElementById("quiz-pages");

// 문제 10개 자동 생성
for (let i = 1; i <= 10; i++) {
  const pageDiv = document.createElement("div");
  pageDiv.classList.add("quiz-page");
  pageDiv.dataset.page = i;
  pageDiv.style.display = "none"; // 기본 숨김

  pageDiv.innerHTML = `
    <h3>문제 ${i}</h3>
    
    ${i === 1 ? `
      <label>퀴즈 제목</label><br>
      <input type="text" name="quizTitle" placeholder="퀴즈 제목 입력"><br>
    ` : ""}

    <label>퀴즈 지문</label><br>
    <textarea name="quizText${i}" id="quiz-text" placeholder="문제 지문 입력"></textarea><br>

    <label>퀴즈 이미지</label><br>
    <input type="file" name="quizImage${i}" accept="image/*"><br>

    <!-- 이미지 미리보기 영역 -->
    <div class="edit-image-preview" style="margin-top:10px;">
      <img class="preview-img" src="" alt="미리보기"
           style="margin: 0 auto; max-width:30%; display:none;">
    </div>

    <div class="options-box">
      <div class="option-item">
        <label>보기 1</label>
        <input type="text" name="option1_${i}" placeholder="객관식 1번 문항을 입력해 주세요.">
      </div>
      <div class="option-item">
        <label>보기 2</label>
        <input type="text" name="option2_${i}" placeholder="객관식 2번 문항을 입력해 주세요.">
      </div>
      <div class="option-item">
        <label>보기 3</label>
        <input type="text" name="option3_${i}" placeholder="객관식 3번 문항을 입력해 주세요.">
      </div>
      <div class="option-item">
        <label>보기 4</label>
        <input type="text" name="option4_${i}" placeholder="객관식 4번 문항을 입력해 주세요.">
      </div>
    </div>

    <div class="answer-box">
      <label>정답 번호</label>
      <select id="answer-option-box" name="answer${i}">
        <option value="1">1번</option>
        <option value="2">2번</option>
        <option value="3">3번</option>
        <option value="4">4번</option>
      </select>
    </div>
  `;
  pagesContainer.appendChild(pageDiv);
}

// 이미지 미리보기 (출제 모달)
document.addEventListener("change", (e) => {
  if (e.target.matches("input[type='file'][name^='quizImage']")) {
    const file = e.target.files[0];
    const preview = e.target.closest(".quiz-page").querySelector(".preview-img");

    if (file) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        preview.src = ev.target.result;
        preview.style.display = "block";  // 이미지 표시
      };
      reader.readAsDataURL(file);
    } else {
      preview.src = "";
      preview.style.display = "none"; // 파일 해제 시 숨김
    }
  }
});

let quizModalCurrentPage = 1;
const totalPages = 10;

const prevBtn = document.getElementById("quiz-prev-btn");
const nextBtn = document.getElementById("quiz-next-btn");
const submitBtn = document.getElementById("submit-btn");
const pagination = document.getElementById("quiz-pagination");

function renderQuizModalPagination() {
  pagination.innerHTML = "";
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === quizModalCurrentPage) btn.classList.add("active");
    btn.addEventListener("click", () => showPage(i));
    pagination.appendChild(btn);
  }
}

function showPage(page) {
  quizModalCurrentPage = page;

  // 모든 페이지 숨기고 현재 페이지만 보여주기
  document.querySelectorAll(".quiz-page").forEach((p, i) => {
    p.style.display = (i === page - 1) ? "block" : "none";
  });

  // 버튼 표시 제어
  prevBtn.style.display = (page === 1);
  nextBtn.style.display = (page === totalPages);

  // 등록 버튼
  submitBtn.style.display = (page === totalPages) ? "inline-block" : "none";

  // 숫자 버튼 갱신
  renderQuizModalPagination();
}

// 이전/다음 버튼 이벤트
prevBtn.addEventListener("click", () => {
  if (quizModalCurrentPage > 1) showPage(quizModalCurrentPage - 1);
});
nextBtn.addEventListener("click", () => {
  if (quizModalCurrentPage < totalPages) showPage(quizModalCurrentPage + 1);
});

// 모달 열 때 항상 1번 페이지부터
document.getElementById("create-quiz-btn").addEventListener("click", () => {
  document.getElementById("quiz-modal").style.display = "flex";
  showPage(1);
});

// 출제 모달 닫기
document.addEventListener("DOMContentLoaded", () => {
  const quizModal = document.getElementById("quiz-modal");
  const closeBtn = quizModal.querySelector(".closeBtn");

  // X 버튼으로 닫기
  closeBtn.addEventListener("click", () => {
    quizModal.style.display = "none";
  });

  // 바깥 클릭 시 닫기
  window.addEventListener("click", (e) => {
    if (e.target === quizModal) {
      quizModal.style.display = "none";
    }
  });
});

// 퀴즈 등록 버튼 클릭 이벤트
submitBtn.addEventListener("click", () => {
  const quizData = [];

  // 대표 제목 (문제 1에만 존재)
  const mainTitle = document.querySelector(`[name="quizTitle1"]`).value.trim();
  if (!mainTitle) {
    alert("퀴즈 제목을 입력해주세요.");
    return;
  }

  for (let i = 1; i <= 10; i++) {
    const text = document.querySelector(`[name="quizText${i}"]`).value.trim();
    const option1 = document.querySelector(`[name="option1_${i}"]`).value.trim();
    const option2 = document.querySelector(`[name="option2_${i}"]`).value.trim();
    const option3 = document.querySelector(`[name="option3_${i}"]`).value.trim();
    const option4 = document.querySelector(`[name="option4_${i}"]`).value.trim();
    const answer = document.querySelector(`[name="answer${i}"]`).value;

    // 필수값 체크
    if (!text || !option1 || !option2 || !option3 || !option4) {
      alert(`문제 ${i}에 입력하지 않은 칸이 있습니다.`);
      return; // 중단
    }

    quizData.push({
      number: i,
      title: mainTitle,
      text,
      options: [option1, option2, option3, option4],
      answer,
      category: cleanCategory // 현재 화면 카테고리 값
    });
  }

  // 10문제 모두 입력되었을 때
  alert("퀴즈가 등록되었습니다!");

  // ✅ 테스트용: dummyData에 새 퀴즈 추가
  dummyData.push({
    id: dummyData.length + 1,
    title: mainTitle,
    category: cleanCategory,
    date: new Date().toISOString().split("T")[0],
    completed: false
  });

  // 목록 갱신
  // 등록 후 해당 카테고리 목록 페이지로 이동
  window.location.href = `/quiz/quizList.html?category=${encodeURIComponent(category)}`;

  // 모달 닫기
  document.getElementById("quiz-modal").style.display = "none";

  // ✅ 확인용 콘솔 출력
  console.log("등록된 퀴즈 데이터:", quizData);
});


// 사이드바 이동 (학습/퀴즈)
document.querySelectorAll(".study-link").forEach(link => {
  link.addEventListener("click", (e) => {
    e.preventDefault();
    const category = link.dataset.category; // ex) "현대-학습"
    window.location.href = `/study/studyList.html?category=${encodeURIComponent(category)}`;
  });
});

document.querySelectorAll(".quiz-link").forEach(link => {
  link.addEventListener("click", (e) => {
    e.preventDefault();
    const category = link.dataset.category; // ex) "현대-퀴즈"
    window.location.href = `/quiz/quizList.html?category=${encodeURIComponent(category)}`;
  });
});
