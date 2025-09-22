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
                document.getElementById("post-btn-box").style.display = "block";
            } else {
                // 일반 유저
                usernameLink.textContent = data.username;
                usernameLink.href = "/mypage/mypage.html";

                // 글쓰기 버튼 숨기기
                document.getElementById("post-btn-box").style.display = "none";
            }
            userDiv.style.display = "block";
        } else {
            userDiv.style.display = "none";
            document.getElementById("post-btn-box").style.display = "none";
        }
    } catch (err) {
        console.error("로그인 체크 에러:", err);
    }
}

// 페이지 로드시 실행
window.onload = () => {
    // 강제로 관리자 화면 보기
    document.getElementById("last-header").textContent = "작업";
    document.getElementById("post-btn-box").style.display = "block";

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

// 사이드바에서 학습/퀴즈 이동
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
