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
    // 첫 페이지 게시글 로드
    renderProducts(1);  // 상품 목록
    renderOrders();     // 주문 목록
};

// 상품 관리 -----------------
// 상품 더미 데이터 (DB 연동 전 테스트용)
let products = [
    { id: 1, name: "투썸플레이스 쿠폰", price: 50, image: "coffee.png" },
    { id: 2, name: "문화상품권 100", price: 100, image: "culture100.png" },
    { id: 3, name: "문화상품권 300", price: 300, image: "culture300.png" },
    { id: 4, name: "문화상품권 500", price: 500, image: "culture500.png" },
    { id: 5, name: "스타벅스 쿠폰", price: 50, image: "starbucks.png" },
    { id: 6, name: "이디야 쿠폰", price: 50, image: "ediya.png" },
    { id: 7, name: "CGV 영화관람권", price: 500, image: "cgv.png" }
];

// 한 페이지당 5개
const pageSize = 5;
let currentPage = 1;

const productBody = document.getElementById("product-body");

// 상품 목록 렌더링
function renderProducts(page = 1) {
    currentPage = page;
    productBody.innerHTML = "";

    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const pageProducts = products.slice(start, end);

    pageProducts.forEach(product => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${product.id}</td>
      <td>${product.name}</td>
      <td>${product.price} 점</td>
      <td>
        <button class="editBtn" data-id="${product.id}">수정</button>
        <button class="deleteBtn" data-id="${product.id}">삭제</button>
      </td>
    `;
        productBody.appendChild(tr);
    });

    // 남은 빈 행 채우기
    const emptyRows = pageSize - pageProducts.length;
    for (let i = 0; i < emptyRows; i++) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>&nbsp;</td>
          <td></td>
          <td></td>
          <td></td>
        `;
        productBody.appendChild(tr);
    }

    // 이벤트 다시 연결
    document.querySelectorAll(".editBtn").forEach(btn => {
        btn.addEventListener("click", () => openEditModal(btn.dataset.id));
    });

    document.querySelectorAll(".deleteBtn").forEach(btn => {
        btn.addEventListener("click", () => deleteProduct(btn.dataset.id));
    });

    // 페이징 다시 렌더링
    renderPagination(products.length, currentPage);
}

// 상품 모달 관련
const editModal = document.getElementById("editProductModal");
const closeBtn = editModal.querySelector(".closeBtn");
const editForm = document.getElementById("editProductForm");

let editingProductId = null;

// 수정 모달 열기
function openEditModal(id) {
    const product = products.find(p => p.id == id);
    if (!product) return;

    editingProductId = id;
    document.getElementById("editProductName").value = product.name;
    document.getElementById("editProductPrice").value = product.price;

    editModal.classList.add("show");
}

// 수정 모달 닫기
function closeEditModal() {
    editModal.classList.remove("show");
}

// 상품 수정 완료
editForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const name = document.getElementById("editProductName").value;
    const price = document.getElementById("editProductPrice").value;

    const product = products.find(p => p.id == editingProductId);
    if (product) {
        product.name = name;
        product.price = price;

        // 이미지 파일 수정 처리 (테스트용: 파일 이름만 반영)
        const imageFile = document.getElementById("editProductImage").files[0];
        if (imageFile) {
            product.image = imageFile.name;
        }
    }

    renderProducts();
    closeEditModal();
});

// 상품 삭제 기능
function deleteProduct(id) {
    const product = products.find(p => p.id == id);
    if (!product) return;

    // 안내 문구 띄우기
    const confirmDelete = confirm(`${product.id}번 상품(${product.name})을 삭제하시겠습니까?`);

    if (confirmDelete) {
        products = products.filter(p => p.id != id);
        renderProducts();
    }
}

// 닫기 버튼
closeBtn.addEventListener("click", closeEditModal);

// 페이징 버튼
function renderPagination(totalItems, currentPage) {
    const paginationDiv = document.getElementById("pagination");
    paginationDiv.innerHTML = "";

    const totalPages = Math.ceil(totalItems / pageSize);

    // ◀ 이전 버튼
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "◀";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => renderProducts(currentPage - 1));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");
        btn.addEventListener("click", () => renderProducts(i));
        paginationDiv.appendChild(btn);
    }

    // ▶ 다음 버튼
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "▶";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => renderProducts(currentPage + 1));
    paginationDiv.appendChild(nextBtn);
}


// 주문 관리 -----------------
// 주문 더미 데이터 (DB 연동 전 테스트용)
let orders = [
    { id: 1, username: "홍길동", email: "hong@test.com", productName: "스타벅스 쿠폰", orderDate: "2025-09-15", confirmed: false },
    { id: 2, username: "이순신", email: "lee@test.com", productName: "CGV 영화관람권", orderDate: "2025-09-16", confirmed: false },
    { id: 3, username: "강감찬", email: "kang@test.com", productName: "문화상품권 100", orderDate: "2025-09-17", confirmed: false },
    { id: 4, username: "을지문덕", email: "ulji@test.com", productName: "이디야 쿠폰", orderDate: "2025-09-17", confirmed: false },
    { id: 5, username: "세종대왕", email: "sejong@test.com", productName: "문화상품권 500", orderDate: "2025-09-17", confirmed: false },
    { id: 6, username: "신사임당", email: "shin@test.com", productName: "투썸플레이스 쿠폰", orderDate: "2025-09-18", confirmed: false }
];

const orderBody = document.getElementById("order-body");

const orderPageSize = 5;
let orderCurrentPage = 1;

// 주문 목록 렌더링
function renderOrders(page = 1) {
    orderCurrentPage = page;
    orderBody.innerHTML = "";

    // 주문일자 기준으로 내림차순 정렬 (최신 주문이 위로)
    const sortedOrders = [...orders].sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));

    const start = (page - 1) * orderPageSize;
    const end = start + orderPageSize;
    const pageOrders = sortedOrders.slice(start, end);

    // 최신 주문이 위로 → 번호는 전체 길이부터 시작
    let number = sortedOrders.length - start;

    pageOrders.forEach(order => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${number--}</td>
            <td>${order.username}</td>
            <td>${order.email}</td>
            <td>${order.productName}</td>
            <td>${order.orderDate}</td>
            <td>
                <button class="confirmBtn" data-id="${order.id}" ${order.confirmed ? "disabled" : ""}>
                    ${order.confirmed ? "완료" : "확인"}
                </button>
            </td>
        `;
        orderBody.appendChild(tr);
    });

    // 빈 행 채우기
    const emptyRows = orderPageSize - pageOrders.length;
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
        orderBody.appendChild(tr);
    }

    // 확인 버튼 이벤트 연결
    document.querySelectorAll(".confirmBtn").forEach(btn => {
        btn.addEventListener("click", async function () {
            const orderId = this.dataset.id;
            try {
                // 실제 DB 연동 시 API 호출
                // await fetch(`/api/orders/${orderId}/confirm`, { method: "PUT" });

                alert("상품을 전달했습니다.");
                this.textContent = "완료";
                this.disabled = true;

                // 실제 데이터 업데이트
                const order = orders.find(o => o.id == orderId);
                if (order) order.confirmed = true;
            } catch (err) {
                console.error("확인 버튼 에러:", err);
            }
        });
    });

    renderOrderPagination(sortedOrders.length, orderCurrentPage);
}

// 주문 페이징
function renderOrderPagination(totalItems, currentPage) {
    let paginationDiv = document.getElementById("order-pagination");

    // 없으면 새로 생성 (상품 페이징과 구분됨)
    if (!paginationDiv) {
        paginationDiv = document.createElement("div");
        paginationDiv.id = "order-pagination";
        paginationDiv.className = "pagination";
        document.getElementById("orderBox").appendChild(paginationDiv);
    }

    paginationDiv.innerHTML = "";

    const totalPages = Math.ceil(totalItems / orderPageSize);

    // ◀ 이전 버튼
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "◀";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => renderOrders(currentPage - 1));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");
        btn.addEventListener("click", () => renderOrders(i));
        paginationDiv.appendChild(btn);
    }

    // ▶ 다음 버튼
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "▶";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => renderOrders(currentPage + 1));
    paginationDiv.appendChild(nextBtn);
}
