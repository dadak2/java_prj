/**
 * 프로필 페이지 JavaScript
 * 
 * 사용자 프로필 정보를 표시하고 관리합니다.
 */

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadProfileData();
});

/**
 * 프로필 데이터 로드
 */
function loadProfileData() {
    try {
        const userInfo = localStorage.getItem('userInfo');
        const savedLoginInfo = localStorage.getItem('savedLoginInfo');
        
        if (userInfo) {
            const user = JSON.parse(userInfo);
            displayProfileData(user);
        } else if (savedLoginInfo) {
            // 기본 정보만 있는 경우
            displayBasicProfile(savedLoginInfo);
        } else {
            // 로그인하지 않은 경우
            showLoginRequired();
        }
    } catch (error) {
        console.error('프로필 데이터 로드 오류:', error);
        showError('프로필 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

/**
 * 프로필 데이터 표시
 */
function displayProfileData(user) {
    // 기본 정보 표시
    document.getElementById('profileName').textContent = user.nickname || '사용자';
    document.getElementById('profileEmail').textContent = user.email || '이메일 없음';
    document.getElementById('profileRole').textContent = getUserRoleText(user.userRole);
    
    // 상세 정보 표시
    document.getElementById('nicknameValue').textContent = user.nickname || '-';
    document.getElementById('emailValue').textContent = user.email || '-';
    document.getElementById('roleValue').textContent = getUserRoleText(user.userRole);
    document.getElementById('joinDateValue').textContent = formatDate(user.createdAt) || '-';
    
    // 통계 정보 로드 (실제로는 API 호출이 필요)
    loadUserStats(user.userNo);
}

/**
 * 기본 프로필 표시 (로그인 정보만 있는 경우)
 */
function displayBasicProfile(nickname) {
    document.getElementById('profileName').textContent = nickname;
    document.getElementById('profileEmail').textContent = '이메일 정보 없음';
    document.getElementById('profileRole').textContent = '일반 사용자';
    
    document.getElementById('nicknameValue').textContent = nickname;
    document.getElementById('emailValue').textContent = '-';
    document.getElementById('roleValue').textContent = '일반 사용자';
    document.getElementById('joinDateValue').textContent = '-';
    
    // 통계는 0으로 표시
    document.getElementById('postCount').textContent = '0';
    document.getElementById('commentCount').textContent = '0';
    document.getElementById('totalViews').textContent = '0';
    document.getElementById('totalLikes').textContent = '0';
}

/**
 * 로그인 필요 메시지 표시
 */
function showLoginRequired() {
    showModal('로그인 필요', '프로필을 보려면 로그인이 필요합니다.', 'info', () => {
        if (typeof showLoginModal === 'function') {
            showLoginModal();
        } else {
            showModal('로그인 필요', '프로필을 보려면 로그인이 필요합니다.', 'info');
        }
    });
}

/**
 * 사용자 통계 로드
 */
async function loadUserStats(userNo) {
    try {
        // 실제로는 API 호출이 필요하지만, 현재는 임시 데이터 사용
        // TODO: API 엔드포인트 구현 후 실제 데이터 로드
        
        // 임시 데이터
        document.getElementById('postCount').textContent = '5';
        document.getElementById('commentCount').textContent = '12';
        document.getElementById('totalViews').textContent = '156';
        document.getElementById('totalLikes').textContent = '23';
        
    } catch (error) {
        console.error('통계 데이터 로드 오류:', error);
        // 오류 시 0으로 표시
        document.getElementById('postCount').textContent = '0';
        document.getElementById('commentCount').textContent = '0';
        document.getElementById('totalViews').textContent = '0';
        document.getElementById('totalLikes').textContent = '0';
    }
}

/**
 * 사용자 역할 텍스트 변환
 */
function getUserRoleText(userRole) {
    switch(userRole) {
        case 'ADMIN':
            return '관리자';
        case 'USER':
            return '일반 사용자';
        case 'MODERATOR':
            return '모더레이터';
        default:
            return '일반 사용자';
    }
}

/**
 * 날짜 포맷팅
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    } catch (error) {
        return '-';
    }
}

/**
 * 프로필 수정
 */
function editProfile() {
    showModal('기능 준비 중', '프로필 수정 기능은 준비 중입니다.', 'info');
}

/**
 * 비밀번호 변경
 */
function changePassword() {
    showModal('기능 준비 중', '비밀번호 변경 기능은 준비 중입니다.', 'info');
}

/**
 * 계정 삭제
 */
function deleteAccount() {
    showModal('계정 삭제', '정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.', 'confirm', () => {
        showModal('기능 준비 중', '계정 삭제 기능은 준비 중입니다.', 'info');
    });
}

/**
 * 오류 메시지 표시
 */
function showError(message) {
    showModal('오류', message, 'error');
}
