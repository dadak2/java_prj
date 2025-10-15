/**
 * 공통 JavaScript 함수들
 * 
 * 모든 페이지에서 공통으로 사용되는 모달 관련 함수들을 정의합니다.
 */

/**
 * 모달 메시지 표시 (공통)
 */
function showModal(title, message, type, callback) {
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const messageModal = document.getElementById('messageModal');
    const modalBtn = document.getElementById('modalBtn');
    
    if (!modalTitle || !modalMessage || !messageModal || !modalBtn) {
        console.warn('모달 요소를 찾을 수 없습니다.');
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
    
    // 콜백이 있으면 확인 버튼에 연결
    if (callback && typeof callback === 'function') {
        modalBtn.onclick = function() {
            closeModal();
            callback();
        };
    } else {
        // 확인 버튼 기본 동작 복원
        modalBtn.onclick = closeModal;
    }
}

/**
 * 모달 닫기
 */
function closeModal() {
    const messageModal = document.getElementById('messageModal');
    if (messageModal) {
        messageModal.style.display = 'none';
    }
}


// 모달 외부 클릭 시 닫기
window.addEventListener('click', function(event) {
    const messageModal = document.getElementById('messageModal');
    if (messageModal && event.target === messageModal) {
        closeModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    const messageModal = document.getElementById('messageModal');
    if (event.key === 'Escape' && messageModal && messageModal.style.display === 'flex') {
        closeModal();
    }
});
