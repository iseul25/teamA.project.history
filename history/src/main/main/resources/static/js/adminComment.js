// adminComment.js - 테이블 헤더 카테고리 드롭다운 버전

document.addEventListener('DOMContentLoaded', function() {
    initializeCategoryHeaderDropdown();
    initializeAlertMessages();
    initializeTableEffects();
    initializePagination();
});

// 테이블 헤더 카테고리 드롭다운 초기화
function initializeCategoryHeaderDropdown() {
    const categoryHeader = document.getElementById("categoryHeader");
    const categoryTableMenu = document.getElementById("categoryTableMenu");
    const arrow = categoryHeader ? categoryHeader.querySelector(".dropdown-arrow") : null;

    if (!categoryHeader || !categoryTableMenu) return;

    // 드롭다운 열기/닫기
    categoryHeader.addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();
        toggleTableDropdown();
    });

    // 카테고리 메뉴 클릭 처리
    categoryTableMenu.addEventListener("click", function(e) {
        let targetElement = e.target;
        while (targetElement && targetElement.tagName !== 'A') {
            targetElement = targetElement.parentElement;
            if (!categoryTableMenu.contains(targetElement)) {
                targetElement = null;
                break;
            }
        }
        if (targetElement && targetElement.tagName === 'A') {
            closeTableDropdown();
            const href = targetElement.getAttribute('href');
            if (href) window.location.href = href;
        }
        e.stopPropagation();
    });

    const categoryLinks = categoryTableMenu.querySelectorAll('a');
    categoryLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            closeTableDropdown();
            const href = this.getAttribute('href');
            if (href && href !== '#') window.location.href = href;
        });
    });

    // 문서 클릭 시 닫기
    document.addEventListener("click", function(e) {
        if (!categoryHeader.contains(e.target) && !categoryTableMenu.contains(e.target)) {
            closeTableDropdown();
        }
    });

    // ESC 키로 닫기
    document.addEventListener("keydown", function(e) {
        if (e.key === "Escape") closeTableDropdown();
    });

    function toggleTableDropdown() {
        const isOpen = categoryTableMenu.classList.contains("show");
        isOpen ? closeTableDropdown() : openTableDropdown();
    }

    function openTableDropdown() {
        categoryTableMenu.classList.add("show");
        if (arrow) arrow.classList.add("up");
        categoryHeader.setAttribute("aria-expanded", "true");
    }

    function closeTableDropdown() {
        categoryTableMenu.classList.remove("show");
        if (arrow) arrow.classList.remove("up");
        categoryHeader.setAttribute("aria-expanded", "false");
    }
}

// 페이징 버튼 초기화
function initializePagination() {
    const pageNavButtons = document.querySelectorAll('.page-nav-btn');
    pageNavButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            const category = button.getAttribute('data-category');
            const page = button.getAttribute('data-page');
            if (category && page) {
                let url = '/admin/comment?page=' + page;
                if (category !== '전체' && category !== null && category !== '') {
                    url += '&category=' + encodeURIComponent(category);
                }
                window.location.href = url;
            }
        });
    });
}

// 알림 메시지 초기화
function initializeAlertMessages() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() { fadeOutAlert(alert); }, 5000);
        alert.addEventListener('click', function() { fadeOutAlert(alert); });
    });
}

function fadeOutAlert(alert) {
    alert.style.transition = 'opacity 0.5s ease';
    alert.style.opacity = '0';
    setTimeout(function() {
        if (alert.parentNode) alert.parentNode.removeChild(alert);
    }, 500);
}

// 테이블 효과 초기화
function initializeTableEffects() {
    const commentRows = document.querySelectorAll('.comment-row');
    commentRows.forEach(function(row) {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#e8f4fd';
            this.style.transform = 'translateX(2px)';
            this.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
            this.style.transition = 'all 0.2s ease';
        });
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
            this.style.transform = '';
            this.style.boxShadow = '';
        });
    });

    const replyRows = document.querySelectorAll('.reply-row');
    replyRows.forEach(function(row) {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f0f8ff';
            this.style.transform = 'translateX(4px)';
            this.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
            this.style.transition = 'all 0.2s ease';
        });
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '#f8f9fa';
            this.style.transform = '';
            this.style.boxShadow = '';
        });
    });
}

// 유틸리티 함수
const AdminCommentUtils = {
    getUrlParameter: function(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    },
    formatDate: function(date) {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    },
    truncateText: function(text, maxLength) {
        if (!text || text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    },
    getRepliesForComment: function(commentRow) {
        const replies = [];
        let nextRow = commentRow.nextElementSibling;
        while (nextRow && nextRow.classList.contains('reply-row')) {
            replies.push(nextRow);
            nextRow = nextRow.nextElementSibling;
        }
        return replies;
    }
};

window.AdminCommentUtils = AdminCommentUtils;
