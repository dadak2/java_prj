/**
 * 헤더 로드
 */
document.addEventListener('DOMContentLoaded', function() {
    loadHeader();
});

function loadHeader() {
    const headerContainer = document.getElementById('header-container');
    if (headerContainer) {
        // header.html 파일을 fetch로 로드
        fetch('/header.html')
            .then(response => response.text())
            .then(html => {
                headerContainer.innerHTML = html;
                initializeHeader();
            })
            .catch(error => {
                console.error('헤더 로드 오류:', error);
            });
    }
}

/**
 * 헤더 초기화
 */
function initializeHeader() {
    // 사용자 정보 표시
    displayUserInfo();
    
    // 현재 페이지에 따른 네비게이션 활성화
    setActiveNavigation();
}

/**
 * 사용자 정보 표시
 */
function displayUserInfo() {
    try {
        // 로컬 스토리지에서 사용자 정보 가져오기
        const savedLoginInfo = localStorage.getItem('savedLoginInfo');
        const userInfo = localStorage.getItem('userInfo');
        
        const loggedOutUI = document.getElementById('loggedOutUI');
        const loggedInUI = document.getElementById('loggedInUI');
        const userNicknameElement = document.getElementById('userNickname');
        
        if (userInfo) {
            const user = JSON.parse(userInfo);
            showLoggedInUI();
            if (userNicknameElement) {
                userNicknameElement.textContent = user.nickname || '사용자';
                // 클릭 이벤트 추가
                userNicknameElement.style.cursor = 'pointer';
                userNicknameElement.style.textDecoration = 'underline';
                userNicknameElement.onclick = function() {
                    window.location.href = '/profile.html';
                };
            }
        } else if (savedLoginInfo) {
            // 로그인 정보만 있는 경우
            showLoggedInUI();
            if (userNicknameElement) {
                userNicknameElement.textContent = savedLoginInfo;
                // 클릭 이벤트 추가
                userNicknameElement.style.cursor = 'pointer';
                userNicknameElement.style.textDecoration = 'underline';
                userNicknameElement.onclick = function() {
                    window.location.href = '/profile.html';
                };
            }
        } else {
            // 로그인 정보가 없는 경우 로그인 버튼 표시
            showLoggedOutUI();
        }
        
        console.log('사용자 정보 표시됨');
        
    } catch (error) {
        console.error('사용자 정보 표시 오류:', error);
        showLoggedOutUI();
    }
}

// 전역으로 사용할 수 있도록 window 객체에 할당
window.displayUserInfo = displayUserInfo;

/**
 * 로그인된 사용자 UI 표시
 */
function showLoggedInUI() {
    const loggedOutUI = document.getElementById('loggedOutUI');
    const loggedInUI = document.getElementById('loggedInUI');
    
    if (loggedOutUI) loggedOutUI.style.display = 'none';
    if (loggedInUI) loggedInUI.style.display = 'block';
}

/**
 * 로그인하지 않은 사용자 UI 표시
 */
function showLoggedOutUI() {
    const loggedOutUI = document.getElementById('loggedOutUI');
    const loggedInUI = document.getElementById('loggedInUI');
    
    if (loggedOutUI) loggedOutUI.style.display = 'block';
    if (loggedInUI) loggedInUI.style.display = 'none';
}

/**
 * 현재 페이지에 따른 네비게이션 활성화
 */
function setActiveNavigation() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    console.log('현재 경로:', currentPath);
    
    // 모든 네비게이션 링크에서 active 클래스 제거
    navLinks.forEach(link => {
        link.classList.remove('active');
    });
    
    // 현재 페이지에 맞는 네비게이션 활성화
    if (currentPath === '/' || currentPath === '/board.html') {
        const boardLink = document.getElementById('nav-board');
        if (boardLink) {
            boardLink.classList.add('active');
            console.log('게시판 링크 활성화');
        }
    } else if (currentPath === '/game.html') {
        const gameLink = document.getElementById('nav-game');
        if (gameLink) {
            gameLink.classList.add('active');
            console.log('게임 링크 활성화');
        }
    } else if (currentPath === '/news.html') {
        const newsLink = document.getElementById('nav-news');
        if (newsLink) {
            newsLink.classList.add('active');
            console.log('뉴스 링크 활성화');
        }
    }
}

/**
 * 로그인 처리
 */
function goToLogin() {
    // login-modal.js가 로드되어 있으면 모달 표시
    if (typeof showLoginModal === 'function') {
        showLoginModal();
    } else {
        console.warn('로그인 모달을 사용할 수 없습니다. login-modal.js가 로드되지 않았습니다.');
    }
}

/**
 * 로그아웃 처리
 */
function logout() {
    // 모달이 있는지 확인
    const messageModal = document.getElementById('messageModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalBtn = document.getElementById('modalBtn');
    
    if (messageModal && modalTitle && modalMessage && modalBtn) {
        showModal('로그아웃', '정말 로그아웃하시겠습니까?', 'confirm');
        
        // 확인 버튼 클릭 시 로그아웃 실행
        modalBtn.onclick = function() {
            // 로컬 스토리지에서 사용자 정보 삭제
            localStorage.removeItem('userInfo');
            localStorage.removeItem('savedLoginInfo');
            
            showModal('로그아웃 완료', '로그아웃되었습니다.', 'success');
            
            // 현재 페이지에서 사용자 정보만 업데이트
            setTimeout(() => {
                displayUserInfo();
                closeModal();
            }, 1500);
        };
    } else {
        // 모달이 없는 경우 바로 로그아웃
        localStorage.removeItem('userInfo');
        localStorage.removeItem('savedLoginInfo');
        displayUserInfo();
    }
}

/**
 * 로그인 성공 후 처리
 */
function onLoginSuccess(data) {
    // 사용자 정보 업데이트
    displayUserInfo();
}

