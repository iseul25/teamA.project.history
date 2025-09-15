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

// 회원 등록
const addUserBtn = document.getElementById("addUserBtn");
const modal = document.getElementById("addUserBox");
const closeModal = document.getElementById("closeModal");
const addUserForm = document.getElementById("addUserForm");
const emailInput = document.getElementById("emailInput");
const checkEmailBtn = document.getElementById("checkEmailBtn");
const emailMessage = document.getElementById("emailMessage");

let emailValid = false; // 이메일 중복 여부

// 모달 열기
addUserBtn.addEventListener("click", () => {
    modal.style.display = "flex";
});

// 모달 닫기
closeModal.addEventListener("click", () => {
    modal.style.display = "none";
    addUserForm.reset();
    emailMessage.style.display = "none";
    emailValid = false;
});

// 모달 외부 클릭 시 닫기
window.addEventListener("click", (e) => {
    if (e.target === modal) modal.style.display = "none";
});

// 이메일 중복확인
checkEmailBtn.addEventListener("click", async () => {
    const email = emailInput.value.trim();
    if (!email) {
        emailMessage.textContent = "이메일을 입력하세요.";
        emailMessage.style.display = "block";
        emailValid = false;
        return;
    }

    try {
        const response = await fetch(`/api/users/check-email?email=${encodeURIComponent(email)}`);
        const data = await response.json();

        if (data.exists) {
            emailMessage.textContent = "이미 사용 중인 이메일입니다.";
            emailMessage.style.color = "red";
            emailValid = false;
        } else {
            emailMessage.textContent = "사용 가능한 이메일입니다.";
            emailMessage.style.color = "green";
            emailValid = true;
        }
        emailMessage.style.display = "block";
    } catch (err) {
        console.error(err);
        emailMessage.textContent = "중복 확인 중 오류가 발생했습니다.";
        emailMessage.style.color = "red";
        emailMessage.style.display = "block";
        emailValid = false;
    }
});

// 회원 등록
addUserForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!emailValid) {
        alert("이메일 중복확인을 해주세요.");
        return;
    }

    const formData = new FormData(addUserForm);
    const userData = {
        name: formData.get("name"),
        email: formData.get("email"),
        password: formData.get("password")
    };

    try {
        const response = await fetch("/api/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            alert("회원이 등록되었습니다!");
            modal.style.display = "none";
            addUserForm.reset();
            emailMessage.style.display = "none";
            emailValid = false;
            loadUsers(); // 회원목록 새로고침
        } else {
            const errorData = await response.json();
            alert("회원 등록 실패: " + errorData.message);
        }
    } catch (err) {
        console.error(err);
        alert("서버 오류");
    }
});

// 회원 삭제
const tbody = document.getElementById("userlist-body");
const paginationDiv = document.getElementById("pagination");

// 설정
const useTestData = true; // true면 테스트용, false면 DB 연동
const pageSize = 10;        // 한 페이지에 보여줄 회원 수

// 테스트용 데이터
let testUsers = [
    { id: 1, name: "홍길동", email: "hong@test.com", type: "일반", attend: true, point: 120 },
    { id: 2, name: "신짱구", email: "kim@test.com", type: "일반", attend: true, point: 300 },
    { id: 3, name: "김철수", email: "lee@test.com", type: "관리자", attend: true, point: 500 },
    { id: 4, name: "유리", email: "park@test.com", type: "일반", attend: true, point: 50 },
    { id: 5, name: "이훈이", email: "choi@test.com", type: "일반", attend: false, point: 200 },
    { id: 6, name: "맹구", email: "jung@test.com", type: "일반", attend: true, point: 30 },
    { id: 7, name: "이훈이", email: "choi@test.com", type: "일반", attend: false, point: 200 },
    { id: 8, name: "맹구", email: "jung@test.com", type: "일반", attend: true, point: 30 },
    { id: 9, name: "이훈이", email: "choi@test.com", type: "일반", attend: false, point: 200 },
    { id: 10, name: "맹구", email: "jung@test.com", type: "일반", attend: false, point: 30 },
    { id: 11, name: "이훈이", email: "choi@test.com", type: "일반", attend: false, point: 200 },
    { id: 12, name: "맹구", email: "jung@test.com", type: "일반", attend: false, point: 30 }
];

let currentPage = 1;
let users = [];

// 데이터 로드 
async function loadUsers() {
    if (useTestData) {
        users = [...testUsers]; // 테스트용 데이터
        renderPage(1);
    } else {
        try {
            const response = await fetch("/api/users");
            users = await response.json(); // DB 데이터
            renderPage(1);
        } catch (err) {
            console.error("회원 불러오기 실패:", err);
        }
    }
}

// 페이지 렌더링
function renderPage(page) {
    currentPage = page;
    tbody.innerHTML = "";

    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const pageUsers = users.slice(start, end);

    pageUsers.forEach(user => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>${user.type}</td>
            <td>${(user.attend === "Y" || user.attend === true) ? "출석" : "미출석"}</td>
            <td>${user.point}</td>
            <td><button class="deleteBtn" data-id="${user.id}">삭제</button></td>
        `;
        tbody.appendChild(tr);
    });

    // 삭제 버튼
    document.querySelectorAll(".deleteBtn").forEach(btn => {
        btn.addEventListener("click", async (e) => {
            const userId = parseInt(e.target.dataset.id);
            if (confirm("정말 삭제하시겠습니까?")) {
                if (useTestData) {
                    testUsers = testUsers.filter(u => u.id !== userId);
                    users = [...testUsers];
                    renderPage(currentPage);
                } else {
                    await fetch(`/api/users/${userId}`, { method: "DELETE" });
                    loadUsers();
                }
            }
        });
    });

    renderPagination();
}

// 페이징
function renderPagination() {
    paginationDiv.innerHTML = "";
    const pageCount = Math.ceil(users.length / pageSize);

    // 이전 버튼
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "◀";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => renderPage(currentPage - 1));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    for (let i = 1; i <= pageCount; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        btn.classList.add("page-btn");
        if (i === currentPage) btn.classList.add("active");
        btn.addEventListener("click", () => renderPage(i));
        paginationDiv.appendChild(btn);
    }

    // 다음 버튼
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "▶";
    nextBtn.disabled = currentPage === pageCount;
    nextBtn.addEventListener("click", () => renderPage(currentPage + 1));
    paginationDiv.appendChild(nextBtn);
}

// 페이지 로드 시 실행
loadUsers();
