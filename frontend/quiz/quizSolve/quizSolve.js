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
                document.getElementById("edit-question-btn").style.display = "block";
            } else {
                // 일반 유저
                usernameLink.textContent = data.username;
                usernameLink.href = "/mypage/mypage.html";

                // 퀴즈 수정 버튼 숨기기
                document.getElementById("edit-question-btn").style.display = "none";
            }
            userDiv.style.display = "block";
        } else {
            userDiv.style.display = "none";
            document.getElementById("edit-question-btn").style.display = "none";
        }
    } catch (err) {
        console.error("로그인 체크 에러:", err);
    }
}

// 페이지 로드시 실행
window.onload = async () => {
    // 강제로 관리자 화면 보기
    document.getElementById("edit-question-btn").style.display = "block";

    // await checkLogin();  // 로그인 먼저 확인
    const quizId = parseInt(getQuizIdFromURL(), 10);
    console.log("quizId:", quizId);
    if (!quizId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/quiz/quizHome.html";
        return;
    }

    currentQuiz = dummyQuestions[quizId];
    if (!currentQuiz) {
        document.querySelector(".quiz-title").textContent = "퀴즈를 찾을 수 없습니다.";
        return;
    }

    // 타이틀 출력
    document.querySelector(".quiz-title").textContent = currentQuiz.title;

    // 첫 번째 문제 표시
    currentIndex = 0;
    renderQuestion(currentQuiz.questions[currentIndex], currentIndex, currentQuiz.questions.length);

    // 버튼 이벤트 연결
    document.getElementById("prev-btn").addEventListener("click", () => {
        if (currentIndex > 0) {
            currentIndex--;
            renderQuestion(currentQuiz.questions[currentIndex], currentIndex, currentQuiz.questions.length);
        }
    });

    document.getElementById("next-btn").addEventListener("click", () => {
        if (currentIndex < currentQuiz.questions.length - 1) {
            currentIndex++;
            renderQuestion(currentQuiz.questions[currentIndex], currentIndex, currentQuiz.questions.length);
        }
    });
};

const dummyQuestions = {
    1: {
        category: "선사시대",
        title: "선사시대 생활도구 퀴즈",
        questions: [
            {
                text: "구석기 시대 사람들이 사용한 대표적인 도구는 무엇일까요?",
                image: "img/stone_tool.png",
                options: ["간석기", "뗀석기", "청동기", "철제도구"],
                answer: 2
            },
            {
                text: "신석기 시대의 대표적인 생활 양식은?",
                image: null,
                options: ["수렵과 채집", "농경과 목축", "청동 무기 제작", "철제 무기 사용"],
                answer: 2
            }
        ]
    },

    2: {
        category: "선사시대",
        title: "구석기와 신석기 비교",
        questions: [
            {
                text: "구석기 시대 주거 형태는?",
                image: "img/막집.jpg",
                options: ["움집", "동굴·막집", "기와집", "궁궐"],
                answer: 2
            },
            {
                text: "신석기 시대 사람들이 곡식을 빻을 때 사용한 도구는?",
                image: "img/갈판과 갈돌.jpg",
                options: ["갈판과 갈돌", "청동기", "쇠도끼", "돌화살촉"],
                answer: 1
            },
            {
                text: "한글을 만든 왕은 누구일까요?",
                image: null,
                options: ["세종대왕", "광개토대왕", "태조 왕건", "단군"],
                answer: 1
            },
            {
                text: "고조선을 세운 사람은?",
                image: null,
                options: ["이성계", "단군", "주몽", "세종"],
                answer: 2
            },
            {
                text: "삼국시대 중 가장 먼저 멸망한 나라는?",
                image: null,
                options: ["백제", "고구려", "신라", "가야"],
                answer: 1
            },
            {
                text: "훈민정음을 반포한 해는?",
                image: null,
                options: ["1443년", "1392년", "1945년", "2000년"],
                answer: 1
            },
            {
                text: "임진왜란 때 활약한 장군은?",
                image: "img/이순신.jpg",
                options: ["을지문덕", "강감찬", "이순신", "김유신"],
                answer: 3
            },
            {
                text: "고려를 세운 왕은?",
                image: null,
                options: ["이성계", "왕건", "세종", "단군"],
                answer: 2
            },
            {
                text: "조선을 세운 왕은?",
                image: null,
                options: ["태조 이성계", "세종대왕", "광해군", "연산군"],
                answer: 1
            },
            {
                text: "신라의 삼국 통일에 기여한 왕은?",
                image: null,
                options: ["문무왕", "선덕여왕", "세종", "단군"],
                answer: 1
            }
        ]
    },

    3: {
        category: "고조선과 여러 나라",
        title: "고조선 8조법 퀴즈",
        questions: [
            {
                text: "고조선의 8조법 중 첫 번째 조항은?",
                image: null,
                options: [
                    "도둑질하면 사형",
                    "거짓말하면 벌금",
                    "세금 내지 않으면 처벌",
                    "술을 마시면 벌칙"
                ],
                answer: 1
            }
        ]
    }

    // ... 나머지도 필요할 때 추가
};

let currentIndex = 0;
let currentQuiz = null;
let userAnswers = []; // index별로 사용자가 선택한 답 저장

const editBtn = document.getElementById("edit-question-btn");
const editModal = document.getElementById("edit-modal");
const closeBtn = document.querySelector(".closeBtn");
const editForm = document.getElementById("edit-form");
const fileInput = document.getElementById("edit-question-file");
const previewImg = document.getElementById("preview-img");

function renderQuestion(question, currentIndex, total) {
    // 상단 카테고리명
    document.querySelector(".quiz-title").textContent = currentQuiz.category;

    // 문제 제목(퀴즈 제목)
    document.getElementById("question-title").textContent = currentQuiz.title;

    // 문제 번호
    document.getElementById("current-question").textContent = currentIndex + 1;
    document.getElementById("total-questions").textContent = total;

    // 실제 문제 텍스트
    document.getElementById("question-text").textContent = question.text;

    // 이미지 처리
    const imgBox = document.getElementById("question-image");
    if (question.image) {
        imgBox.style.display = "block";
        imgBox.querySelector("img").src = question.image;
    } else {
        imgBox.style.display = "none";
    }

    // 보기 표시
    const optionsBox = document.getElementById("options-box");
    optionsBox.innerHTML = "";

    question.options.forEach((opt, i) => {
        const optionIndex = i + 1;
        const checked = userAnswers[currentIndex] === optionIndex ? "checked" : "";
        let extraClass = "";

        let classes = [];

        if (reviewMode) {
            if (optionIndex === question.answer) {
                classes.push("correct-answer"); // 정답 → 파란색
            }
            if (userAnswers[currentIndex] === optionIndex && userAnswers[currentIndex] !== question.answer) {
                classes.push("wrong-answer"); // 내가 찍은 오답 → 빨간색
            }
        }

        optionsBox.innerHTML += `
        <label class="${classes.join(" ")}">
        <input type="radio" name="answer-${currentIndex}" value="${optionIndex}" ${checked} ${reviewMode ? "disabled" : ""}>
        <span>${opt}</span>
        </label><br>
        `;
    });

    // 라디오 클릭 시 선택 저장
    // 라디오 클릭 이벤트 (상세보기 모드에서는 막기)
    if (!reviewMode) {
        document.querySelectorAll(`input[name="answer-${currentIndex}"]`).forEach(input => {
            input.addEventListener("change", e => {
                userAnswers[currentIndex] = parseInt(e.target.value, 10);
            });
        });
    }

    // 제출 버튼 표시 여부
    // 제출 버튼 (상세보기 모드에서는 숨김)
    // 제출 버튼 / 목록 버튼 전환
    const submitBtn = document.getElementById("submit-btn");
    const backListBtn = document.getElementById("back-list-btn");

    if (reviewMode) {
        submitBtn.style.display = "none";
        // 마지막 문제일 때만 목록 버튼 보이기
        if (currentIndex === total - 1) {
            backListBtn.style.display = "block";
        } else {
            backListBtn.style.display = "none";
        }
    } else {
        backListBtn.style.display = "none";
        if (currentIndex === total - 1) {
            submitBtn.style.display = "block";
        } else {
            submitBtn.style.display = "none";
        }
    }

    document.getElementById("back-list-btn").addEventListener("click", () => {
        window.location.href = "/quiz/quizList.html";
    });

    // 진행률 갱신
    updateProgress(currentIndex, total);

    // 페이징 갱신
    renderPagination(total, currentIndex);

    // 자동 스크롤 (문제 바뀔 때 상단으로 이동)
    window.scrollTo({ top: 0, behavior: "smooth" });
}

// URL에서 quizId 추출
function getQuizIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id"); // 문자열로 반환됨
}

// 페이징 버튼
function renderPagination(total, currentIndex) {
    const paginationDiv = document.getElementById("pagination");
    paginationDiv.innerHTML = "";

    for (let i = 0; i < total; i++) {
        addPageButton(i, currentIndex, paginationDiv, total);
    }
}

function addPageButton(i, currentIndex, container, total) {
    const btn = document.createElement("button");
    btn.textContent = i + 1;

    if (i === currentIndex) {
        btn.classList.add("active");
    }

    btn.addEventListener("click", () => {
        currentIndex = i;
        renderQuestion(currentQuiz.questions[currentIndex], currentIndex, total);
        renderPagination(total, currentIndex);
    });

    container.appendChild(btn);
}

// 진행률
function updateProgress(currentIndex, total) {
    const pct = Math.round(((currentIndex + 1) / total) * 100);

    const fill = document.getElementById("progress-fill");
    const text = document.getElementById("progress-text");
    const bar = document.getElementById("progress-bar");

    // 채움 너비 & 접근성 값
    fill.style.width = pct + "%";
    bar.setAttribute("aria-valuenow", String(pct));

    // 안내 텍스트 (원하면 형식 바꿔도 됨)
    text.textContent = `문제 ${currentIndex + 1} / ${total} · ${pct}% 완료`;

    // (선택) 이동 느낌 강조: 300ms만 스트라이프 애니메이션
    fill.classList.add("animate");
    setTimeout(() => fill.classList.remove("animate"), 300);
}

// 제출 버튼 이벤트 연결
document.getElementById("submit-btn").addEventListener("click", () => {
    gradeQuiz();
});

function gradeQuiz() {
    const questions = currentQuiz.questions;
    const total = questions.length;
    let score = 0;
    let correctCount = 0;

    // 리뷰 리스트 초기화
    const reviewList = document.getElementById("review-list");
    reviewList.innerHTML = "";

    for (let i = 0; i < total; i++) {
        const q = questions[i];
        const userAnswer = userAnswers[i];

        if (!userAnswer) {
            alert(`문제 ${i + 1}을(를) 풀지 않았습니다.`);
            return;
        }

        if (userAnswer === q.answer) {
            score += 10;
            correctCount++;
            reviewList.innerHTML += `<li>문제 ${i + 1}: 정답 ✅ (${q.options[q.answer - 1]})</li>`;
        } else {
            reviewList.innerHTML += `<li>문제 ${i + 1}: 오답 ❌ (정답: ${q.options[q.answer - 1]})</li>`;
        }
    }

    // 결과 표시
    document.getElementById("total-score").textContent = score;
    document.getElementById("correct-count").textContent = `${correctCount} / ${total}`;
    document.getElementById("earned-points").textContent = score; // 점수 = 포인트 가정

    // 퀴즈 화면 숨기기
    document.querySelector(".quiz-wrapper").style.display = "none";

    // 결과 박스 보여주기
    document.getElementById("result-box").style.display = "block";
    window.scrollTo({ top: 0, behavior: "smooth" }); // 결과 화면 위로 이동
}

// 닫기 버튼 → 퀴즈 목록으로 이동
document.getElementById("close-btn").addEventListener("click", () => {
    window.location.href = "/quiz/quizList.html";
});

let reviewMode = false;

// 상세보기 버튼 → 정답/오답 표시 모드로 다시 퀴즈 화면 보여주기
document.getElementById("detail-btn").addEventListener("click", () => {
    reviewMode = true;  // 상세보기 모드 켜기
    document.getElementById("result-box").style.display = "none";

    const wrapper = document.querySelector(".quiz-wrapper");
    wrapper.style.display = "block";
    wrapper.classList.add("review-mode");

    // 첫 번째 문제부터 다시 표시
    currentIndex = 0;
    renderQuestion(currentQuiz.questions[currentIndex], currentIndex, currentQuiz.questions.length);
});

// 수정 버튼 열기
editBtn.addEventListener("click", () => {
  const q = currentQuiz.questions[currentIndex];

  document.getElementById("edit-question-text").value = q.text;

  // 미리보기 (이미지가 있으면 표시)
  if (q.image) {
    previewImg.src = q.image;
    previewImg.style.display = "block";
  } else {
    previewImg.style.display = "none";
  }

  q.options.forEach((opt, i) => {
    document.getElementById(`edit-option-${i+1}`).value = opt;
  });

  document.getElementById("edit-answer").value = q.answer;

  editModal.style.display = "flex";
});

// 닫기
closeBtn.addEventListener("click", () => {
  editModal.style.display = "none";
});

// 파일 선택 시 미리보기
fileInput.addEventListener("change", e => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = ev => {
      previewImg.src = ev.target.result;
      previewImg.style.display = "block";
    };
    reader.readAsDataURL(file);

    // 프론트 테스트용: 경로 대신 base64 임시 저장
    // urlInput.value = file.name; // 나중에 서버 업로드 연결 시 바꿔야 함
  }
});

// 저장
editForm.addEventListener("submit", e => {
  e.preventDefault();

  const q = currentQuiz.questions[currentIndex];
  q.text = document.getElementById("edit-question-text").value;

  // 업로드 vs URL 구분
  if (fileInput.files[0]) {
    // TODO: 서버 업로드 API 필요
    q.image = previewImg.src; // 지금은 base64로 반영 (프론트 테스트용)
  } else {
    q.image = q.image || ""; // 기존 이미지 유지, 없으면 빈 값
  }

  q.options = [
    document.getElementById("edit-option-1").value,
    document.getElementById("edit-option-2").value,
    document.getElementById("edit-option-3").value,
    document.getElementById("edit-option-4").value
  ];
  q.answer = parseInt(document.getElementById("edit-answer").value, 10);

  // 다시 렌더링
  renderQuestion(q, currentIndex, currentQuiz.questions.length);

  editModal.style.display = "none";

  // TODO: DB 연동 시 fetch("/api/update-quiz", { ... }) 추가
});
