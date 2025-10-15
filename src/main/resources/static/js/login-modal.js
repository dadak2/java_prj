/**
 * 공통 로그인 모달 JavaScript
 * 
 * 주요 기능:
 * - 로그인 모달 동적 로드
 * - 로그인 폼 처리
 * - 유효성 검사
 * - 비밀번호 표시/숨김 토글
 * - 로딩 상태 관리
 */

// 로그인 모달이 로드되었는지 확인
let loginModalLoaded = false;

/**
 * 로그인 모달 동적 로드
 */
async function loadLoginModal() {
    if (loginModalLoaded) {
        console.log('로그인 모달이 이미 로드되어 있습니다.');
        return;
    }
    
    try {
        console.log('로그인 모달 로딩 시작...');
        
        // CSS 로드
        if (!document.querySelector('link[href="/css/login-modal.css"]')) {
            const cssLink = document.createElement('link');
            cssLink.rel = 'stylesheet';
            cssLink.href = '/css/login-modal.css';
            document.head.appendChild(cssLink);
            console.log('로그인 모달 CSS 로드됨');
        }
        
        // HTML 직접 생성
        const html = createLoginModalHTML();
        console.log('HTML 생성됨, 길이:', html.length);
        
        // body에 추가
        document.body.insertAdjacentHTML('beforeend', html);
        console.log('HTML이 body에 추가됨');
        
        // 이벤트 리스너 등록
        initializeLoginModal();
        
        loginModalLoaded = true;
        console.log('로그인 모달이 로드되었습니다.');
        
    } catch (error) {
        console.error('로그인 모달 로드 실패:', error);
    }
}

/**
 * 로그인 모달 HTML 생성
 */
function createLoginModalHTML() {
    return `
    <!-- 로그인 모달 -->
    <div id="loginModal" class="modal" style="display: none;">
        <div class="modal-content login-modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-sign-in-alt"></i> 로그인</h3>
                <span class="close" onclick="closeLoginModal()">&times;</span>
            </div>
            <div class="modal-body">
                <form id="loginForm" class="login-form">
                    <div class="form-group">
                        <label for="loginInfo">
                            <i class="fas fa-user"></i> 사용자별명 또는 이메일
                        </label>
                        <input type="text" id="loginInfo" name="loginInfo" required 
                               placeholder="사용자별명 또는 이메일을 입력하세요">
                        <div class="validation-message" id="loginInfoMessage"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">
                            <i class="fas fa-lock"></i> 비밀번호
                        </label>
                        <div class="password-input">
                            <input type="password" id="password" name="password" required 
                                   placeholder="비밀번호를 입력하세요">
                            <button type="button" class="password-toggle" onclick="togglePassword()">
                                <i class="fas fa-eye" id="passwordIcon"></i>
                            </button>
                        </div>
                        <div class="validation-message" id="passwordMessage"></div>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" id="rememberMe">
                            <span class="checkmark"></span>
                            로그인 상태 유지
                        </label>
                    </div>
                    
                    <button type="submit" id="loginBtn" class="login-btn">
                        <span class="btn-text">로그인</span>
                        <div class="loading-spinner" id="loginSpinner" style="display: none;">
                            <i class="fas fa-spinner fa-spin"></i>
                        </div>
                    </button>
                </form>
                
                <div class="login-footer">
                    <p>계정이 없으신가요? <a href="#" onclick="closeLoginModal(); showSignupModal(); return false;">회원가입</a></p>
                </div>
            </div>
        </div>
    </div>
    `;
}

/**
 * 로그인 모달 초기화
 */
function initializeLoginModal() {
    const loginForm = document.getElementById('loginForm');
    const loginInfoInput = document.getElementById('loginInfo');
    const passwordInput = document.getElementById('password');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (loginInfoInput) {
        loginInfoInput.addEventListener('input', function() {
            const value = this.value.trim();
            if (value) {
                showValidationMessage('loginInfoMessage', '');
            }
        });
    }
    
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            const value = this.value;
            if (value) {
                showValidationMessage('passwordMessage', '');
            }
        });
    }
}

/**
 * 로그인 모달 표시
 */
async function showLoginModal() {
    await loadLoginModal();
    
    const loginModal = document.getElementById('loginModal');
    console.log('로그인 모달 요소:', loginModal);
    
    if (loginModal) {
        loginModal.style.display = 'flex';
        console.log('로그인 모달 표시됨');
        // 첫 번째 입력 필드에 포커스
        setTimeout(() => {
            const loginInfoInput = document.getElementById('loginInfo');
            if (loginInfoInput) {
                loginInfoInput.focus();
            }
        }, 100);
    } else {
        console.error('로그인 모달 요소를 찾을 수 없습니다.');
    }
}

/**
 * 로그인 모달 닫기
 */
function closeLoginModal() {
    const loginModal = document.getElementById('loginModal');
    if (loginModal) {
        loginModal.style.display = 'none';
        // 폼 초기화
        resetLoginForm();
    }
}

/**
 * 로그인 폼 초기화
 */
function resetLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.reset();
        clearValidationMessages();
    }
}

/**
 * 유효성 검사 메시지 초기화
 */
function clearValidationMessages() {
    const loginInfoMessage = document.getElementById('loginInfoMessage');
    const passwordMessage = document.getElementById('passwordMessage');
    
    if (loginInfoMessage) loginInfoMessage.textContent = '';
    if (passwordMessage) passwordMessage.textContent = '';
}

/**
 * 비밀번호 표시/숨김 토글
 */
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const passwordIcon = document.getElementById('passwordIcon');
    
    if (passwordInput && passwordIcon) {
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            passwordIcon.classList.remove('fa-eye');
            passwordIcon.classList.add('fa-eye-slash');
        } else {
            passwordInput.type = 'password';
            passwordIcon.classList.remove('fa-eye-slash');
            passwordIcon.classList.add('fa-eye');
        }
    }
}

/**
 * 로그인 처리
 */
async function handleLogin(event) {
    event.preventDefault();
    
    const loginInfo = document.getElementById('loginInfo').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    
    // 유효성 검사
    if (!validateLoginForm(loginInfo, password)) {
        return;
    }
    
    // 로딩 상태 표시
    setLoginLoading(true);
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                loginInfo: loginInfo,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // 로그인 성공
            handleLoginSuccess(data, rememberMe);
        } else {
            // 로그인 실패
            handleLoginError(data.message || '로그인에 실패했습니다.');
        }
    } catch (error) {
        console.error('로그인 오류:', error);
        handleLoginError('네트워크 오류가 발생했습니다.');
    } finally {
        setLoginLoading(false);
    }
}

/**
 * 로그인 폼 유효성 검사
 */
function validateLoginForm(loginInfo, password) {
    let isValid = true;
    
    // 로그인 정보 검사
    if (!loginInfo) {
        showValidationMessage('loginInfoMessage', '사용자별명 또는 이메일을 입력해주세요.');
        isValid = false;
    } else {
        showValidationMessage('loginInfoMessage', '');
    }
    
    // 비밀번호 검사
    if (!password) {
        showValidationMessage('passwordMessage', '비밀번호를 입력해주세요.');
        isValid = false;
    } else {
        showValidationMessage('passwordMessage', '');
    }
    
    return isValid;
}

/**
 * 유효성 검사 메시지 표시
 */
function showValidationMessage(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
    }
}

/**
 * 로그인 로딩 상태 설정
 */
function setLoginLoading(loading) {
    const loginBtn = document.getElementById('loginBtn');
    
    if (loginBtn) {
        if (loading) {
            loginBtn.classList.add('loading');
            loginBtn.disabled = true;
        } else {
            loginBtn.classList.remove('loading');
            loginBtn.disabled = false;
        }
    }
}

/**
 * 로그인 성공 처리
 */
function handleLoginSuccess(data, rememberMe) {
    // 사용자 정보 저장
    localStorage.setItem('userInfo', JSON.stringify(data.user));
    
    if (rememberMe) {
        localStorage.setItem('savedLoginInfo', data.user.nickname);
    }
    
    // 모달 닫기
    closeLoginModal();
    
    // 성공 메시지 표시
    if (typeof showModal === 'function') {
        showModal('로그인 성공', '로그인되었습니다.', 'success');
    } else {
        alert('로그인되었습니다.');
    }
    
    // 페이지별 로그인 성공 처리
    if (typeof onLoginSuccess === 'function') {
        onLoginSuccess(data);
    } else {
        // 기본 동작: 페이지 새로고침
        setTimeout(() => {
            window.location.reload();
        }, 1500);
    }
}

/**
 * 로그인 실패 처리
 */
function handleLoginError(message) {
    if (typeof showModal === 'function') {
        showModal('로그인 실패', message, 'error');
    } else {
        alert('로그인 실패: ' + message);
    }
}
