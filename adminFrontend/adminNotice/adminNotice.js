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
window.onload = () => {
    checkLogin();     // 로그인 체크
    fetchNotices(1);  // 첫 페이지 게시글 로드
};

// -----
const pageSize = 10;
let currentPage = 1;

async function fetchNotices(page = 1) {
    try {
        // 실제 API 호출 부분
        // const response = await fetch(`/api/admin/notices?page=${page}&pageSize=${pageSize}`, {
        //     credentials: "include"
        // });
        // const data = await response.json();

        // 더미 데이터 (프론트 테스트용)
        // const dummyNotices = [
        //     { id: 1, title: "서버 점검 안내", writer: "관리자", createdAt: "2025-09-01" },
        //     { id: 2, title: "추석 연휴 휴무 공지", writer: "관리자", createdAt: "2025-09-02" },
        //     { id: 3, title: "사이트 개편 예정 안내", writer: "관리자", createdAt: "2025-09-03" },
        //     { id: 4, title: "신규 강좌 업로드 안내", writer: "관리자", createdAt: "2025-09-04" },
        //     { id: 5, title: "포인트 제도 개편", writer: "관리자", createdAt: "2025-09-05" },
        //     { id: 6, title: "보안 패치 적용 공지", writer: "관리자", createdAt: "2025-09-06" },
        //     { id: 7, title: "모바일 최적화 업데이트", writer: "관리자", createdAt: "2025-09-07" },
        //     { id: 8, title: "회원가입 이벤트 안내", writer: "관리자", createdAt: "2025-09-08" },
        //     { id: 9, title: "출석 보상 개편", writer: "관리자", createdAt: "2025-09-09" },
        //     { id: 10, title: "주간 퀴즈 대회 개최", writer: "관리자", createdAt: "2025-09-10" },
        //     { id: 11, title: "장애 처리 보고", writer: "관리자", createdAt: "2025-09-11" },
        //     { id: 12, title: "신규 회원 혜택 안내", writer: "관리자", createdAt: "2025-09-12" },
        //     { id: 13, title: "교육자료 업데이트", writer: "관리자", createdAt: "2025-09-13" },
        //     { id: 14, title: "FAQ 페이지 개편", writer: "관리자", createdAt: "2025-09-14" },
        //     { id: 15, title: "공지사항 테스트", writer: "관리자", createdAt: "2025-09-15" }
        // ];

        // 유저 공지사항 페이지와 연결
        let notices = JSON.parse(localStorage.getItem("notices")) || [];
        const totalItems = notices.length;

        // 페이지 계산
        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const paginatedNotices = notices.slice(start, end);

        // data 구조 예시: { notices: [...], totalItems: 57 }
        // renderNoticeTable(data.notices);
        // renderPagination(data.totalPages, page);

        // 더미 데이터 테스트용
        renderNoticeTable(paginatedNotices);
        renderPagination(totalItems, page);

        // 현재 페이지 갱신
        currentPage = page;
    } catch (err) {
        console.error("공지사항 불러오기 에러:", err);
    }
}

// 테이블 출력
function renderNoticeTable(notices) {
    const tbody = document.getElementById("notice-body");
    tbody.innerHTML = "";

    notices.forEach(notice => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${notice.id}</td>
      <td><a href="noticeDetail.html?id=${notice.id}">${notice.title}</td>
      <td>${notice.writer}</td>
      <td>${notice.createdAt}</td>
      <td>
        <button class="editBtn" data-id="${notice.id}">수정</button>
        <button class="deleteBtn" data-id="${notice.id}">삭제</button>
      </td>
    `;
        tbody.appendChild(tr);
    });

    // 남은 빈 행 채우기
    const emptyRows = pageSize - notices.length;
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

    // 수정/삭제 버튼 이벤트 연결
    document.querySelectorAll(".editBtn").forEach(btn =>
        btn.addEventListener("click", () => openEditModal(btn.dataset.id))
    );
    document.querySelectorAll(".deleteBtn").forEach(btn =>
        btn.addEventListener("click", () => deleteNotice(btn.dataset.id))
    );
}

// 수정 api용
// function openEditModal(id) {
//     fetch(`/api/admin/notices/${id}`)
//         .then(res => res.json())
//         .then(notice => {
//             document.getElementById("editTitle").value = notice.title;
//             document.getElementById("editContent").value = notice.content;
//             document.getElementById("editForm").dataset.id = id;

//             document.getElementById("editModal").style.display = "flex";
//         });
// }

// 수정 localStorage용
function openEditModal(id) {
    let notices = JSON.parse(localStorage.getItem("notices")) || [];
    const notice = notices.find(n => n.id == id);

    if (notice) {
        document.getElementById("editTitle").value = notice.title;
        document.getElementById("editContent").value = notice.content;
        document.getElementById("editForm").dataset.id = id;

        document.getElementById("editModal").style.display = "flex";
    }
}

// 닫기 버튼
document.querySelector(".closeBtn").onclick = () => {
    document.getElementById("editModal").style.display = "none";
};

// 수정 완료
document.getElementById("editForm").addEventListener("submit", async e => {
    e.preventDefault();
    const id = e.target.dataset.id;
    const updated = {
        title: document.getElementById("editTitle").value,
        content: document.getElementById("editContent").value
    };

    // await fetch(`/api/admin/notices/${id}`, {
    //     method: "PUT",
    //     headers: { "Content-Type": "application/json" },
    //     body: JSON.stringify(updated),
    //     credentials: "include"
    // });

    // fetchNotices(); // 새로고침

    // localStorage 반영
    let notices = JSON.parse(localStorage.getItem("notices"));
    notices = notices.map(n => n.id == id ? { ...n, ...updated } : n);
    localStorage.setItem("notices", JSON.stringify(notices));

    document.getElementById("editModal").style.display = "none";
    fetchNotices(currentPage);
});

// 삭제
async function deleteNotice(id) {
    if (!confirm(`${id}번 게시물을 삭제하시겠습니까?`)) return;

    // await fetch(`/api/admin/notices/${id}`, {
    //     method: "DELETE",
    //     credentials: "include"
    // });

    // fetchNotices(); // 새로고침

    // localStorage 반영
    let notices = JSON.parse(localStorage.getItem("notices"));
    notices = notices.filter(n => n.id != id);
    localStorage.setItem("notices", JSON.stringify(notices));

    fetchNotices(currentPage);
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
    prevBtn.addEventListener("click", () => fetchNotices(currentPage - 1));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");
        btn.addEventListener("click", () => fetchNotices(i));
        paginationDiv.appendChild(btn);
    }

    // ▶ 다음 버튼
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "▶";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => fetchNotices(currentPage + 1));
    paginationDiv.appendChild(nextBtn);
}
