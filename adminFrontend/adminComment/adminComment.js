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

// 댓글 관리 더미데이터
const dummyComments = [
  {
    id: 1,
    category: "선사시대",
    postTitle: "선사시대 생활도구",
    writer: "신짱구",
    content: "돌도끼가 신기하네요!",
    type: "댓글",
    date: "2025-09-20"
  },
  {
    id: 2,
    category: "선사시대",
    postTitle: "구석기와 신석기 비교",
    writer: "봉미선",
    content: "구석기보다 신석기가 살기 편했겠죠?",
    type: "답글",
    date: "2025-09-20"
  },
  {
    id: 3,
    category: "고조선과 여러 나라",
    postTitle: "고조선 8조법",
    writer: "신형만",
    content: "법치의 시작 같아요.",
    type: "댓글",
    date: "2025-09-21"
  },
  {
    id: 4,
    category: "고조선과 여러 나라",
    postTitle: "여러 나라 발전",
    writer: "나미리",
    content: "부여랑 삼한이 특히 흥미롭네요.",
    type: "댓글",
    date: "2025-09-21"
  },
  {
    id: 5,
    category: "삼국과 가야",
    postTitle: "삼국시대 전쟁",
    writer: "채성아",
    content: "전쟁이 너무 많았던 시대네요...",
    type: "댓글",
    date: "2025-09-22"
  },
  {
    id: 6,
    category: "삼국과 가야",
    postTitle: "가야 문화",
    writer: "철수",
    content: "가야의 철기 문화가 대단합니다!",
    type: "답글",
    date: "2025-09-22"
  },
  {
    id: 7,
    category: "남북극시대",
    postTitle: "남북극시대1",
    writer: "훈이",
    content: "북쪽과 남쪽이 공존했다니 신기하네요.",
    type: "댓글",
    date: "2025-09-23"
  },
  {
    id: 8,
    category: "남북극시대",
    postTitle: "남북극시대2",
    writer: "유리",
    content: "시대 구분이 독특하네요.",
    type: "답글",
    date: "2025-09-23"
  },
  {
    id: 9,
    category: "고려시대",
    postTitle: "고려시대 건국",
    writer: "맹구",
    content: "왕건의 업적이 크네요!",
    type: "댓글",
    date: "2025-09-24"
  },
  {
    id: 10,
    category: "고려시대",
    postTitle: "고려시대 문화",
    writer: "수지",
    content: "팔만대장경 보고 싶어요",
    type: "답글",
    date: "2025-09-24"
  },
  {
    id: 11,
    category: "조선시대",
    postTitle: "조선 건국",
    writer: "흑곰",
    content: "태조 이성계의 리더십 굿!",
    type: "댓글",
    date: "2025-09-25"
  },
  {
    id: 12,
    category: "근대",
    postTitle: "근대 개항기",
    writer: "액션가면",
    content: "개항의 영향이 엄청났군요.",
    type: "댓글",
    date: "2025-09-25"
  },
  {
    id: 13,
    category: "현대",
    postTitle: "현대사 민주화 운동",
    writer: "고뭉치",
    content: "민주화 과정이 감동적이네요.",
    type: "답글",
    date: "2025-09-25"
  }
];

// 전역 변수 (현재 선택된 카테고리)
let currentCategory = "전체";
let currentPageCmt = 1;
const pageSizeCmt = 8;

// 댓글 목록 렌더링
const tbody = document.getElementById("cmt-body");
const paginationCmt = document.getElementById("cmt-pagination");

function goToPageCmt(page) {
  currentPageCmt = page;
  renderComments();
}

function renderComments() {
  tbody.innerHTML = ""; // 초기화

  const filtered = currentCategory === "전체"
    ? dummyComments
    : dummyComments.filter(c => c.category === currentCategory);

  // 페이징 처리
  const start = (currentPageCmt - 1) * pageSizeCmt;
  const end = start + pageSizeCmt;
  const pagedData = filtered.slice(start, end);

  pagedData.forEach((cmt, index) => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${start + index + 1}</td>
      <td>${cmt.writer}</td>
      <td>
        <div class="post-title">
        <span class="category-label">[${cmt.category}]</span>${cmt.postTitle}
      </div>
        <div class="comment-text">${cmt.content}</div>
      </td>
      <td>${cmt.type}</td>
      <td>${cmt.date}</td>
      <td><button class="delete-btn" data-id="${cmt.id}">삭제</button></td>
    `;

    tbody.appendChild(tr);
  });

  // 빈 행 채우기
  const emptyRows = pageSizeCmt - pagedData.length;
  for (let i = 0; i < emptyRows; i++) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
    <td>&nbsp;</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  `;
    tbody.appendChild(tr);
  }

  addDeleteEvent(); // 삭제 이벤트 새로 연결
  renderPagination(filtered.length);
}

// 삭제 기능
function addDeleteEvent() {
  const deleteButtons = document.querySelectorAll(".delete-btn");

  deleteButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      const id = parseInt(btn.getAttribute("data-id"));
      const index = dummyComments.findIndex(c => c.id === id);

      if (index !== -1) {
        // 확인창 띄우기
        const confirmDelete = confirm("댓글을 삭제하시겠습니까?");
        if (!confirmDelete) return; // 취소 누르면 아무 동작 안 함

        // 확인 눌렀을 때만 삭제 진행
        dummyComments.splice(index, 1);

        // 삭제 후 현재 페이지의 데이터가 비면 이전 페이지로 이동
        const filtered = currentCategory === "전체"
          ? dummyComments
          : dummyComments.filter(c => c.category === currentCategory);

        const maxPage = Math.ceil(filtered.length / pageSizeCmt);
        if (currentPageCmt > maxPage) currentPageCmt = maxPage;

        renderComments();
      }
    });
  });
}

// 목록 페이징 버튼 렌더링
function renderPagination(totalItems) {
  paginationCmt.innerHTML = "";
  const pageCount = Math.ceil(totalItems / pageSizeCmt);

  // 이전
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = currentPageCmt === 1;
  prevBtn.addEventListener("click", () => goToPageCmt(currentPageCmt - 1));
  paginationCmt.appendChild(prevBtn);

  // 번호 버튼
  for (let i = 1; i <= pageCount; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPageCmt) btn.classList.add("active");
    btn.addEventListener("click", () => goToPageCmt(i));
    paginationCmt.appendChild(btn);
  }

  // 다음
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = currentPageCmt === pageCount;
  nextBtn.addEventListener("click", () => goToPageCmt(currentPageCmt + 1));
  paginationCmt.appendChild(nextBtn);
}


// 카테고리 드롭다운 열기/닫기 + 필터링
const categorySelect = document.getElementById("categorySelect");
const categoryMenu = document.getElementById("categoryMenu");
const selectedCategory = document.getElementById("selectedCategory");
const arrow = categorySelect.querySelector(".arrow");

// 열고 닫기
categorySelect.addEventListener("click", () => {
  categoryMenu.classList.toggle("open");
  arrow.classList.toggle("open");
});

// 카테고리 선택
categoryMenu.querySelectorAll("li").forEach(li => {
  li.addEventListener("click", e => {
    currentCategory = e.target.getAttribute("data-category");
    selectedCategory.textContent = currentCategory; // 선택된 카테고리 표시

    categoryMenu.classList.remove("open"); // 닫기
    arrow.classList.remove("open");
    currentPageCmt = 1;
    renderComments();
  });
});

// 페이지 로드 실행
window.onload = () => {
  checkLogin();
  renderComments();
};
