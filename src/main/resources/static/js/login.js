/**
 * 로그인 페이지 JavaScript
 * 
 * 주요 기능:
 * - 폼 유효성 검사
 * - 비밀번호 표시/숨김 토글
 * - 로그인 API 호출
 * - 로딩 상태 관리
 * - 모달 메시지 표시
 */

// DOM 요소들
const loginForm = document.getElementById('loginForm');
const loginInfoInput = document.getElementById('loginInfo');
const passwordInput = document.getElementById('password');
const rememberMeCheckbox = document.getElementById('rememberMe');
const loginBtn = document.getElementById('loginBtn');
const loadingSpinner = document.getElementById('loadingSpinner');
const messageModal = document.getElementById('messageModal');

// 유효성 검사 메시지 요소들
const loginInfoMessage = document.getElementById('loginInfoMessage');
const passwordMessage = document.getElementById('passwordMessage');

// 모달 요소들
const modalTitle = document.getElementById('modalTitle');
const modalMessage = document.getElementById('modalMessage');
const modalBtn = document.getElementById('modalBtn');

/**
 * 페이지 로드 시 초기화
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('로그인 페이지 로드됨');
    
    // 폼 제출 이벤트 리스너 등록
    loginForm.addEventListener('submit', handleLogin);
    
    // 입력 필드 실시간 유효성 검사
    loginInfoInput.addEventListener('input', validateLoginInfo);
    passwordInput.addEventListener('input', validatePassword);
    
    // 엔터 키 이벤트 처리
    loginInfoInput.addEventListener('keypress', handleEnterKey);
    passwordInput.addEventListener('keypress', handleEnterKey);
    
    // 저장된 로그인 정보 복원
    loadSavedCredentials();
});

/**
 * 엔터 키 이벤트 처리
 * @param {KeyboardEvent} event 
 */
function handleEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        if (event.target === loginInfoInput) {
            passwordInput.focus();
        } else if (event.target === passwordInput) {
            handleLogin(event);
        }
    }
}

/**
 * 사용자명 유효성 검사
 */
function validateLoginInfo() {
    const loginInfo = loginInfoInput.value.trim();
    const messageElement = loginInfoMessage;
    
    // 기존 메시지 초기화
    clearValidationMessage(messageElement);
    
    if (!loginInfo) {
        showValidationMessage(messageElement, '사용자명 또는 이메일을 입력해주세요.', 'error');
        return false;
    }
    
    // 이메일 형식인지 확인
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginInfo);
    
    if (isEmail) {
        // 이메일 형식 검증
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginInfo)) {
            showValidationMessage(messageElement, '올바른 이메일 형식을 입력해주세요.', 'error');
            return false;
        }
    } else {
        // 사용자별명 형식 검증 (3-50자, 한글, 영문, 숫자, 언더스코어만, 띄어쓰기 불가)
        if (!/^[a-zA-Z0-9가-힣_]{3,50}$/.test(loginInfo)) {
            showValidationMessage(messageElement, '사용자별명은 3-50자의 한글, 영문, 숫자, 언더스코어만 사용 가능합니다. 띄어쓰기는 허용되지 않습니다.', 'error');
            return false;
        }
    }
    
    showValidationMessage(messageElement, '✓', 'success');
    return true;
}

/**
 * 비밀번호 유효성 검사
 */
function validatePassword() {
    const password = passwordInput.value;
    const messageElement = passwordMessage;
    
    // 기존 메시지 초기화
    clearValidationMessage(messageElement);
    
    if (!password) {
        showValidationMessage(messageElement, '비밀번호를 입력해주세요.', 'error');
        return false;
    }
    
    if (password.length < 6) {
        showValidationMessage(messageElement, '비밀번호는 최소 6자 이상이어야 합니다.', 'error');
        return false;
    }
    
    showValidationMessage(messageElement, '✓', 'success');
    return true;
}

/**
 * 유효성 검사 메시지 표시
 * @param {HTMLElement} element - 메시지를 표시할 요소
 * @param {string} message - 표시할 메시지
 * @param {string} type - 메시지 타입 ('error' 또는 'success')
 */
function showValidationMessage(element, message, type) {
    element.textContent = message;
    element.className = `validation-message ${type}`;
}

/**
 * 유효성 검사 메시지 초기화
 * @param {HTMLElement} element - 초기화할 요소
 */
function clearValidationMessage(element) {
    element.textContent = '';
    element.className = 'validation-message';
}

/**
 * 비밀번호 표시/숨김 토글
 */
function togglePassword() {
    const passwordField = passwordInput;
    const passwordIcon = document.getElementById('passwordIcon');
    
    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        passwordIcon.className = 'fas fa-eye-slash';
    } else {
        passwordField.type = 'password';
        passwordIcon.className = 'fas fa-eye';
    }
}

/**
 * 로그인 처리
 * @param {Event} event - 폼 제출 이벤트
 */
async function handleLogin(event) {
    event.preventDefault();
    
    console.log('로그인 시도 중...');
    
    // 유효성 검사
    const isLoginInfoValid = validateLoginInfo();
    const isPasswordValid = validatePassword();
    
    if (!isLoginInfoValid || !isPasswordValid) {
        showModal('입력 오류', '모든 필드를 올바르게 입력해주세요.', 'error');
        return;
    }
    
    // 로그인 버튼 비활성화 및 로딩 표시
    setLoadingState(true);
    
    try {
        // 로그인 데이터 준비
        const loginData = {
            loginInfo: loginInfoInput.value.trim(),
            password: passwordInput.value,
            rememberMe: rememberMeCheckbox.checked
        };
        
        console.log('로그인 데이터:', { ...loginData, password: '***' });
        
        // 로그인 API 호출
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        
        console.log('로그인 응답:', result);
        
        if (response.ok && result.success) {
            // 로그인 성공
            showModal('로그인 성공', '환영합니다! 게시판으로 이동합니다.', 'success');
            
            // 사용자 정보 저장
            if (result.user) {
                localStorage.setItem('userInfo', JSON.stringify(result.user));
            }
            
            // 로그인 정보 저장 (체크박스가 체크된 경우)
            if (rememberMeCheckbox.checked) {
                saveCredentials(loginData.loginInfo);
            } else {
                clearSavedCredentials();
            }
            
            // 잠시 후 게시판 페이지로 이동
            setTimeout(() => {
                window.location.href = '/board.html';
            }, 2000);
            
        } else {
            // 로그인 실패
            const errorMessage = result.message || '로그인에 실패했습니다. 사용자별명 또는 이메일과 비밀번호를 확인해주세요.';
            showModal('로그인 실패', errorMessage, 'error');
        }
        
    } catch (error) {
        console.error('로그인 오류:', error);
        showModal('오류', '서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요.', 'error');
    } finally {
        // 로딩 상태 해제
        setLoadingState(false);
    }
}

/**
 * 로딩 상태 설정
 * @param {boolean} isLoading - 로딩 상태 여부
 */
function setLoadingState(isLoading) {
    if (isLoading) {
        loginBtn.disabled = true;
        loginBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 로그인 중...';
        loadingSpinner.style.display = 'flex';
    } else {
        loginBtn.disabled = false;
        loginBtn.innerHTML = '<i class="fas fa-sign-in-alt"></i> 로그인';
        loadingSpinner.style.display = 'none';
    }
}

/**
 * 모달 메시지 표시
 * @param {string} title - 모달 제목
 * @param {string} message - 모달 메시지
 * @param {string} type - 메시지 타입 ('success' 또는 'error')
 */
function showModal(title, message, type) {
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    
    // 타입에 따른 스타일 적용
    if (type === 'success') {
        modalTitle.style.color = '#28a745';
    } else if (type === 'error') {
        modalTitle.style.color = '#dc3545';
    }
    
    messageModal.style.display = 'flex';
}

/**
 * 모달 닫기
 */
function closeModal() {
    messageModal.style.display = 'none';
}

/**
 * 로그인 정보 저장 (로컬 스토리지)
 * @param {string} loginInfo - 사용자별명 또는 이메일
 */
function saveCredentials(loginInfo) {
    try {
        localStorage.setItem('savedLoginInfo', loginInfo);
        console.log('로그인 정보 저장됨:', loginInfo);
    } catch (error) {
        console.error('로그인 정보 저장 실패:', error);
    }
}

/**
 * 저장된 로그인 정보 불러오기
 */
function loadSavedCredentials() {
    try {
        const savedLoginInfo = localStorage.getItem('savedLoginInfo');
        if (savedLoginInfo) {
            loginInfoInput.value = savedLoginInfo;
            rememberMeCheckbox.checked = true;
            console.log('저장된 로그인 정보 복원됨:', savedLoginInfo);
        }
    } catch (error) {
        console.error('저장된 로그인 정보 불러오기 실패:', error);
    }
}

/**
 * 저장된 로그인 정보 삭제
 */
function clearSavedCredentials() {
    try {
        localStorage.removeItem('savedLoginInfo');
        console.log('저장된 로그인 정보 삭제됨');
    } catch (error) {
        console.error('저장된 로그인 정보 삭제 실패:', error);
    }
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