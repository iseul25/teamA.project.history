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

                // 관리자용 버튼 보이기
                document.getElementById(".post-actions").style.display = "block";
            } else {
                // 일반 유저
                usernameLink.textContent = data.username;
                usernameLink.href = "/mypage/mypage.html";

                // 게시글 수정, 삭제 버튼 숨기기
                document.getElementById(".post-actions").style.display = "none";
            }
            userDiv.style.display = "block";
        } else {
            userDiv.style.display = "none";
            document.getElementById(".post-actions").style.display = "none";
        }
    } catch (err) {
        console.error("로그인 체크 에러:", err);
    }
}

// 페이지 로드시 실행
window.onload = () => {
    // 강제로 관리자 화면 보기
    //document.getElementById(".post-actions").style.display = "block";

    checkLogin();
    loadPost()
}

// 테스트용 더미 데이터 (학습목록과 동일하게 유지)
const dummyData = [
    {
        id: 1, category: "선사시대", title: "선사시대 생활도구", content: "선사시대 생활도구에 대한 본문입니다.", date: "2025-01-01", writer: "관리자", image: "img/taegeuk1.png", comments: [
            {
                id: 1,
                writer: "현재 사용자",
                date: "2025-09-22",
                content: "좋은 글이네요!",
                replies: [
                    { id: 11, writer: "관리자", date: "2025-09-22", content: "읽어주셔서 감사합니다!" },
                    { id: 12, writer: "사용자2", date: "2025-09-22", content: "저도 공감합니다." }
                ]
            },
            {
                id: 2,
                writer: "현재 사용자",
                date: "2025-09-22",
                content: "많이 배워갑니다.",
                replies: []
            }
        ]
    },
    { id: 2, category: "선사시대", title: "구석기와 신석기 비교", content: "구석기와 신석기 본문 내용입니다.", date: "2025-01-02", writer: "관리자", image: "img/taegeuk1.png", comments:[] },
    { id: 3, category: "고조선과 여러 나라", title: "고조선 8조법", content: "고조선 8조법 관련 본문입니다.", date: "2025-01-03", writer: "관리자", image: "", comments:[] },
    { id: 4, category: "고조선과 여러 나라", title: "여러 나라 발전", content: "여러 나라 발전 본문입니다.", date: "2025-01-04", writer: "관리자", image: "", comments:[] },
    { id: 5, category: "삼국과 가야", title: "삼국시대 전쟁", content: "삼국시대 전쟁 관련 본문입니다.", date: "2025-01-05", writer: "관리자", image: "", comments:[] },
    { id: 6, category: "삼국과 가야", title: "가야 문화", content: "가야 문화 본문입니다.", date: "2025-01-06", writer: "관리자", image: "", comments:[] },
    { id: 7, category: "남북극시대", title: "남북극시대1", content: "남북극시대1 본문입니다.", date: "2025-01-07", writer: "관리자", image: "", comments:[] },
    { id: 8, category: "남북극시대", title: "남북극시대2", content: "남북극시대2 본문입니다.", date: "2025-01-08", writer: "관리자", image: "", comments:[] },
    { id: 9, category: "고려시대", title: "고려시대 건국", content: "고려시대 건국 본문입니다.", date: "2025-01-09", writer: "관리자", image: "", comments:[] },
    { id: 10, category: "고려시대", title: "고려시대 문화", content: "고려시대 문화 본문입니다.", date: "2025-01-10", writer: "관리자", image: "", comments:[] },
    { id: 11, category: "조선시대", title: "조선 건국", content: "조선 건국 본문입니다.", date: "2025-01-11", writer: "관리자", image: "", comments:[] },
    { id: 12, category: "근대", title: "근대 개항기", content: "근대 개항기 본문입니다.", date: "2025-01-12", writer: "관리자", image: "", comments:[] },
    { id: 13, category: "현대", title: "현대사 민주화 운동", content: "현대사 민주화 운동 본문입니다.", date: "2025-01-13", writer: "관리자", image: "", comments:[] }
];

// 게시글 로드
function loadPost() {
    const params = new URLSearchParams(window.location.search);
    const postId = parseInt(params.get("id"), 10);

    const post = dummyData.find(p => p.id === postId);

    if (!post) {
        document.querySelector("#post-box").innerHTML = "<p>게시글을 찾을 수 없습니다.</p>";
        return;
    }

    // 카테고리명
    document.querySelector(".post-category").textContent = post.category;

    // 제목 + 작성자 + 날짜
    document.querySelector(".post-title h3").textContent = post.title;
    document.querySelector(".post-meta").textContent = `${post.writer} | ${post.date}`;

    // 본문 & 이미지
    const postContent = document.querySelector(".post-content");
    postContent.innerHTML = `
        ${post.image ? `<div class="post-image"><img src="${post.image}" alt="게시글 이미지"></div>` : ""}
        <p>${post.content}</p>
    `;

    // 댓글 렌더링
    renderComments(post);
    setupCommentEvents(post);
    setupReplyEvents(post);

    // 수정 버튼
    document.getElementById("edit-post-btn").addEventListener("click", () => {
        const modal = document.getElementById("editModal");
        modal.style.display = "flex";

        // 기존 내용 채우기
        document.getElementById("editTitle").value = post.title;
        document.getElementById("editContent").value = post.content;

        // 기존 이미지 미리보기
        const previewImg = document.querySelector("#editImagePreview img");
        if (post.image) {
            previewImg.src = post.image;
            previewImg.style.display = "block";
        } else {
            previewImg.style.display = "none";
        }
    });

    // 이미지 선택 시 미리보기
    document.getElementById("editImage").addEventListener("change", (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (event) {
                const previewImg = document.querySelector("#editImagePreview img");
                previewImg.src = event.target.result;
                previewImg.style.display = "block";
            };
            reader.readAsDataURL(file);
        }
    });

    // 모달 닫기
    document.querySelector(".closeBtn").addEventListener("click", () => {
        document.getElementById("editModal").style.display = "none";
    });

    // 수정 완료
    document.getElementById("editForm").addEventListener("submit", (e) => {
        e.preventDefault();

        post.title = document.getElementById("editTitle").value;
        post.content = document.getElementById("editContent").value;

        // 이미지 변경 반영
        const imageInput = document.getElementById("editImage");
        if (imageInput.files[0]) {
            const reader = new FileReader();
            reader.onload = function (event) {
                post.image = event.target.result; // base64로 저장 (테스트용)
                alert("게시글이 수정되었습니다.");
                document.getElementById("editModal").style.display = "none";
                loadPost(); // 다시 로드
            };
            reader.readAsDataURL(imageInput.files[0]);
        } else {
            alert("게시글이 수정되었습니다.");
            document.getElementById("editModal").style.display = "none";
            loadPost();
        }
    });

    // 삭제 버튼
    document.getElementById("delete-post-btn").addEventListener("click", () => {
        alert("정말 삭제하시겠습니까?");
    });
}

function renderComments(post) {
    const commentList = document.getElementById("comment-list");
    commentList.innerHTML = "";

    // 현재 로그인 사용자 (임시로 저장, checkLogin에서 세팅 가능)
    const currentUser = "현재 사용자";

    post.comments.forEach(comment => {
        const li = document.createElement("li");
        li.classList.add("comment");

        // 댓글 삭제 버튼 (본인만 보이도록 조건부 렌더링)
        const deleteBtn = comment.writer === currentUser
            ? `<button class="delete-btn" data-id="${comment.id}">삭제</button>`
            : "";

        li.innerHTML = `
            <div class="comment-content">
                <p><strong>${comment.writer}</strong> | ${comment.date}</p>
                <p>${comment.content}</p>
                <button class="reply-btn" data-id="${comment.id}">답글 달기</button>
                ${deleteBtn}
            </div>
            <ul class="replies">
                ${comment.replies.map(reply => `
                    <li class="comment">
                        <div class="comment-content">
                            <p><strong>${reply.writer}</strong> | ${reply.date}</p>
                            <p>${reply.content}</p>
                            ${
                                reply.writer === currentUser
                                    ? `<button class="delete-btn" data-id="${reply.id}" data-parent="${comment.id}">삭제</button>`
                                    : ""
                            }
                        </div>
                    </li>
                `).join("")}
            </ul>
        `;
        commentList.appendChild(li);
    });

    // 삭제 이벤트 등록
    document.querySelectorAll(".delete-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const commentId = parseInt(btn.dataset.id, 10);
            const parentId = btn.dataset.parent ? parseInt(btn.dataset.parent, 10) : null;

            if (parentId) {
                // 대댓글 삭제
                const parentComment = post.comments.find(c => c.id === parentId);
                parentComment.replies = parentComment.replies.filter(r => r.id !== commentId);
            } else {
                // 댓글 삭제
                post.comments = post.comments.filter(c => c.id !== commentId);
            }

            renderComments(post);
            setupReplyEvents(post);
        });
    });
}

function setupCommentEvents(post) {
    // 댓글 등록
    document.getElementById("comment-submit").addEventListener("click", () => {
        const input = document.getElementById("comment-input");
        const content = input.value.trim();
        if (!content) return;

        const newComment = {
            id: Date.now(), // 임시 ID
            writer: "현재 사용자", // 나중에 로그인 유저명으로 변경
            date: new Date().toISOString().split("T")[0],
            content,
            replies: []
        };

        post.comments.push(newComment);
        input.value = "";
        renderComments(post);
        setupReplyEvents(post); // 답글 이벤트 다시 등록
    });
}

// 답글 달기 이벤트
function setupReplyEvents(post) {
    document.querySelectorAll(".reply-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const parentId = parseInt(btn.dataset.id, 10);

            // 이미 답글 입력창이 있으면 제거
            const existingForm = btn.parentElement.querySelector(".reply-form");
            if (existingForm) {
                existingForm.remove();
                return;
            }

            // 답글 입력창 만들기
            const replyForm = document.createElement("div");
            replyForm.classList.add("reply-form");
            replyForm.innerHTML = `
                <textarea class="reply-input" placeholder="답글을 입력하세요"></textarea>
                <button class="reply-submit">등록</button>
            `;

            btn.parentElement.appendChild(replyForm);

            // 등록 버튼 이벤트
            replyForm.querySelector(".reply-submit").addEventListener("click", () => {
                const replyContent = replyForm.querySelector(".reply-input").value.trim();
                if (!replyContent) return;

                // 부모 댓글 찾기
                const parentComment = post.comments.find(c => c.id === parentId);
                if (parentComment) {
                    parentComment.replies.push({
                        id: Date.now(),
                        writer: "현재 사용자",
                        date: new Date().toISOString().split("T")[0],
                        content: replyContent
                    });
                } else {
                    // 대댓글일 경우 → 상위 댓글 안에서 찾기
                    for (let c of post.comments) {
                        if (c.replies.find(r => r.id === parentId)) {
                            c.replies.push({
                                id: Date.now(),
                                writer: "현재 사용자",
                                date: new Date().toISOString().split("T")[0],
                                content: replyContent
                            });
                            break;
                        }
                    }
                }

                // 다시 렌더링
                renderComments(post);
                setupReplyEvents(post);
            });
        });
    });
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

