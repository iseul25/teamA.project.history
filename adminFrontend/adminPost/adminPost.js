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

// 페이지 로드 시 로그인 체크
window.onload = checkLogin;

// -----
const tbody = document.getElementById("post-body");
const paginationDiv = document.getElementById("pagination");

const pageSize = 10;
let currentPage = 1;
let posts = [];
let currentCategory = "전체";

// 테스트용 데이터
const testPosts = [
  { id: 1, category: "조선시대", title: "첫 번째 글", content: "내용1", date: "2025-08-15" },
  { id: 2, category: "선사시대", title: "두 번째 글", content: "내용1", date: "2025-07-16" },
  { id: 3, category: "고조선과 여러 나라", title: "세 번째 글", content: "내용1", date: "2025-10-15" },
  { id: 4, category: "삼국과 가야", title: "네 번째 글", content: "내용1", date: "2025-09-12" },
  { id: 5, category: "남북극시대", title: "다섯 번째 글", content: "내용1", date: "2025-09-15" },
  { id: 6, category: "고려시대", title: "여섯 번째 글", content: "내용1", date: "2025-06-05" },
  { id: 7, category: "근대", title: "일곱 번째 글", content: "내용1", date: "2025-01-08" },
  { id: 8, category: "현대", title: "여덟 번째 글", content: "내용1", date: "2025-03-26" },
  { id: 9, category: "조선시대", title: "아홉 번째 글", content: "내용1", date: "2025-09-15" },
  { id: 10, category: "현대", title: "열 번째 글", content: "내용1", date: "2025-09-15" },
  { id: 11, category: "근대", title: "열한 번째 글", content: "내용1", date: "2025-09-15" },
  { id: 12, category: "삼국과 가야", title: "열두 번째 글", content: "내용2", date: "2025-09-14" }
];

const categoryHeader = document.getElementById("categoryHeader");
const categoryMenu = document.getElementById("categoryMenu");
const arrow = categoryHeader.querySelector(".arrow");

// 드롭다운 열기/닫기
categoryHeader.addEventListener("click", (e) => {
  if (e.target.closest("#categoryMenu")) return; // 메뉴 내부 클릭이면 토글 금지
  categoryMenu.classList.toggle("show");
  arrow.classList.toggle("up");
});

// 카테고리 메뉴 클릭
categoryMenu.querySelectorAll("li").forEach(item => {
  item.addEventListener("click", (e) => {
    e.stopPropagation();               // 버블링 차단(중요)
    const val = item.dataset.category;
    currentCategory = val;
    // currentCategory = item.dataset.category;

    // 드롭다운 다시 닫기
    categoryMenu.classList.remove("show");
    arrow.classList.remove("up");

    renderPage(1); // 첫 페이지부터 다시
  });
});

// 바깥 클릭 시 닫기
window.addEventListener("click", (e) => {
  if (!categoryHeader.contains(e.target)) {
    categoryMenu.classList.remove("show");
    arrow.classList.remove("up");
  }
});

// 게시물 불러오기
async function loadPosts() {
  // DB 연동 시:
  // const response = await fetch("/api/posts");
  // posts = await response.json();

  // 테스트용
  const saved = localStorage.getItem("posts");
  if (saved) {
    posts = JSON.parse(saved);
  } else {
    posts = [...testPosts]; // 초기 데이터
    localStorage.setItem("posts", JSON.stringify(posts));
  }
  renderPage(1);
}

// 특정 페이지 렌더링
function renderPage(page) {
  currentPage = page;
  tbody.innerHTML = "";

  const filtered = posts.filter(
    post => currentCategory === "전체" || post.category === currentCategory
  );

  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  const pagePosts = filtered.slice(start, end);

  // 밑에 post.title은 게시글 제목이고 클릭 시 실제 게시글로 넘어갑니다.
  // 실제 게시글 화면은 우진님이 만드신 파일로 연결하시면 돼요. 아래 링크는 임시 링크입니다.
  // 파일 연결 예시 postDetail.html 같이 업로드 하겠습니다. 참고해 주세요.
  pagePosts.forEach(post => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${post.id}</td>
      <td>${post.category}</td>
      <td>
        <a href="postDetail.html?id=${post.id}" class="post-link">
          ${post.title}
        </a>
      </td>
      <td>${post.date}</td>
      <td>
        <button onclick="editPost(${post.id})" id="editBtn">수정</button>
        <button onclick="deletePost(${post.id})" id="deleteBtn">삭제</button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // ✅ 빈 행 채우기 (항상 10행 유지)
  const remain = pageSize - pagePosts.length;
  for (let i = 0; i < remain; i++) {
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

  renderPagination(filtered.length);
}

// 페이징 버튼
function renderPagination(totalItems) {
  paginationDiv.innerHTML = "";
  const pageCount = Math.ceil(totalItems / pageSize);

  // 이전
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "◀";
  prevBtn.disabled = currentPage === 1;
  prevBtn.addEventListener("click", () => renderPage(currentPage - 1));
  paginationDiv.appendChild(prevBtn);

  // 번호 버튼
  for (let i = 1; i <= pageCount; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPage) btn.classList.add("active");
    btn.addEventListener("click", () => renderPage(i));
    paginationDiv.appendChild(btn);
  }

  // 다음
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "▶";
  nextBtn.disabled = currentPage === pageCount;
  nextBtn.addEventListener("click", () => renderPage(currentPage + 1));
  paginationDiv.appendChild(nextBtn);
}

// 삭제
async function deletePost(id) {
  if (!confirm(`${id}번 게시물을 삭제하시겠습니까?`)) return;
  try {
    // DB 연동
    // await fetch(`/api/posts/${id}`, { method: "DELETE" });
    posts = posts.filter(p => p.id !== id); // 테스트용 삭제
    renderPage(currentPage);
  } catch (err) {
    console.error("삭제 실패:", err);
  }
}

// 수정
const modal = document.getElementById("editModal");
const closeBtn = document.querySelector(".closeBtn");
const editForm = document.getElementById("editForm");
let currentEditId = null;

function editPost(id) {
  currentEditId = id;
  const post = posts.find(p => p.id === id);
  document.getElementById("editTitle").value = post.title;
  document.getElementById("editContent").value = post.content;
  // modal.style.display = "block";
  modal.classList.add("show");
}

// closeBtn.onclick = () => (modal.style.display = "none");
closeBtn.onclick = () => {
  modal.classList.remove("show");
};

editForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const updatedPost = {
    // id: currentEditId,
    title: document.getElementById("editTitle").value,
    content: document.getElementById("editContent").value
  };

  try {
    // DB 연동 모드
    const response = await fetch(`/api/posts/${currentEditId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedPost)
    });

    if (!response.ok) throw new Error("서버 오류");

  } catch (err) {
    console.warn("⚠️ DB 연결 실패, 로컬에서만 업데이트합니다:", err);
  }

  // 프론트엔드 테스트용
  // posts 배열에서 해당 글 찾아서 업데이트
  const index = posts.findIndex(p => p.id === currentEditId);
  if (index !== -1) {
    posts[index] = { ...posts[index], ...updatedPost };
  }

  // localStorage에 저장
  localStorage.setItem("posts", JSON.stringify(posts));

  // 모델 닫고 화면 갱신
  modal.style.display = "none";
  renderPage(currentPage);

  // 테스트용 알림
  alert(`${currentEditId}번 게시글이 수정되었습니다!`);
});

// 시작
window.onload = loadPosts;
