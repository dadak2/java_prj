/**
 * 게시판 페이지 JavaScript
 * 
 * 주요 기능:
 * - 사용자 정보 표시
 * - 로그아웃 처리
 * - 더미 게시글 표시
 * - 검색 및 필터링
 * - 페이지네이션
 */

// DOM 요소들
const userNicknameElement = document.getElementById('userNickname');
const postListElement = document.getElementById('postList');
const paginationElement = document.getElementById('pagination');
const searchInput = document.getElementById('searchInput');
const categoryFilter = document.getElementById('categoryFilter');
const sortFilter = document.getElementById('sortFilter');
const loadingSpinner = document.getElementById('loadingSpinner');
const messageModal = document.getElementById('messageModal');

// 모달 요소들
const modalTitle = document.getElementById('modalTitle');
const modalMessage = document.getElementById('modalMessage');
const modalBtn = document.getElementById('modalBtn');

// 페이지 상태
let currentPage = 1;
let postsPerPage = 10;
let allPosts = [];
let filteredPosts = [];

/**
 * 페이지 로드 시 초기화
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('게시판 페이지 로드됨');
    
    // 사용자 정보 표시
    displayUserInfo();
    
    // 실제 게시글 데이터 로드
    loadPosts();
    
    // 검색 이벤트 리스너
    searchInput.addEventListener('input', debounce(handleSearch, 300));
    
    // 엔터 키로 검색
    searchInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            searchPosts();
        }
    });
});

/**
 * 사용자 정보 표시
 */
function displayUserInfo() {
    try {
        // 로컬 스토리지에서 사용자 정보 가져오기
        const savedLoginInfo = localStorage.getItem('savedLoginInfo');
        const userInfo = localStorage.getItem('userInfo');
        
        if (userInfo) {
            const user = JSON.parse(userInfo);
            userNicknameElement.textContent = user.nickname || '사용자';
        } else if (savedLoginInfo) {
            // 로그인 정보만 있는 경우
            userNicknameElement.textContent = savedLoginInfo;
        } else {
            // 로그인 정보가 없는 경우 로그인 페이지로 리다이렉트
            showModal('로그인 필요', '로그인이 필요한 페이지입니다.', 'error');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 2000);
            return;
        }
        
        console.log('사용자 정보 표시됨:', userNicknameElement.textContent);
        
    } catch (error) {
        console.error('사용자 정보 표시 오류:', error);
        userNicknameElement.textContent = '사용자';
    }
}

/**
 * 실제 게시글 데이터 로드
 */
async function loadPosts() {
    showLoadingSpinner();
    
    try {
        const response = await fetch('/api/boards?page=0&size=100&sort=createdAt,desc');
        
        if (!response.ok) {
            throw new Error('게시글을 불러오는데 실패했습니다.');
        }
        
        const result = await response.json();
        console.log('API 응답:', result);
        
        if (result.content && Array.isArray(result.content)) {
            allPosts = result.content.map(board => ({
                id: board.boardNo,
                boardNo: board.boardNo,
                title: board.title,
                excerpt: board.content ? board.content.substring(0, 100) + (board.content.length > 100 ? '...' : '') : '',
                category: board.category,
                categoryName: getCategoryName(board.category),
                author: board.authorNickname || '알 수 없음',
                authorEmail: board.authorEmail,
                createdAt: new Date(board.createdAt),
                updatedAt: board.updatedAt ? new Date(board.updatedAt) : null,
                views: board.viewCount || 0,
                likes: board.likeCount || 0,
                comments: board.commentCount || 0,
                status: board.status
            }));
            
            // 날짜순으로 정렬
            allPosts.sort((a, b) => b.createdAt - a.createdAt);
            filteredPosts = [...allPosts];
            
            console.log('로드된 게시글 수:', allPosts.length);
        } else {
            console.log('게시글이 없습니다.');
            allPosts = [];
            filteredPosts = [];
        }
        
        displayPosts();
        
    } catch (error) {
        console.error('게시글 로드 오류:', error);
        showModal('오류', '게시글을 불러오는 중 오류가 발생했습니다.', 'error');
        allPosts = [];
        filteredPosts = [];
        displayPosts();
    } finally {
        hideLoadingSpinner();
    }
}

/**
 * 게시글 새로고침
 */
function refreshPosts() {
    console.log('게시글 새로고침');
    currentPage = 1;
    loadPosts();
}

/**
 * 카테고리 이름 변환
 */
function getCategoryName(category) {
    const categoryNames = {
        '일반': '일반',
        '공지': '공지',
        '질문': '질문',
        '자유': '자유',
        '기술': '기술'
    };
    return categoryNames[category] || category;
}

/**
 * 로딩 스피너 표시/숨김
 */
function showLoadingSpinner() {
    if (loadingSpinner) {
        loadingSpinner.style.display = 'flex';
    }
}

function hideLoadingSpinner() {
    if (loadingSpinner) {
        loadingSpinner.style.display = 'none';
    }
}

/**
 * 게시글 목록 표시
 */
function displayPosts() {
    const startIndex = (currentPage - 1) * postsPerPage;
    const endIndex = startIndex + postsPerPage;
    const postsToShow = filteredPosts.slice(startIndex, endIndex);
    
    if (postsToShow.length === 0) {
        if (allPosts.length === 0) {
            postListElement.innerHTML = `
                <div class="loading-message">
                    <i class="fas fa-file-alt"></i>
                    <p>아직 게시글이 없습니다.</p>
                    <p>첫 번째 게시글을 작성해보세요!</p>
                </div>
            `;
        } else {
            postListElement.innerHTML = `
                <div class="loading-message">
                    <i class="fas fa-search"></i>
                    <p>검색 결과가 없습니다.</p>
                    <p>다른 검색어를 입력해보세요.</p>
                </div>
            `;
        }
        paginationElement.innerHTML = '';
        return;
    }
    
    const postsHTML = postsToShow.map(post => `
        <div class="post-item" onclick="viewPost(${post.boardNo || post.id})">
            <div class="post-category">${post.categoryName}</div>
            <div class="post-content">
                <div class="post-title">${post.title}</div>
                <div class="post-excerpt">${post.excerpt}</div>
                <div class="post-meta">
                    <span><i class="fas fa-user"></i> ${post.author}</span>
                    <span><i class="fas fa-calendar"></i> ${formatDate(post.createdAt)}</span>
                </div>
            </div>
            <div class="post-stats">
                <span><i class="fas fa-eye"></i> ${post.views}</span>
                <span><i class="fas fa-heart"></i> ${post.likes}</span>
                <span><i class="fas fa-comment"></i> ${post.comments}</span>
            </div>
        </div>
    `).join('');
    
    postListElement.innerHTML = postsHTML;
    displayPagination();
}

/**
 * 페이지네이션 표시
 */
function displayPagination() {
    const totalPages = Math.ceil(filteredPosts.length / postsPerPage);
    
    if (totalPages <= 1) {
        paginationElement.innerHTML = '';
        return;
    }
    
    let paginationHTML = '';
    
    // 이전 페이지 버튼
    paginationHTML += `
        <button onclick="changePage(${currentPage - 1})" ${currentPage === 1 ? 'disabled' : ''}>
            <i class="fas fa-chevron-left"></i>
        </button>
    `;
    
    // 페이지 번호들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <button onclick="changePage(${i})" ${i === currentPage ? 'class="active"' : ''}>
                ${i}
            </button>
        `;
    }
    
    // 다음 페이지 버튼
    paginationHTML += `
        <button onclick="changePage(${currentPage + 1})" ${currentPage === totalPages ? 'disabled' : ''}>
            <i class="fas fa-chevron-right"></i>
        </button>
    `;
    
    paginationElement.innerHTML = paginationHTML;
}

/**
 * 페이지 변경
 */
function changePage(page) {
    const totalPages = Math.ceil(filteredPosts.length / postsPerPage);
    
    if (page < 1 || page > totalPages) {
        return;
    }
    
    currentPage = page;
    displayPosts();
    
    // 페이지 상단으로 스크롤
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

/**
 * 검색 처리
 */
function handleSearch() {
    const searchTerm = searchInput.value.trim().toLowerCase();
    
    if (searchTerm === '') {
        filteredPosts = [...allPosts];
    } else {
        filteredPosts = allPosts.filter(post => 
            post.title.toLowerCase().includes(searchTerm) ||
            post.excerpt.toLowerCase().includes(searchTerm) ||
            post.author.toLowerCase().includes(searchTerm)
        );
    }
    
    currentPage = 1;
    displayPosts();
}

/**
 * 검색 버튼 클릭
 */
function searchPosts() {
    handleSearch();
}

/**
 * 카테고리 필터링
 */
function filterPosts() {
    const selectedCategory = categoryFilter.value;
    const searchTerm = searchInput.value.trim().toLowerCase();
    
    let filtered = allPosts;
    
    // 카테고리 필터링
    if (selectedCategory) {
        filtered = filtered.filter(post => post.category === selectedCategory);
    }
    
    // 검색어 필터링
    if (searchTerm) {
        filtered = filtered.filter(post => 
            post.title.toLowerCase().includes(searchTerm) ||
            post.excerpt.toLowerCase().includes(searchTerm) ||
            post.author.toLowerCase().includes(searchTerm)
        );
    }
    
    filteredPosts = filtered;
    currentPage = 1;
    displayPosts();
}

/**
 * 정렬 처리
 */
function sortPosts() {
    const sortType = sortFilter.value;
    
    switch (sortType) {
        case 'latest':
            filteredPosts.sort((a, b) => b.createdAt - a.createdAt);
            break;
        case 'popular':
            filteredPosts.sort((a, b) => b.likes - a.likes);
            break;
        case 'views':
            filteredPosts.sort((a, b) => b.views - a.views);
            break;
    }
    
    currentPage = 1;
    displayPosts();
}

/**
 * 게시글 상세보기
 */
function viewPost(postId) {
    console.log('게시글 상세보기:', postId);
    // 상세보기 페이지로 이동
    window.location.href = `/detail.html?boardNo=${postId}`;
}

/**
 * 글쓰기 페이지로 이동
 */
function goToWrite() {
    console.log('글쓰기 페이지로 이동');
    // 글쓰기 페이지로 이동
    window.location.href = '/write.html';
}

/**
 * 로그아웃 처리
 */
function logout() {
    showModal('로그아웃', '정말 로그아웃하시겠습니까?', 'confirm');
    
    // 확인 버튼 클릭 시 로그아웃 실행
    modalBtn.onclick = function() {
        // 로컬 스토리지에서 사용자 정보 삭제
        localStorage.removeItem('userInfo');
        localStorage.removeItem('savedLoginInfo');
        
        showModal('로그아웃 완료', '로그아웃되었습니다. 로그인 페이지로 이동합니다.', 'success');
        
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 2000);
    };
}

/**
 * 모달 메시지 표시
 */
function showModal(title, message, type) {
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    
    // 타입에 따른 스타일 적용
    if (type === 'success') {
        modalTitle.style.color = '#28a745';
    } else if (type === 'error') {
        modalTitle.style.color = '#dc3545';
    } else if (type === 'info') {
        modalTitle.style.color = '#17a2b8';
    } else if (type === 'confirm') {
        modalTitle.style.color = '#ffc107';
    }
    
    messageModal.style.display = 'flex';
    
    // 확인 버튼 기본 동작 복원
    modalBtn.onclick = closeModal;
}

/**
 * 모달 닫기
 */
function closeModal() {
    messageModal.style.display = 'none';
}

/**
 * 날짜 포맷팅
 */
function formatDate(date) {
    const now = new Date();
    const diff = now - date;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (days === 0) {
        return '오늘';
    } else if (days === 1) {
        return '어제';
    } else if (days < 7) {
        return `${days}일 전`;
    } else {
        return date.toLocaleDateString('ko-KR');
    }
}

/**
 * 디바운스 함수
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 모달 외부 클릭 시 닫기
window.addEventListener('click', function(event) {
    if (event.target === messageModal) {
        closeModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' && messageModal.style.display === 'flex') {
        closeModal();
    }
}); 