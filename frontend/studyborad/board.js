// 게시글 목록 가져오기
function getPosts() {
    return JSON.parse(localStorage.getItem('posts') || '[]');
}

// 날짜 형식 반환 (YYYY-MM-DD)
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

// 게시글 저장 (새 글 작성용)
function savePost() {
    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();
    const videoUrl = document.getElementById('videoUrl').value.trim();
    const dateOnly = getLocalDateString();
    const id = Date.now();
    const videoId = extractYoutubeId(videoUrl);

    if (!title || !content) {
        alert('제목과 내용을 입력해주세요.');
        return;
    }

    const newPost = { id, title, content, date: dateOnly, videoUrl, videoId };
    const posts = getPosts();
    posts.push(newPost);
    localStorage.setItem('posts', JSON.stringify(posts));

    alert('게시글이 저장되었습니다.');
    window.location.href = 'index.html';
}

// 게시글 목록 렌더링 (페이징 포함)
function renderPosts(page = 1) {
    const posts = getPosts();
    const postsPerPage = 10;
    const totalPages = Math.max(1, Math.ceil(posts.length / postsPerPage));
    const startIndex = (page - 1) * postsPerPage;
    const currentPosts = posts.slice(startIndex, startIndex + postsPerPage);

    const list = document.getElementById('post-list');
    if (!list) return;

    list.innerHTML = '';
    currentPosts.forEach((post, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${startIndex + index + 1}</td>
            <td><a href="view.html?id=${post.id}">${post.title}</a></td>
            <td>${post.date}</td>
        `;
        list.appendChild(tr);
    });

    const pageInfo = document.getElementById('page-info');
    if (pageInfo) pageInfo.textContent = `${page} / ${totalPages}`;

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

// 게시글 단일 렌더링 (상세보기)
function renderPost() {
    const params = new URLSearchParams(window.location.search);
    const idParam = params.get('id');
    if (!idParam) {
        document.body.innerHTML = '<p>게시글 ID가 없습니다.</p>';
        return;
    }
    const id = Number(idParam);
    const post = getPosts().find(p => p.id === id);

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
        iframe.setAttribute('allowfullscreen', '');
        iframe.style.display = 'block';
        iframe.style.margin = '20px auto';

        const contentElem = document.getElementById('post-content');
        contentElem.appendChild(document.createElement('br'));
        contentElem.appendChild(iframe);
    }
}

// 게시글 삭제
function deletePost() {
    const params = new URLSearchParams(window.location.search);
    const idParam = params.get('id');
    if (!idParam) {
        alert('삭제할 게시글 ID가 없습니다.');
        return;
    }
    const id = Number(idParam);

    let posts = getPosts();
    posts = posts.filter(post => post.id !== id);
    localStorage.setItem('posts', JSON.stringify(posts));

    const comments = JSON.parse(localStorage.getItem('comments') || '{}');
    delete comments[id];
    localStorage.setItem('comments', JSON.stringify(comments));

    alert('글이 삭제되었습니다.');
    window.location.href = 'index.html';
}

// 댓글 가져오기
function getComments(postId) {
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    return allComments[String(postId)] || [];
}

// 댓글 저장
function saveComment(postId, { author, content, date }) {
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    if (!allComments[postId]) allComments[postId] = [];
    allComments[postId].push({ author, content, date });
    localStorage.setItem('comments', JSON.stringify(allComments));
}

// 댓글 렌더링
function renderComments(postId) {
    const commentList = document.getElementById('comment-list');
    if (!commentList) return;
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

let editingCommentIndex = null;

// 댓글 수정 시작
function editComment(index) {
    const postId = String(new URLSearchParams(window.location.search).get('id'));
    const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
    const comments = allComments[postId] || [];

    const comment = comments[index];
    if (!comment) return;

    document.getElementById('comment-content').value = comment.content;

    editingCommentIndex = index;

    const submitButton = document.querySelector('#comment-form button');
    if (submitButton) submitButton.textContent = '댓글 수정 완료';

    const editingStatus = document.getElementById('editing-status');
    if (editingStatus) editingStatus.style.display = 'block';
}

// 댓글 삭제
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

// 초기화 함수 (게시글 상세보기 및 댓글)
function initialize() {
    const params = new URLSearchParams(window.location.search);
    const postId = params.get('id');

    renderPost();
    renderComments(postId);

    const commentForm = document.getElementById('comment-form');
    const contentInput = document.getElementById('comment-content');

    if (commentForm) {
        commentForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const author = localStorage.getItem('nickname') || '익명';
            const content = contentInput.value.trim();
            if (!content) {
                alert('댓글 내용을 입력해주세요.');
                return;
            }

            const date = getLocalDateString();

            if (editingCommentIndex !== null) {
                const allComments = JSON.parse(localStorage.getItem('comments') || '{}');
                const comments = allComments[postId] || [];
                comments[editingCommentIndex].content = content;
                comments[editingCommentIndex].date = date;
                allComments[postId] = comments;
                localStorage.setItem('comments', JSON.stringify(allComments));

                editingCommentIndex = null;

                const submitButton = document.querySelector('#comment-form button');
                if (submitButton) submitButton.textContent = '댓글 등록';

                const editingStatus = document.getElementById('editing-status');
                if (editingStatus) editingStatus.style.display = 'none';
            } else {
                saveComment(postId, { author, content, date });
            }

            contentInput.value = '';
            renderComments(postId);
        });
    }

    const editBtn = document.getElementById('edit-btn');
    if (editBtn) {
        editBtn.addEventListener('click', () => {
            window.location.href = `edit.html?id=${postId}`;
        });
    }

    const deleteBtn = document.getElementById('delete-btn');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => {
            if (confirm('이 글을 삭제하시겠습니까?')) {
                deletePost();
            }
        });
    }
}

// 페이지 로드 시 실행 (index.html / view.html 구분)
document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname.split('/').pop();

    if (currentPath === 'index.html' || currentPath === '') {
        renderPosts(1);
    } else if (currentPath === 'view.html') {
        if (document.getElementById('post-title')) {
            initialize();
        }
    }
});
