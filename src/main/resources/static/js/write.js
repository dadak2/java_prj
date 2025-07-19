// 글쓰기 페이지 JavaScript

// 현재 로그인된 사용자 정보 가져오기
function getCurrentUser() {
    // localStorage에서 사용자 정보 가져오기 (login.js와 일치)
    const userInfo = localStorage.getItem('userInfo');
    console.log('localStorage에서 가져온 userInfo:', userInfo);
    
    if (userInfo) {
        try {
            const user = JSON.parse(userInfo);
            console.log('파싱된 사용자 정보:', user);
            return user;
        } catch (e) {
            console.error('사용자 정보 파싱 오류:', e);
            return null;
        }
    }
    
    console.log('사용자 정보가 없습니다. 로그인이 필요합니다.');
    return null;
    }

document.addEventListener('DOMContentLoaded', function() {
    console.log('글쓰기 페이지 로드됨');
    
    // 로그인 상태 확인
    const currentUser = getCurrentUser();
    if (!currentUser || !currentUser.userNo) {
        console.log('로그인되지 않은 사용자. 로그인 페이지로 리다이렉트');
        showModal('로그인 필요', '글쓰기를 위해서는 로그인이 필요합니다.', 'error', () => {
            window.location.href = '/login.html';
        });
        return;
    }
    
    console.log('로그인된 사용자:', currentUser);
    
    const writeForm = document.getElementById('writeForm');
    const titleInput = document.getElementById('title');
    const contentTextarea = document.getElementById('content');
    const categorySelect = document.getElementById('category');
    const submitBtn = document.getElementById('submitBtn');
    
    const titleCharCount = document.getElementById('titleCharCount');
    const contentCharCount = document.getElementById('contentCharCount');
    
    // 문자 수 카운트 업데이트
    function updateCharCount(input, counter, maxLength = null) {
        const length = input.value.length;
        counter.textContent = length;
        
        if (maxLength) {
            counter.textContent = length + '/' + maxLength;
            if (length > maxLength * 0.8) {
                counter.style.color = '#ff6b6b';
            } else {
                counter.style.color = '#667eea';
            }
        }
    }
    
    // 제목 문자 수 카운트
    titleInput.addEventListener('input', function() {
        updateCharCount(this, titleCharCount, 200);
        validateTitle();
    });
    
    // 내용 문자 수 카운트
    contentTextarea.addEventListener('input', function() {
        updateCharCount(this, contentCharCount);
        validateContent();
    });
    
    // 카테고리 변경 시 검증
    categorySelect.addEventListener('change', validateCategory);
    
    // 폼 제출 이벤트
    writeForm.addEventListener('submit', handleSubmit);
    
    // 검증 함수들
    function validateTitle() {
        const title = titleInput.value.trim();
        const messageElement = document.getElementById('titleMessage');
        
        if (!title) {
            showValidationMessage(messageElement, '제목을 입력해주세요.', 'error');
            return false;
        }
        
        if (title.length < 2) {
            showValidationMessage(messageElement, '제목은 2자 이상 입력해주세요.', 'error');
            return false;
        }
        
        if (title.length > 200) {
            showValidationMessage(messageElement, '제목은 200자 이하로 입력해주세요.', 'error');
            return false;
        }
        
        showValidationMessage(messageElement, '올바른 제목입니다.', 'success');
        return true;
    }
    
    function validateContent() {
        const content = contentTextarea.value.trim();
        const messageElement = document.getElementById('contentMessage');
        
        if (!content) {
            showValidationMessage(messageElement, '내용을 입력해주세요.', 'error');
            return false;
        }
        
        if (content.length < 10) {
            showValidationMessage(messageElement, '내용은 10자 이상 입력해주세요.', 'error');
            return false;
        }
        
        showValidationMessage(messageElement, '올바른 내용입니다.', 'success');
        return true;
    }
    
    function validateCategory() {
        const category = categorySelect.value;
        const messageElement = document.getElementById('categoryMessage');
        
        if (!category) {
            showValidationMessage(messageElement, '카테고리를 선택해주세요.', 'error');
            return false;
        }
        
        showValidationMessage(messageElement, '카테고리가 선택되었습니다.', 'success');
        return true;
    }
    
    function showValidationMessage(element, message, type) {
        if (element) {
            element.textContent = message;
            element.className = 'validation-message ' + type;
        }
    }
    
    // 폼 제출 처리
    async function handleSubmit(e) {
        e.preventDefault();
        
        // 모든 필드 검증
        const isTitleValid = validateTitle();
        const isContentValid = validateContent();
        const isCategoryValid = validateCategory();
        
        if (!isTitleValid || !isContentValid || !isCategoryValid) {
            showModal('입력 오류', '모든 필드를 올바르게 입력해주세요.', 'error');
            return;
        }
        
        // 로딩 스피너 표시
        showLoadingSpinner();
        submitBtn.disabled = true;
        
        try {
            // 현재 로그인된 사용자 정보 가져오기 (세션에서)
            const currentUser = getCurrentUser();
            if (!currentUser || !currentUser.userNo) {
                throw new Error('로그인이 필요합니다.');
            }
            
            const formData = {
                title: titleInput.value.trim(),
                content: contentTextarea.value.trim(),
                category: categorySelect.value,
                userNo: currentUser.userNo
            };
            
            const response = await fetch('/api/boards', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '게시글 등록에 실패했습니다.');
            }
            
            const result = await response.json();
            
            hideLoadingSpinner();
            submitBtn.disabled = false;
            
            // 폼 제출 성공 시 beforeunload 이벤트 제거
            window.removeEventListener('beforeunload', handleBeforeUnload);
            
            showModal('등록 성공', '게시글이 성공적으로 등록되었습니다.', 'success', () => {
                // 게시판 페이지로 이동
                window.location.href = '/board.html';
            });
            
        } catch (error) {
            hideLoadingSpinner();
            submitBtn.disabled = false;
            
            console.error('게시글 등록 오류:', error);
            showModal('등록 실패', error.message || '게시글 등록 중 오류가 발생했습니다.', 'error');
        }
    }
    
    // 로딩 스피너 표시/숨김
    function showLoadingSpinner() {
        const spinner = document.getElementById('loadingSpinner');
        if (spinner) {
            spinner.style.display = 'flex';
        }
    }
    
    function hideLoadingSpinner() {
        const spinner = document.getElementById('loadingSpinner');
        if (spinner) {
            spinner.style.display = 'none';
            }
}

// 모달 표시
function showModal(title, message, type = 'info', callback = null) {
    const modal = document.getElementById('messageModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalBtn = document.getElementById('modalBtn');
    
    if (modal && modalTitle && modalMessage && modalBtn) {
        modalTitle.textContent = title;
        modalMessage.textContent = message;
        
        // 타입에 따른 스타일 적용
        modalTitle.className = type === 'error' ? 'error' : type === 'success' ? 'success' : '';
        
        modal.style.display = 'flex';
        
        // 콜백이 있으면 버튼 클릭 시 실행
        if (callback) {
            modalBtn.onclick = function() {
                closeModal();
                callback();
            };
        } else {
            modalBtn.onclick = closeModal;
        }
    }
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('messageModal');
    if (modal) {
        modal.style.display = 'none';
    }
}
    
    // 모달 닫기 (전역 함수로 등록)
    window.closeModal = closeModal;
    
    // 뒤로 가기
    window.goBack = function() {
        if (confirm('작성 중인 내용이 사라집니다. 정말 나가시겠습니까?')) {
            window.history.back();
        }
    };
    
    // 페이지 떠날 때 경고 핸들러
    function handleBeforeUnload(e) {
        const title = titleInput.value.trim();
        const content = contentTextarea.value.trim();
        
        if (title || content) {
            e.preventDefault();
            e.returnValue = '작성 중인 내용이 있습니다. 정말 나가시겠습니까?';
            return '작성 중인 내용이 있습니다. 정말 나가시겠습니까?';
        }
    }
    
    // beforeunload 이벤트 등록
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    // 초기 검증 메시지 초기화
    document.getElementById('titleMessage').textContent = '';
    document.getElementById('contentMessage').textContent = '';
    document.getElementById('categoryMessage').textContent = '';
}); 