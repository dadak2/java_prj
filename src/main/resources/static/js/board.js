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
let userNicknameElement = null; // 동적으로 생성되므로 초기값을 null로 설정
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
 * 로그인 성공 후 처리
 */
function onLoginSuccess(data) {
    // 사용자 정보 업데이트 (common.js의 함수 사용)
    if (typeof displayUserInfo === 'function') {
        displayUserInfo();
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
                author: board.author?.nickname || '알 수 없음',
                authorEmail: board.author?.email,
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
    
    // 로그인 상태 확인
    const userInfo = localStorage.getItem('userInfo');
    const savedLoginInfo = localStorage.getItem('savedLoginInfo');
    
    if (!userInfo && !savedLoginInfo) {
        showModal('로그인 필요', '글쓰기를 위해서는 로그인이 필요합니다.', 'info', () => {
            console.log('로그인 모달 확인');
            showLoginModal();
        });
        return;
    }
    
    // 글쓰기 페이지로 이동
    window.location.href = '/write.html';
}

// 로그아웃 함수는 common.js에서 제공됩니다.

// 모달 관련 함수들은 common.js에서 제공됩니다.

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

// 모달 관련 이벤트 리스너는 common.js에서 처리됩니다. 