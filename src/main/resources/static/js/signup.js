/**
 * 회원가입 페이지 JavaScript 모듈
 * 
 * 회원가입 폼의 모든 기능을 처리하는 JavaScript 코드입니다.
 * 실시간 유효성 검사, 비밀번호 강도 확인, 중복 확인, 회원가입 처리 등을 담당합니다.
 * 
 * 주요 기능:
 * - 실시간 폼 유효성 검사
 * - 비밀번호 강도 계산 및 표시
 * - 사용자별명/이메일 중복 확인 (디바운싱 적용)
 * - 회원가입 API 호출 및 응답 처리
 * - 로딩 스피너 및 모달 관리
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */

// ==================== DOM 요소 참조 ====================

/**
 * 회원가입 폼 요소
 * 사용자가 입력한 데이터를 수집하고 제출하는 메인 폼
 */
const signupForm = document.getElementById('signupForm');

/**
 * 사용자별명 입력 필드
 * 3-50자의 영문, 숫자, 언더스코어만 사용 가능
 */
const nicknameInput = document.getElementById('nickname');

/**
 * 이메일 입력 필드
 * 유효한 이메일 형식이어야 함
 */
const emailInput = document.getElementById('email');

/**
 * 비밀번호 입력 필드
 * 최소 8자 이상, 강도 표시 기능 포함
 */
const passwordInput = document.getElementById('password');

/**
 * 비밀번호 확인 입력 필드
 * 비밀번호와 일치 여부 검증
 */
const confirmPasswordInput = document.getElementById('confirmPassword');

/**
 * 이용약관 동의 체크박스
 * 회원가입 필수 동의 항목
 */
const agreeTermsCheckbox = document.getElementById('agreeTerms');

/**
 * 회원가입 버튼
 * 모든 유효성 검사를 통과해야 활성화됨
 */
const signupBtn = document.getElementById('signupBtn');

// ==================== 메시지 표시 요소 ====================

/**
 * 사용자별명 유효성 검사 메시지 표시 영역
 * 실시간으로 사용자별명의 유효성 상태를 표시
 */
const nicknameMessage = document.getElementById('nicknameMessage');

/**
 * 이메일 유효성 검사 메시지 표시 영역
 * 실시간으로 이메일의 유효성 상태를 표시
 */
const emailMessage = document.getElementById('emailMessage');

/**
 * 비밀번호 확인 메시지 표시 영역
 * 비밀번호 일치 여부를 실시간으로 표시
 */
const confirmPasswordMessage = document.getElementById('confirmPasswordMessage');

/**
 * 비밀번호 강도 표시 영역
 * 비밀번호의 강도를 시각적으로 표시 (색상 바)
 */
const passwordStrength = document.getElementById('passwordStrength');

// ==================== UI 요소 ====================

/**
 * 로딩 스피너
 * API 호출 중 사용자에게 처리 중임을 알리는 오버레이
 */
const loadingSpinner = document.getElementById('loadingSpinner');

/**
 * 메시지 모달
 * 성공/실패 메시지를 표시하는 팝업 창
 */
const messageModal = document.getElementById('messageModal');

/**
 * 모달 제목
 * 모달 창의 제목을 표시
 */
const modalTitle = document.getElementById('modalTitle');

/**
 * 모달 메시지
 * 모달 창의 메인 메시지를 표시
 */
const modalMessage = document.getElementById('modalMessage');

/**
 * 모달 확인 버튼
 * 모달 창을 닫는 버튼
 */
const modalBtn = document.getElementById('modalBtn');

// ==================== 유효성 검사 상태 변수 ====================

/**
 * 사용자명 유효성 상태
 * true: 유효한 사용자명, false: 유효하지 않은 사용자명
 */
let isNicknameValid = false;

/**
 * 이메일 유효성 상태
 * true: 유효한 이메일, false: 유효하지 않은 이메일
 */
let isEmailValid = false;

/**
 * 비밀번호 유효성 상태
 * true: 유효한 비밀번호, false: 유효하지 않은 비밀번호
 */
let isPasswordValid = false;

/**
 * 비밀번호 확인 유효성 상태
 * true: 비밀번호 일치, false: 비밀번호 불일치
 */
let isConfirmPasswordValid = false;

/**
 * 이용약관 동의 상태
 * true: 동의함, false: 동의하지 않음
 */
let isTermsAgreed = false;

// ==================== 이벤트 리스너 등록 ====================

/**
 * DOM 로드 완료 시 초기화 함수
 * 
 * 페이지가 완전히 로드된 후 모든 이벤트 리스너를 등록합니다.
 * 실시간 유효성 검사, 중복 확인, 폼 제출 처리를 설정합니다.
 */
document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소 존재 확인
    if (!signupForm || !nicknameInput || !emailInput || !passwordInput || 
        !confirmPasswordInput || !agreeTermsCheckbox || !signupBtn) {
        console.error('필수 DOM 요소를 찾을 수 없습니다.');
        return;
    }
    
    console.log('회원가입 페이지 초기화 완료');
    
    // 실시간 유효성 검사 이벤트 리스너 등록
    nicknameInput.addEventListener('input', validateNickname);
    emailInput.addEventListener('input', validateEmail);
    passwordInput.addEventListener('input', validatePassword);
    confirmPasswordInput.addEventListener('input', validateConfirmPassword);
    agreeTermsCheckbox.addEventListener('change', validateTerms);
    
    // 폼 제출 이벤트 리스너 등록
    signupForm.addEventListener('submit', handleSignup);
    
    // 사용자명 중복 확인 (디바운싱 적용)
    let nicknameTimeout;
    nicknameInput.addEventListener('input', function() {
        // 이전 타이머를 취소하고 새로운 타이머 설정
        clearTimeout(nicknameTimeout);
        nicknameTimeout = setTimeout(checkNicknameAvailability, 500); // 500ms 지연
    });
    
    // 이메일 중복 확인 (디바운싱 적용)
    let emailTimeout;
    emailInput.addEventListener('input', function() {
        // 이전 타이머를 취소하고 새로운 타이머 설정
        clearTimeout(emailTimeout);
        emailTimeout = setTimeout(checkEmailAvailability, 500); // 500ms 지연
    });
});

// ==================== 유효성 검사 함수들 ====================

/**
 * 사용자명 유효성 검사 함수
 * 
 * 사용자가 입력한 사용자명의 유효성을 실시간으로 검사합니다.
 * 
 * 검사 항목:
 * 1. 빈 값 검사 - 사용자명이 입력되었는지 확인
 * 2. 형식 검사 - 영문, 숫자, 언더스코어만 사용 가능 (3-50자)
 * 3. 시각적 피드백 - 성공/실패 상태에 따른 스타일 적용
 * 4. 메시지 표시 - 사용자에게 적절한 안내 메시지 제공
 * 
 * @returns {void}
 */
function validateNickname() {
    // DOM 요소 존재 확인
    if (!nicknameInput || !nicknameMessage) {
        console.error('사용자별명 관련 DOM 요소를 찾을 수 없습니다.');
        return;
    }
    
    // 입력값에서 앞뒤 공백 제거
    const nickname = nicknameInput.value.trim();
    
    // 사용자별명 정규식 패턴 (한글, 영문, 숫자, 언더스코어만 허용, 띄어쓰기 불가, 3-50자)
    const nicknameRegex = /^[a-zA-Z0-9가-힣_]{3,50}$/;
    
    // 빈 값 검사
    if (nickname.length === 0) {
        showValidationMessage(nicknameMessage, '사용자별명을 입력해주세요.', 'error');
        nicknameInput.classList.remove('success');
        nicknameInput.classList.add('error');
        isNicknameValid = false;
    } 
    // 형식 검사
    else if (!nicknameRegex.test(nickname)) {
        showValidationMessage(nicknameMessage, '사용자별명은 3-50자의 한글, 영문, 숫자, 언더스코어만 사용 가능합니다. 띄어쓰기는 허용되지 않습니다.', 'error');
        nicknameInput.classList.remove('success');
        nicknameInput.classList.add('error');
        isNicknameValid = false;
    } 
    // 유효한 사용자명
    else {
        showValidationMessage(nicknameMessage, '사용 가능한 사용자별명입니다.', 'success');
        nicknameInput.classList.remove('error');
        nicknameInput.classList.add('success');
        isNicknameValid = true;
    }
    
    // 회원가입 버튼 상태 업데이트
    updateSignupButton();
}

// 이메일 유효성 검사
function validateEmail() {
    const email = emailInput.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (email.length === 0) {
        showValidationMessage(emailMessage, '이메일을 입력해주세요.', 'error');
        emailInput.classList.remove('success');
        emailInput.classList.add('error');
        isEmailValid = false;
    } else if (!emailRegex.test(email)) {
        showValidationMessage(emailMessage, '올바른 이메일 형식이 아닙니다.', 'error');
        emailInput.classList.remove('success');
        emailInput.classList.add('error');
        isEmailValid = false;
    } else {
        showValidationMessage(emailMessage, '사용 가능한 이메일입니다.', 'success');
        emailInput.classList.remove('error');
        emailInput.classList.add('success');
        isEmailValid = true;
    }
    
    updateSignupButton();
}

// 비밀번호 유효성 검사
function validatePassword() {
    const password = passwordInput.value;
    
    if (password.length === 0) {
        showPasswordStrength('', '');
        passwordInput.classList.remove('success', 'error');
        isPasswordValid = false;
    } else if (password.length < 8) {
        showPasswordStrength('weak', '비밀번호가 너무 짧습니다.');
        passwordInput.classList.remove('success');
        passwordInput.classList.add('error');
        isPasswordValid = false;
    } else {
        const strength = calculatePasswordStrength(password);
        showPasswordStrength(strength.level, strength.message);
        
        if (strength.level === 'strong' || strength.level === 'good') {
            passwordInput.classList.remove('error');
            passwordInput.classList.add('success');
            isPasswordValid = true;
        } else {
            passwordInput.classList.remove('success');
            passwordInput.classList.add('error');
            isPasswordValid = false;
        }
    }
    
    // 비밀번호 확인도 다시 검사
    if (confirmPasswordInput.value.length > 0) {
        validateConfirmPassword();
    }
    
    updateSignupButton();
}

// 비밀번호 확인 유효성 검사
function validateConfirmPassword() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    
    if (confirmPassword.length === 0) {
        showValidationMessage(confirmPasswordMessage, '비밀번호를 다시 입력해주세요.', 'error');
        confirmPasswordInput.classList.remove('success');
        confirmPasswordInput.classList.add('error');
        isConfirmPasswordValid = false;
    } else if (password !== confirmPassword) {
        showValidationMessage(confirmPasswordMessage, '비밀번호가 일치하지 않습니다.', 'error');
        confirmPasswordInput.classList.remove('success');
        confirmPasswordInput.classList.add('error');
        isConfirmPasswordValid = false;
    } else {
        showValidationMessage(confirmPasswordMessage, '비밀번호가 일치합니다.', 'success');
        confirmPasswordInput.classList.remove('error');
        confirmPasswordInput.classList.add('success');
        isConfirmPasswordValid = true;
    }
    
    updateSignupButton();
}

// 약관 동의 검사
function validateTerms() {
    isTermsAgreed = agreeTermsCheckbox.checked;
    updateSignupButton();
}

// 비밀번호 강도 계산
function calculatePasswordStrength(password) {
    let score = 0;
    let feedback = [];
    
    // 길이 체크
    if (password.length >= 8) score += 1;
    if (password.length >= 12) score += 1;
    
    // 문자 종류 체크
    if (/[a-z]/.test(password)) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/[0-9]/.test(password)) score += 1;
    if (/[^A-Za-z0-9]/.test(password)) score += 1;
    
    if (score <= 2) {
        return { level: 'weak', message: '비밀번호가 너무 약합니다.' };
    } else if (score <= 3) {
        return { level: 'fair', message: '비밀번호가 보통입니다.' };
    } else if (score <= 4) {
        return { level: 'good', message: '비밀번호가 좋습니다.' };
    } else {
        return { level: 'strong', message: '비밀번호가 강합니다.' };
    }
}

// 비밀번호 강도 표시
function showPasswordStrength(level, message) {
    passwordStrength.className = 'password-strength ' + level;
    if (message) {
        passwordStrength.setAttribute('title', message);
    }
}

// 사용자명 중복 확인
async function checkNicknameAvailability() {
    // DOM 요소 존재 확인
    if (!nicknameInput || !nicknameMessage) {
        console.error('사용자별명 관련 DOM 요소를 찾을 수 없습니다.');
        return;
    }
    
    const nickname = nicknameInput.value.trim();
    
    if (nickname.length === 0 || !/^[a-zA-Z0-9가-힣_]{3,50}$/.test(nickname)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/auth/check-nickname/${nickname}`);
        const data = await response.json();
        
        if (data.available) {
            showValidationMessage(nicknameMessage, '사용 가능한 사용자별명입니다.', 'success');
            nicknameInput.classList.remove('error');
            nicknameInput.classList.add('success');
            isNicknameValid = true;
        } else {
            showValidationMessage(nicknameMessage, '이미 사용 중인 사용자별명입니다.', 'error');
            nicknameInput.classList.remove('success');
            nicknameInput.classList.add('error');
            isNicknameValid = false;
        }
    } catch (error) {
        console.error('사용자별명 중복 확인 오류:', error);
    }
    
    updateSignupButton();
}

// 이메일 중복 확인
async function checkEmailAvailability() {
    const email = emailInput.value.trim();
    
    if (email.length === 0 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/auth/check-email/${email}`);
        const data = await response.json();
        
        if (data.available) {
            showValidationMessage(emailMessage, '사용 가능한 이메일입니다.', 'success');
            emailInput.classList.remove('error');
            emailInput.classList.add('success');
            isEmailValid = true;
        } else {
            showValidationMessage(emailMessage, '이미 사용 중인 이메일입니다.', 'error');
            emailInput.classList.remove('success');
            emailInput.classList.add('error');
            isEmailValid = false;
        }
    } catch (error) {
        console.error('이메일 중복 확인 오류:', error);
    }
    
    updateSignupButton();
}

// ==================== 회원가입 처리 ====================

/**
 * 회원가입 폼 제출 처리 함수
 * 
 * 사용자가 회원가입 폼을 제출했을 때 호출되는 메인 함수입니다.
 * 
 * 처리 과정:
 * 1. 기본 폼 제출 동작 방지
 * 2. 전체 폼 유효성 검사
 * 3. 로딩 스피너 표시
 * 4. 회원가입 API 호출
 * 5. 응답 처리 (성공/실패)
 * 6. 로딩 스피너 숨김
 * 
 * @param {Event} event - 폼 제출 이벤트 객체
 * @returns {Promise<void>}
 */
async function handleSignup(event) {
    // 기본 폼 제출 동작 방지 (페이지 새로고침 방지)
    event.preventDefault();
    
    // 전체 폼 유효성 검사
    if (!isFormValid()) {
        showModal('오류', '모든 필드를 올바르게 입력해주세요.');
        return;
    }
    
    // 로딩 스피너 표시
    showLoading(true);
    
    // 폼 데이터 수집 및 정리
    const formData = {
        nickname: nicknameInput.value.trim(),
        email: emailInput.value.trim(),
        password: passwordInput.value,
        confirmPassword: confirmPasswordInput.value
    };
    
    try {
        // 회원가입 API 호출
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        // 응답 데이터 파싱
        const data = await response.json();
        
        // 성공 응답 처리
        if (response.ok && data.success) {
            showModal('성공', '회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.', () => {
                // 성공 시 로그인 페이지로 리다이렉트
                window.location.href = '/login.html';
            });
        } 
        // 실패 응답 처리
        else {
            showModal('오류', data.message || '회원가입 중 오류가 발생했습니다.');
        }
    } 
    // 네트워크 오류 처리
    catch (error) {
        console.error('회원가입 오류:', error);
        showModal('오류', '네트워크 오류가 발생했습니다. 다시 시도해주세요.');
    } 
    // finally 블록 - 항상 실행되는 정리 코드
    finally {
        // 로딩 스피너 숨김
        showLoading(false);
    }
}

// 폼 유효성 검사
function isFormValid() {
    return isNicknameValid && isEmailValid && isPasswordValid && 
           isConfirmPasswordValid && isTermsAgreed;
}

// 회원가입 버튼 상태 업데이트
function updateSignupButton() {
    signupBtn.disabled = !isFormValid();
}

// 검증 메시지 표시
function showValidationMessage(element, message, type) {
    if (element) {
        element.textContent = message;
        element.className = 'validation-message ' + type;
    } else {
        console.error('유효성 검사 메시지 요소를 찾을 수 없습니다:', message);
    }
}

// 비밀번호 표시/숨김 토글
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const button = input.parentElement.querySelector('.toggle-password i');
    
    if (input.type === 'password') {
        input.type = 'text';
        button.className = 'fas fa-eye-slash';
    } else {
        input.type = 'password';
        button.className = 'fas fa-eye';
    }
}

// 로딩 스피너 표시/숨김
function showLoading(show) {
    loadingSpinner.style.display = show ? 'flex' : 'none';
}

// 모달 표시
function showModal(title, message, callback = null) {
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    messageModal.style.display = 'flex';
    
    if (callback) {
        modalBtn.onclick = () => {
            closeModal();
            callback();
        };
    }
}

// 모달 닫기
function closeModal() {
    messageModal.style.display = 'none';
    modalBtn.onclick = closeModal;
}

// 이용약관 표시
function showTerms() {
    showModal('이용약관', '이용약관 내용이 여기에 표시됩니다.');
}

// 개인정보처리방침 표시
function showPrivacy() {
    showModal('개인정보처리방침', '개인정보처리방침 내용이 여기에 표시됩니다.');
} 