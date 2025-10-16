/**
 * 뉴스 전용 모달 표시 함수
 */
function showNewsModal(title, message, type) {
    const modalTitle = document.getElementById('newsModalTitle');
    const modalMessage = document.getElementById('newsModalMessage');
    const messageModal = document.getElementById('newsMessageModal');
    const modalBtn = document.getElementById('newsModalBtn');
    
    if (!modalTitle || !modalMessage || !messageModal || !modalBtn) {
        console.warn('뉴스 모달 요소를 찾을 수 없습니다.');
        return;
    }
    
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
    
    // 확인 버튼 클릭 시 모달 닫기
    modalBtn.onclick = function() {
        messageModal.style.display = 'none';
    };
}

/**
 * 뉴스 전용 모달 닫기 함수
 */
function closeNewsModal() {
    const messageModal = document.getElementById('newsMessageModal');
    if (messageModal) {
        messageModal.style.display = 'none';
    }
}

// 전역 변수
let currentPage = 0;
let currentCategory = '';
let currentSort = 'latest';
let totalPages = 0;
let newsData = [];

// DOM 요소
const newsList = document.getElementById('newsList');
const pagination = document.getElementById('pagination');
const loadingIndicator = document.getElementById('loadingIndicator');
const categoryFilter = document.getElementById('categoryFilter');
const sortBy = document.getElementById('sortBy');
const refreshBtn = document.getElementById('refreshBtn');
const followBtn = document.getElementById('followBtn');
const followModal = document.getElementById('newsFollowModal');
const saveFollowBtn = document.getElementById('saveFollowBtn');
const cancelFollowBtn = document.getElementById('cancelFollowBtn');

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeNewsPage();
    setupEventListeners();
    loadNews();
});

/**
 * 뉴스 페이지 초기화
 */
function initializeNewsPage() {
    console.log('뉴스 피드 페이지 초기화');
    
    // 사용자 정보 확인
    const userInfo = localStorage.getItem('userInfo');
    if (!userInfo) {
        console.log('로그인되지 않은 사용자');
        // 로그인 모달 표시 옵션
    }
}

/**
 * 이벤트 리스너 설정
 */
function setupEventListeners() {
    // 새로고침 버튼
    refreshBtn.addEventListener('click', function() {
        loadNews();
    });
    
    // 팔로우 관리 버튼
    followBtn.addEventListener('click', function() {
        showFollowModal();
    });
    
    // 카테고리 필터 변경
    categoryFilter.addEventListener('change', function() {
        currentCategory = this.value;
        currentPage = 0;
        loadNews();
    });
    
    // 정렬 방식 변경
    sortBy.addEventListener('change', function() {
        currentSort = this.value;
        currentPage = 0;
        loadNews();
    });
    
    // 팔로우 모달 저장 버튼
    saveFollowBtn.addEventListener('click', function() {
        saveFollowSettings();
    });
    
    // 팔로우 모달 취소 버튼
    cancelFollowBtn.addEventListener('click', function() {
        closeFollowModal();
    });
    
    // 뉴스 모달 닫기 버튼들
    document.querySelectorAll('#newsFollowModal .close, #newsMessageModal .close').forEach(closeBtn => {
        closeBtn.addEventListener('click', function() {
            const modal = this.closest('.modal');
            modal.style.display = 'none';
        });
    });
    
    // 뉴스 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target.id === 'newsFollowModal' || event.target.id === 'newsMessageModal') {
            event.target.style.display = 'none';
        }
    });
}

/**
 * 뉴스 목록 로드
 */
async function loadNews() {
    try {
        showLoading(true);
        
        const params = new URLSearchParams({
            page: currentPage,
            size: 10,
            category: currentCategory,
            sort: currentSort
        });
        
        const response = await fetch(`/api/news?${params}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        newsData = data.content || [];
        totalPages = data.totalPages || 0;
        
        renderNewsList();
        renderPagination();
        
    } catch (error) {
        console.error('뉴스 로드 실패:', error);
        showNewsModal('오류', '뉴스를 불러오는 중 오류가 발생했습니다.', 'error');
        renderEmptyState();
    } finally {
        showLoading(false);
    }
}

/**
 * 뉴스 목록 렌더링
 */
function renderNewsList() {
    if (newsData.length === 0) {
        renderEmptyState();
        return;
    }
    
    const newsHTML = newsData.map(news => `
        <div class="news-item" onclick="viewNewsDetail(${news.id})">
            <div class="news-item-header">
                <span class="news-category ${news.category}">${getCategoryName(news.category)}</span>
                <span class="news-date">${formatDate(news.createdAt)}</span>
            </div>
            <h3 class="news-title">${news.title}</h3>
            <div class="news-content">${news.content}</div>
            <div class="news-meta">
                <div class="news-author">
                    <i class="fas fa-user"></i>
                    <span>${news.authorNickname || '익명'}</span>
                </div>
                <div class="news-stats">
                    <span><i class="fas fa-eye"></i> ${news.viewCount || 0}</span>
                    <span><i class="fas fa-heart"></i> ${news.likeCount || 0}</span>
                    <span><i class="fas fa-comment"></i> ${news.commentCount || 0}</span>
                </div>
            </div>
        </div>
    `).join('');
    
    newsList.innerHTML = newsHTML;
}

/**
 * 빈 상태 렌더링
 */
function renderEmptyState() {
    newsList.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-newspaper" style="font-size: 3rem; color: #6c757d; margin-bottom: 20px;"></i>
            <h3>뉴스가 없습니다</h3>
            <p>아직 등록된 뉴스가 없거나 필터 조건에 맞는 뉴스가 없습니다.</p>
        </div>
    `;
}

/**
 * 페이지네이션 렌더링
 */
function renderPagination() {
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let paginationHTML = '';
    
    // 이전 버튼
    paginationHTML += `
        <button ${currentPage === 0 ? 'disabled' : ''} onclick="changePage(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;
    
    // 페이지 번호들
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <button class="${i === currentPage ? 'active' : ''}" onclick="changePage(${i})">
                ${i + 1}
            </button>
        `;
    }
    
    // 다음 버튼
    paginationHTML += `
        <button ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="changePage(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;
    
    pagination.innerHTML = paginationHTML;
}

/**
 * 페이지 변경
 */
function changePage(page) {
    if (page < 0 || page >= totalPages) return;
    
    currentPage = page;
    loadNews();
    
    // 페이지 상단으로 스크롤
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

/**
 * 팔로우 모달 표시
 */
function showFollowModal() {
    const userInfo = localStorage.getItem('userInfo');
    if (!userInfo) {
        if (typeof showLoginModal === 'function') {
            showLoginModal();
        } else {
            showNewsModal('로그인 필요', '팔로우 기능을 사용하려면 로그인이 필요합니다.', 'info');
        }
        return;
    }
    
    // 현재 팔로우 설정 로드
    loadFollowSettings();
    const followModal = document.getElementById('newsFollowModal');
    if (followModal) {
        followModal.style.display = 'flex';
    }
}

/**
 * 팔로우 설정 로드
 */
async function loadFollowSettings() {
    try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        const response = await fetch(`/api/news/follows/${userInfo.id}`);
        
        if (response.ok) {
            const follows = await response.json();
            // 체크박스 상태 업데이트
            document.querySelectorAll('.category-checkboxes input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = follows.includes(checkbox.value);
            });
        }
    } catch (error) {
        console.error('팔로우 설정 로드 실패:', error);
    }
}

/**
 * 팔로우 설정 저장
 */
async function saveFollowSettings() {
    try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        const selectedCategories = Array.from(document.querySelectorAll('.category-checkboxes input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.value);
        
        const response = await fetch(`/api/news/follows/${userInfo.id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(selectedCategories)
        });
        
        if (response.ok) {
            showNewsModal('성공', '팔로우 설정이 저장되었습니다.', 'success');
            closeFollowModal();
            // 뉴스 목록 새로고침
            loadNews();
        } else {
            throw new Error('저장 실패');
        }
        
    } catch (error) {
        console.error('팔로우 설정 저장 실패:', error);
        showNewsModal('오류', '팔로우 설정 저장 중 오류가 발생했습니다.', 'error');
    }
}

/**
 * 팔로우 모달 닫기
 */
function closeFollowModal() {
    const followModal = document.getElementById('newsFollowModal');
    if (followModal) {
        followModal.style.display = 'none';
    }
}

/**
 * 뉴스 상세 보기
 */
function viewNewsDetail(newsId) {
    // 뉴스 상세 페이지로 이동 (향후 구현)
    console.log('뉴스 상세 보기:', newsId);
    showNewsModal('알림', '뉴스 상세 기능은 추후 구현 예정입니다.', 'info');
}

/**
 * 로딩 상태 표시/숨김
 */
function showLoading(show) {
    loadingIndicator.style.display = show ? 'block' : 'none';
}

/**
 * 카테고리명 변환
 */
function getCategoryName(category) {
    const categoryNames = {
        'TECH': '기술',
        'SPORTS': '스포츠',
        'POLITICS': '정치',
        'ENTERTAINMENT': '엔터테인먼트',
        'BUSINESS': '비즈니스'
    };
    return categoryNames[category] || category;
}

/**
 * 날짜 포맷팅
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) {
        return '어제';
    } else if (diffDays < 7) {
        return `${diffDays}일 전`;
    } else {
        return date.toLocaleDateString('ko-KR');
    }
}
