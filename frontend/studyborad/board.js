
// 게시글 로컬스토리지에서 가져오기
function getPosts() {
    return JSON.parse(localStorage.getItem('posts') || '[]');
}

// 날짜 표기
function getLocalDateString() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 유튜브 ID 추출
function extractYoutubeId(url) {
    const regExp = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&]+)/;
    const match = url.match(regExp);
    return match ? match[1] : '';
}

// 게시글 렌더링
function renderPost() {
    const params = new URLSearchParams(window.location.search);
    const idParam = params.get('id');
    if (!idParam) {
        document.body.innerHTML = '<p>게시글 ID가 없습니다.</p>';
        return;
    }
    const id = Number(idParam);
    const post = getPosts().find((p) => p.id === id);

    if (!post) {
        document.body.innerHTML = '<p>존재하지 않는 글입니다.</p>';
        return;
    }

    document.getElementById('post-title').textContent = post.title;
    document.getElementById('post-content').textContent = post.content;

    if (post.videoId) {
        const iframe = document.createElement('iframe');
        iframe.width = "800";
        iframe.height = "450";
        iframe.src = `https://www.youtube.com/embed/${post.videoId}`;
        iframe.title = "YouTube video player";
        iframe.frameBorder = "0";
        iframe.allow = "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture";
        iframe.allowFullscreen = true;
        iframe.style.display = 'block';
        iframe.style.margin = '20px auto';

        document.getElementById('post-content').appendChild(document.createElement('br'));
        document.getElementById('post-content').appendChild(iframe);
    }
}

// 게시글 저장
function savePost() {
    const title = document.getElementById('title').value.trim();
    const author = document.getElementById('author').value.trim();
    const content = document.getElementById('content').value.trim();
    const videoUrl = document.getElementById('videoUrl').value.trim();

    const dateOnly = getLocalDateString();
    const id = Date.now(); // 숫자로 id 생성
    const videoId = extractYoutubeId(videoUrl);

    const newPost = { id, title, author, content, date: dateOnly, videoUrl, videoId };

    const posts = getPosts();
    posts.push(newPost);
    localStorage.setItem('posts', JSON.stringify(posts));
}

function renderPosts(page) {
    const posts = getPosts();
    const postsPerPage = 10;
    const totalPages = Math.ceil(posts.length / postsPerPage);
    const startIndex = (page - 1) * postsPerPage;
    const endIndex = startIndex + postsPerPage;
    const currentPosts = posts.slice(startIndex, endIndex);

    const list = document.getElementById('post-list');
    list.innerHTML = '';

    currentPosts.forEach((post, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${startIndex + index + 1}</td>
            <td><a href="view.html?id=${post.id}">${post.title}</a></td>
            <td>${post.date}</td>
            <td>${post.author}</td>
        `;
        list.appendChild(tr);
    });

    // 페이지 정보 표시
    const pageInfo = document.getElementById('page-info');
    if (pageInfo) pageInfo.textContent = `${page} / ${totalPages}`;

    // 버튼 설정
    const prevBtn = document.getElementById('prev-page');
    const nextBtn = document.getElementById('next-page');

    if (prevBtn) {
        prevBtn.disabled = page === 1;
        prevBtn.onclick = () => renderPosts(page - 1);
    }

    if (nextBtn) {
        nextBtn.disabled = page === totalPages;
        nextBtn.onclick = () => renderPosts(page + 1);
    }
}



// 게시글 삭제
function deletePost() {
    const params = new URLSearchParams(window.location.search);
    const id = Number(params.get('id'));

    let posts = getPosts();
    posts = posts.filter(post => post.id !== id);

    localStorage.setItem('posts', JSON.stringify(posts));

    const comments = JSON.parse(localStorage.getItem('comments') || '{}');
    delete comments[id];
    localStorage.setItem('comments', JSON.stringify(comments));

    alert('글이 삭제되었습니다.');
    window.location.href = 'index.html';
}

// 댓글 관련 함수
function getComments(postId) {
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    return allComments[String(postId)] || [];
}

function saveComment(postId, { author, content, date }) {
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    if (!allComments[postId]) {
        allComments[postId] = [];
    }
    allComments[postId].push({ author, content, date });
    localStorage.setItem('comments', JSON.stringify(allComments));
}

function renderComments(postId) {
    const commentList = document.getElementById('comment-list');
    commentList.innerHTML = '';

    const comments = getComments(postId);

    if (comments.length === 0) {
        const li = document.createElement('li');
        li.textContent = '댓글이 없습니다.';
        commentList.appendChild(li);
        return;
    }

    comments.forEach((comment, index) => {
        const li = document.createElement('li');
        li.innerHTML = `
            <strong>${comment.author}</strong> <small>${comment.date}</small>
            <p>${comment.content}</p>
            <button onclick="editComment(${index})">수정</button>
            <button onclick="deleteComment(${index})">삭제</button>
        `;
        commentList.appendChild(li);
    });
}

function editComment(index) {
    const postId = String(new URLSearchParams(window.location.search).get('id'));
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    const comments = allComments[postId] || [];

    const newContent = prompt("댓글 내용을 수정하세요:", comments[index].content);
    if (newContent !== null && newContent.trim() !== '') {
        comments[index].content = newContent.trim();
        comments[index].date = new Date().toLocaleString();
        allComments[postId] = comments;
        localStorage.setItem('comments', JSON.stringify(allComments));
        renderComments(postId);
    }
}

function deleteComment(index) {
    const postId = String(new URLSearchParams(window.location.search).get('id'));
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    const comments = allComments[postId] || [];

    if (confirm('이 댓글을 삭제하시겠습니까?')) {
        comments.splice(index, 1);
        allComments[postId] = comments;
        localStorage.setItem('comments', JSON.stringify(allComments));
        renderComments(postId);
    }
}

// DOMContentLoaded 이벤트 등록 함수 (초기 실행)
function initialize() {
    const params = new URLSearchParams(window.location.search);
    const postId = params.get('id');

    renderPost();
    renderComments(postId);

    document.getElementById('comment-form').addEventListener('submit', (e) => {
        e.preventDefault();

        const authorInput = document.getElementById('comment-author');
        const contentInput = document.getElementById('comment-content');

        const author = authorInput.value.trim();
        const content = contentInput.value.trim();

        if (!author || !content) {
            alert('작성자와 내용을 입력해주세요.');
            return;
        }

        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const date = `${year}-${month}-${day}`;

        saveComment(postId, { author, content, date });

        authorInput.value = '';
        contentInput.value = '';

        renderComments(postId);
    });

    // 수정 버튼 이벤트
    document.getElementById('edit-btn').addEventListener('click', () => {
        window.location.href = `edit.html?id=${postId}`;
    });

    // 삭제 버튼 이벤트
    document.getElementById('delete-btn').addEventListener('click', () => {
        if (confirm('이 글을 삭제하시겠습니까?')) {
            deletePost();
        }
    });
}

// 초기화 함수 실행
document.addEventListener('DOMContentLoaded', () => {
    const path = window.location.pathname;

    // view.html에서만 initialize() 실행
    if (path.includes('view.html')) {
        initialize();
    }
});

document.addEventListener('DOMContentLoaded', () => {
    renderPosts(1); // 첫 페이지부터 표시
});