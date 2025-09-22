async function checkLogin() {
  try {
    const response = await fetch("/api/check-login", {
      credentials: "include"
    });
    const data = await response.json();

    const userDiv = document.getElementById("user");
    const usernameLink = document.getElementById("userName-link");
    const userPointSpan = document.getElementById("userPoint");

    if (data.isLoggedIn) {
      if (data.role === "admin") {
        // 관리자 계정
        usernameLink.textContent = "관리자";
        usernameLink.href = "/admin/dashboard.html"; // 관리자 전용 페이지
        userPointSpan.textContent = ""; // 관리자에선 포인트 안 보이게
      } else {
        // 일반 유저 계정
        usernameLink.textContent = data.username;
        usernameLink.href = "/mypage/mypage.html"; // 마이페이지

        // 포인트 표시
        userPointSpan.textContent = ` |  ${data.point} P`;
      }
      userDiv.style.display = "block";
    } else {
      userDiv.style.display = "none";  // 로그인 안 했을 때
    }
  } catch (err) {
    console.error("로그인 체크 에러:", err);
  }
}

// 전역 변수
const pageSize = 8; // 한 페이지에 보여줄 상품 개수
let currentPage = 1;
let currentCategory = "drink"; // 기본 카테고리
let currentBrand = "all";      // 기본 브랜드 (전체)

// 카테고리별 브랜드 목록
const categoryInfo = {
  "drink": {
    title: "음료",
    brands: [
      { key: "all", label: "전체" },
      { key: "compose", label: "컴포즈" },
      { key: "mega", label: "메가커피" },
      { key: "starbucks", label: "스타벅스" },
      { key: "ediya", label: "이디야" },
      { key: "twosome", label: "투썸플레이스" }
    ]
  },
  "C-store": {
    title: "편의점",
    brands: [
      { key: "all", label: "전체" },
      { key: "cu", label: "CU" },
      { key: "gs25", label: "GS25" },
      { key: "7-Eleven", label: "세븐일레븐" },
      { key: "emart24", label: "이마트24" }
    ]
  },
  "movie": {
    title: "영화",
    brands: [
      { key: "all", label: "전체" },
      { key: "cgv", label: "CGV" },
      { key: "Megabox", label: "메가박스" },
      { key: "Lotte", label: "롯데시네마" }
    ]
  },
  "gift-card": {
    title: "상품권",
    brands: [
      { key: "all", label: "전체" },
      { key: "Kyobo", label: "교보문고" },
      { key: "culture", label: "컬쳐랜드" },
      { key: "Booknlife", label: "북앤라이프" }
    ]
  }
};

// 브랜드별 항목
const products = [
  // 음료
  { category: "drink", brand: "compose", name: "컴포즈 커피 5천원권", price: "1000P", img: "img/c5.png" },
  { category: "drink", brand: "compose", name: "컴포즈 커피 1만원권", price: "2000P", img: "img/c10.png" },
  { category: "drink", brand: "compose", name: "컴포즈 커피 2만원권", price: "3000P", img: "img/c20.png" },
  { category: "drink", brand: "compose", name: "컴포즈 커피 3만원권", price: "4000P", img: "img/c30.png" },
  { category: "drink", brand: "compose", name: "컴포즈 커피 5만원권", price: "5000P", img: "img/c50.png" },

  { category: "drink", brand: "mega", name: "메가커피 5천원권", price: "1000P", img: "img/m5.png" },
  { category: "drink", brand: "mega", name: "메가커피 1만원권", price: "2000P", img: "img/m10.png" },
  { category: "drink", brand: "mega", name: "메가커피 2만원권", price: "3000P", img: "img/m20.png" },
  { category: "drink", brand: "mega", name: "메가커피 3만원권", price: "4000P", img: "img/m30.png" },
  { category: "drink", brand: "mega", name: "메가커피 5만원권", price: "5000P", img: "img/m50.png" },

  { category: "drink", brand: "starbucks", name: "스타벅스 2만원권", price: "3000P", img: "img/s20.png" },
  { category: "drink", brand: "starbucks", name: "스타벅스 3만원권", price: "4000P", img: "img/s30.png" },
  { category: "drink", brand: "starbucks", name: "스타벅스 5만원권", price: "5000P", img: "img/s50.png" },

  { category: "drink", brand: "ediya", name: "이디야 5천원권", price: "1000P", img: "img/e5.png" },
  { category: "drink", brand: "ediya", name: "이디야 1만원권", price: "2000P", img: "img/e10.png" },
  { category: "drink", brand: "ediya", name: "이디야 2만원권", price: "3000P", img: "img/e20.png" },
  { category: "drink", brand: "ediya", name: "이디야 3만원권", price: "4000P", img: "img/e30.png" },
  { category: "drink", brand: "ediya", name: "이디야 5만원권", price: "5000P", img: "img/e50.png" },

  { category: "drink", brand: "twosome", name: "투썸플레이스 2만원권", price: "3000P", img: "img/t20.png" },
  { category: "drink", brand: "twosome", name: "투썸플레이스 3만원권", price: "4000P", img: "img/t30.png" },
  { category: "drink", brand: "twosome", name: "투썸플레이스 5만원권", price: "5000P", img: "img/t50.png" },

  // 편의점
  { category: "C-store", brand: "cu", name: "cu 1천원권", price: "300P", img: "img/cu1.png" },
  { category: "C-store", brand: "cu", name: "cu 2천원권", price: "400P", img: "img/cu2.png" },
  { category: "C-store", brand: "cu", name: "cu 3천원권", price: "500P", img: "img/cu3.png" },
  { category: "C-store", brand: "cu", name: "cu 5천원권", price: "1000P", img: "img/cu5.png" },
  { category: "C-store", brand: "cu", name: "cu 1만원권", price: "2000P", img: "img/cu10.png" },
  { category: "C-store", brand: "cu", name: "cu 2만원권", price: "3000P", img: "img/cu20.png" },
  { category: "C-store", brand: "cu", name: "cu 3만원권", price: "4000P", img: "img/cu30.png" },
  { category: "C-store", brand: "cu", name: "cu 5만원권", price: "5000P", img: "img/cu50.png" },

  { category: "C-store", brand: "gs25", name: "gs25 1천원권", price: "300P", img: "img/gs251.png" },
  { category: "C-store", brand: "gs25", name: "gs25 2천원권", price: "400P", img: "img/gs252.png" },
  { category: "C-store", brand: "gs25", name: "gs25 3천원권", price: "500P", img: "img/gs253.png" },
  { category: "C-store", brand: "gs25", name: "gs25 5천원권", price: "1000P", img: "img/gs255.png" },
  { category: "C-store", brand: "gs25", name: "gs25 1만원권", price: "2000P", img: "img/gs2510.png" },
  { category: "C-store", brand: "gs25", name: "gs25 2만원권", price: "3000P", img: "img/gs2520.png" },
  { category: "C-store", brand: "gs25", name: "gs25 3만원권", price: "4000P", img: "img/gs2530.png" },
  { category: "C-store", brand: "gs25", name: "gs25 5만원권", price: "5000P", img: "img/gs2550.png" },

  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 1천원권", price: "300P", img: "img/7-Eleven1.png" },
  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 3천원권", price: "500P", img: "img/7-Eleven3.png" },
  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 5천원권", price: "1000P", img: "img/7-Eleven5.png" },
  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 1만원권", price: "2000P", img: "img/7-Eleven10.png" },
  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 3만원권", price: "4000P", img: "img/7-Eleven30.png" },
  { category: "C-store", brand: "7-Eleven", name: "세븐일레븐 5만원권", price: "5000P", img: "img/7-Eleven50.png" },

  { category: "C-store", brand: "emart24", name: "이마트24 1천원권", price: "300P", img: "img/emart241.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 2천원권", price: "400P", img: "img/emart242.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 3천원권", price: "500P", img: "img/emart243.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 5천원권", price: "1000P", img: "img/emart245.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 1만원권", price: "2000P", img: "img/emart2410.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 2만원권", price: "3000P", img: "img/emart2420.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 3만원권", price: "4000P", img: "img/emart2430.png" },
  { category: "C-store", brand: "emart24", name: "이마트24 5만원권", price: "5000P", img: "img/emart2450.png" },

  // 영화
  { category: "movie", brand: "cgv", name: "CGV 관람권 1매", price: "5000P", img: "img/cgv1.png" },
  { category: "movie", brand: "cgv", name: "CGV 4DX 관람권 1매", price: "5000P", img: "img/cgv2.png" },
  { category: "movie", brand: "cgv", name: "CGV 1인 패키지", price: "5000P", img: "img/cgv4.png" },
  { category: "movie", brand: "cgv", name: "CGV 2인 패키지", price: "5000P", img: "img/cgv3.png" },
  { category: "movie", brand: "cgv", name: "CGV 콤보", price: "5000P", img: "img/cgv5.png" },
  { category: "movie", brand: "cgv", name: "CGV 기프트카드 5만원권", price: "5000P", img: "img/cgv50.png" },

  { category: "movie", brand: "Megabox", name: "메가박스 2만원권", price: "5000P", img: "img/Megabox20.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 3만원권", price: "5000P", img: "img/Megabox30.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 5만원권", price: "5000P", img: "img/Megabox50.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 관람권 1매", price: "5000P", img: "img/Megabox3.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 관람권 2매", price: "5000P", img: "img/Megabox2.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 1인 패키지", price: "5000P", img: "img/Megabox4.png" },
  { category: "movie", brand: "Megabox", name: "메가박스 2인 패키지", price: "5000P", img: "img/Megabox1.png" },

  { category: "movie", brand: "Lotte", name: "롯데시네마 관람권 1매", price: "5000P", img: "img/Lotte2.png" },
  { category: "movie", brand: "Lotte", name: "롯데시네마 관람권 2매", price: "5000P", img: "img/Lotte1.png" },
  { category: "movie", brand: "Lotte", name: "롯데시네마 더블 콤보", price: "5000P", img: "img/Lotte4.png" },
  { category: "movie", brand: "Lotte", name: "롯데시네마 스위트 콤보", price: "5000P", img: "img/Lotte3.png" },

  // 상품권
  { category: "gift-card", brand: "Kyobo", name: "교보문고 전자책 1만원권", price: "2000P", img: "img/Kyobo10.png" },
  { category: "gift-card", brand: "Kyobo", name: "교보문고 전자책 2만원권", price: "3000P", img: "img/Kyobo30.png" },
  { category: "gift-card", brand: "Kyobo", name: "교보문고 전자책 3만원권", price: "4000P", img: "img/Kyobo50.png" },
  { category: "gift-card", brand: "Kyobo", name: "교보문고 1만원권", price: "2000P", img: "img/Kyobo-10.png" },
  { category: "gift-card", brand: "Kyobo", name: "교보문고 2만원권", price: "3000P", img: "img/Kyobo-30.png" },
  { category: "gift-card", brand: "Kyobo", name: "교보문고 3만원권", price: "4000P", img: "img/Kyobo-50.png" },

  { category: "gift-card", brand: "culture", name: "컬쳐랜드 5천원권", price: "1000P", img: "img/culture5.png" },
  { category: "gift-card", brand: "culture", name: "컬쳐랜드 1만원권", price: "2000P", img: "img/culture10.png" },
  { category: "gift-card", brand: "culture", name: "컬쳐랜드 2만원권", price: "3000P", img: "img/culture20.png" },
  { category: "gift-card", brand: "culture", name: "컬쳐랜드 3만원권", price: "4000P", img: "img/culture30.png" },
  { category: "gift-card", brand: "culture", name: "컬쳐랜드 5만원권", price: "5000P", img: "img/culture50.png" },

  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 1천원권", price: "300P", img: "img/Booknlife1.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 3천원권", price: "500P", img: "img/Booknlife3.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 5천원권", price: "1000P", img: "img/Booknlife5.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 1만원권", price: "2000P", img: "img/Booknlife10.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 2만원권", price: "3000P", img: "img/Booknlife20.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 3만원권", price: "4000P", img: "img/Booknlife30.png" },
  { category: "gift-card", brand: "Booknlife", name: "북앤라이프 5만원권", price: "5000P", img: "img/Booknlife50.png" }
];

function renderTitle() {
  const titleBox = document.querySelector(".product-title");
  titleBox.innerHTML = ""; // 초기화

  const info = categoryInfo[currentCategory];
  if (!info) return;

  // 제목 + 브랜드 목록 ul 생성
  const ul = document.createElement("ul");

  // 메인 타이틀
  const mainLi = document.createElement("li");
  mainLi.classList.add("main-title");
  mainLi.textContent = info.title;
  ul.appendChild(mainLi);

  // 브랜드 목록
  info.brands.forEach(b => {
    const li = document.createElement("li");
    const a = document.createElement("a");
    a.href = "#";
    a.classList.add("brand-link");
    a.dataset.brand = b.key;
    a.textContent = b.label;

    // 현재 선택된 브랜드 강조
    if (currentBrand === b.key) {
      a.classList.add("active");
    }

    li.appendChild(a);
    ul.appendChild(li);
  });

  titleBox.appendChild(ul);

  // 새로 만들어진 brand-link에 다시 이벤트 바인딩
  titleBox.querySelectorAll(".brand-link").forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      currentBrand = link.dataset.brand;
      renderTitle();
      renderTable(1);
    });
  });
}

// 브랜드 상품 렌더링
function renderTable(page = 1) {
  const container = document.querySelector(".product-list");
  container.innerHTML = ""; // 초기화

  // 카테고리 + 브랜드 조건으로 필터링
  let filtered = products.filter(p =>
    p.category === currentCategory &&
    (currentBrand === "all" || p.brand === currentBrand)
  );

  // 보여줄 상품 범위 계산
  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  const itemsToShow = filtered.slice(start, end);

  // DOM 생성해서 추가
  itemsToShow.forEach(p => {
    const card = document.createElement("div");
    card.classList.add("product-card");
    card.dataset.category = p.category;
    card.dataset.brand = p.brand;

    card.innerHTML = `
      <img src="${p.img}" alt="${p.name}">
      <div class="product-name-box">
        <p class="product-name">${p.name}</p>
        <p class="product-price">${p.price}</p>
        <button class="buy-btn" data-price="${p.price}" data-name="${p.name}">구매하기</button>
      </div>
    `;
    container.appendChild(card);
  });

  // 구매 버튼 이벤트 바인딩
  document.querySelectorAll(".buy-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      // 상품 정보
      const priceText = btn.dataset.price.replace("P", "");
      const price = parseInt(priceText);
      const name = btn.dataset.name;

      // 유저 포인트 (실제는 DB에서 가져옴)
      const userPointText = document.getElementById("userPoint").textContent.replace("|", "").replace("P", "").trim();
      let userPoint = parseInt(userPointText) || 0;

      if (userPoint >= price) {
        alert(`'${name}' 상품을 구매하시겠습니까? (차감 포인트: ${price}P)`);

        // 프론트 테스트용 포인트 차감 (실제는 백엔드에서 처리 후 다시 반영)
        userPoint -= price;
        document.getElementById("userPoint").textContent = `| ${userPoint} P`;

        // TODO: 백엔드 연동 시 fetch로 구매 요청 보내기
        /*
        fetch("/api/purchase", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ productName: name, price })
        })
        .then(res => res.json())
        .then(data => {
          if(data.success) {
            // 포인트 업데이트
          }
        });
        */
      } else {
        alert("보유 포인트가 부족하여 상품을 구매하실 수 없습니다.");
      }
    });
  });

  // 페이징 다시 그리기
  renderPagination(filtered.length, page);

  // 현재 페이지 저장
  currentPage = page;
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

// 이벤트 바인딩
function bindEvents() {
  // 사이드바 카테고리 클릭
  document.querySelectorAll(".category-link").forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      currentCategory = link.dataset.category;
      currentBrand = "all"; // 카테고리 바뀌면 브랜드는 전체로 초기화
      renderTitle();
      renderTable(1);
    });
  });

  // 브랜드 필터 클릭
  document.querySelectorAll(".brand-link").forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      currentBrand = link.dataset.brand;
      renderTable(1);
    });
  });
}

// 페이지 로드시 실행
window.onload = function () {
  checkLogin();
  bindEvents();
  renderTitle();
  renderTable(1); // 첫 페이지 출력
};
