// 상세보기 페이지 JavaScript
let currentPost = null;
let currentUser = null;
let isLiked = false;

document.addEventListener('DOMContentLoaded', function() {
    // URL에서 게시글 번호 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const boardNo = urlParams.get('boardNo');
    
    if (!boardNo) {
        showModal('오류', '게시글 번호가 없습니다.', 'error', () => {
            window.location.href = '/';
        });
        return;
    }
    
    // 현재 사용자 정보 가져오기
    currentUser = getCurrentUser();
    
    // 게시글 로딩
    loadPost(boardNo);
});

// 게시글 로딩
async function loadPost(boardNo) {
    showLoadingSpinner();
    
    try {
        const response = await fetch(`/api/boards/${boardNo}`);
        
        if (!response.ok) {
            throw new Error('게시글을 찾을 수 없습니다.');
        }
        
        const post = await response.json();
        currentPost = post;
        
        // 게시글 정보 표시
        displayPost(post);
        
        // 디버깅 정보 출력
        console.log('현재 사용자:', currentUser);
        console.log('게시글 작성자:', post.author);
        console.log('게시글 활성화 상태:', post.isActive);
        
        // 작성자 액션 버튼 표시 여부 결정
        let canManagePost = false;
        
        if (currentUser && post.author && currentUser.userNo === post.author.userNo) {
            console.log('작성자 권한 확인됨');
            canManagePost = true;
        }
        
        // 관리자 권한 확인 (관리자도 모든 게시글 관리 가능)
        if (currentUser && currentUser.userRole === 'ADMIN') {
            console.log('관리자 권한 확인됨');
            canManagePost = true;
        }
        
        console.log('게시글 관리 권한:', canManagePost);
        
        if (canManagePost) {
            console.log('작성자 액션 버튼 표시');
            document.getElementById('authorActions').style.display = 'flex';
            
            // 게시글 활성화 상태에 따라 버튼 표시
            if (post.isActive) {
                document.getElementById('deactivateBtn').style.display = 'inline-block';
                document.getElementById('activateBtn').style.display = 'none';
            } else {
                document.getElementById('deactivateBtn').style.display = 'none';
                document.getElementById('activateBtn').style.display = 'inline-block';
            }
        }
        
        // 댓글 로딩 (향후 구현)
        // loadComments(boardNo);
        
    } catch (error) {
        console.error('게시글 로딩 오류:', error);
        showModal('오류', error.message || '게시글을 불러오는 중 오류가 발생했습니다.', 'error', () => {
            window.location.href = '/';
        });
    } finally {
        hideLoadingSpinner();
    }
}

// 게시글 정보 표시
function displayPost(post) {
    // 제목
    document.getElementById('postTitle').textContent = post.title;
    
    // 카테고리
    document.getElementById('categoryText').textContent = post.category;
    
    // 작성일
    const createdDate = new Date(post.createdAt);
    document.getElementById('dateText').textContent = formatDate(createdDate);
    
    // 작성자
    document.getElementById('authorName').textContent = post.author?.nickname || '알 수 없음';
    
    // 통계
    document.getElementById('viewCount').textContent = post.viewCount || 0;
    document.getElementById('likeCount').textContent = post.likeCount || 0;
    document.getElementById('commentCount').textContent = post.commentCount || 0;
    
    // 내용
    document.getElementById('postContent').textContent = post.content;
    
    // 페이지 제목 업데이트
    document.title = `${post.title} - Cursor Project`;
}

// 날짜 포맷팅
function formatDate(date) {
    const now = new Date();
    const diff = now - date;
    const diffMinutes = Math.floor(diff / (1000 * 60));
    const diffHours = Math.floor(diff / (1000 * 60 * 60));
    const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (diffMinutes < 1) {
        return '방금 전';
    } else if (diffMinutes < 60) {
        return `${diffMinutes}분 전`;
    } else if (diffHours < 24) {
        return `${diffHours}시간 전`;
    } else if (diffDays < 7) {
        return `${diffDays}일 전`;
    } else {
        return date.toLocaleDateString('ko-KR');
    }
}

// 좋아요 토글
async function toggleLike() {
    if (!currentUser) {
        showModal('로그인 필요', '좋아요를 누르려면 로그인이 필요합니다.', 'info', () => {
            showLoginModal();
        });
        return;
    }
    
    if (!currentPost) return;
    
    try {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeText = document.getElementById('likeText');
        const likeCount = document.getElementById('likeCount');
        
        if (isLiked) {
            // 좋아요 취소
            const response = await fetch(`/api/boards/${currentPost.boardNo}/like`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                isLiked = false;
                likeBtn.classList.remove('liked');
                likeText.textContent = '좋아요';
                currentPost.likeCount = Math.max(0, currentPost.likeCount - 1);
                likeCount.textContent = currentPost.likeCount;
            }
        } else {
            // 좋아요 추가
            const response = await fetch(`/api/boards/${currentPost.boardNo}/like`, {
                method: 'POST'
            });
            
            if (response.ok) {
                isLiked = true;
                likeBtn.classList.add('liked');
                likeText.textContent = '좋아요 취소';
                currentPost.likeCount = (currentPost.likeCount || 0) + 1;
                likeCount.textContent = currentPost.likeCount;
            }
        }
    } catch (error) {
        console.error('좋아요 토글 오류:', error);
        showModal('오류', '좋아요 처리 중 오류가 발생했습니다.', 'error');
    }
}

// 댓글 작성
async function submitComment() {
    if (!currentUser) {
        showModal('로그인 필요', '댓글을 작성하려면 로그인이 필요합니다.', 'info', () => {
            showLoginModal();
        });
        return;
    }
    
    const commentText = document.getElementById('commentText').value.trim();
    
    if (!commentText) {
        showModal('입력 오류', '댓글 내용을 입력해주세요.', 'error');
        return;
    }
    
    try {
        // 댓글 API 호출 (향후 구현)
        // const response = await fetch(`/api/boards/${currentPost.boardNo}/comments`, {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     },
        //     body: JSON.stringify({
        //         content: commentText,
        //         userNo: currentUser.userNo
        //     })
        // });
        
        // 임시로 성공 메시지 표시
        showModal('댓글 작성', '댓글이 작성되었습니다. (댓글 기능은 향후 구현 예정)', 'success');
        
        // 댓글 입력창 초기화
        document.getElementById('commentText').value = '';
        
        // 댓글 수 증가
        currentPost.commentCount = (currentPost.commentCount || 0) + 1;
        document.getElementById('commentCount').textContent = currentPost.commentCount;
        
    } catch (error) {
        console.error('댓글 작성 오류:', error);
        showModal('오류', '댓글 작성 중 오류가 발생했습니다.', 'error');
    }
}

// 댓글 입력창 포커스
function focusComment() {
    if (!currentUser) {
        showModal('로그인 필요', '댓글을 작성하려면 로그인이 필요합니다.', 'error');
        return;
    }
    
    document.getElementById('commentText').focus();
}

// 게시글 공유
function sharePost() {
    if (navigator.share) {
        navigator.share({
            title: currentPost.title,
            text: currentPost.content.substring(0, 100) + '...',
            url: window.location.href
        });
    } else {
        // 클립보드에 URL 복사
        navigator.clipboard.writeText(window.location.href).then(() => {
            showModal('공유', '게시글 링크가 클립보드에 복사되었습니다.', 'success');
        }).catch(() => {
            showModal('공유', '게시글 링크 복사에 실패했습니다.', 'error');
        });
    }
}

// 게시글 수정
function editPost() {
    if (!currentUser) {
        showModal('로그인 필요', '게시글을 수정하려면 로그인이 필요합니다.', 'info', () => {
            showLoginModal();
        });
        return;
    }
    
    if (!currentPost) return;
    
    showConfirmModal('게시글 수정', '게시글을 수정하시겠습니까?', () => {
        // 수정 페이지로 이동 (향후 구현)
        showModal('알림', '게시글 수정 기능은 향후 구현 예정입니다.', 'info');
    });
}

// 게시글 삭제
function deletePost() {
    if (!currentUser) {
        showModal('로그인 필요', '게시글을 삭제하려면 로그인이 필요합니다.', 'info', () => {
            showLoginModal();
        });
        return;
    }
    
    if (!currentPost) return;
    
    showConfirmModal('게시글 삭제', '정말로 이 게시글을 삭제하시겠습니까?\n삭제된 게시글은 복구할 수 없습니다.', async () => {
        try {
            const response = await fetch(`/api/boards/${currentPost.boardNo}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                showModal('삭제 완료', '게시글이 삭제되었습니다.', 'success', () => {
                    window.location.href = '/';
                });
            } else {
                throw new Error('게시글 삭제에 실패했습니다.');
            }
        } catch (error) {
            console.error('게시글 삭제 오류:', error);
            showModal('오류', error.message || '게시글 삭제 중 오류가 발생했습니다.', 'error');
        }
    });
}

// 게시글 비활성화
function deactivatePost() {
    if (!currentPost || !currentUser) return;
    
    showConfirmModal('게시글 비활성화', '이 게시글을 비활성화하시겠습니까?\n비활성화된 게시글은 목록에서 보이지 않으며, 관련 댓글도 함께 비활성화됩니다.', async () => {
        try {
            const response = await fetch(`/api/boards/${currentPost.boardNo}/deactivate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userNo: currentUser.userNo
                })
            });
            
            if (response.ok) {
                showModal('비활성화 완료', '게시글이 비활성화되었습니다.', 'success', () => {
                    // 페이지 새로고침하여 변경사항 반영
                    window.location.reload();
                });
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || '게시글 비활성화에 실패했습니다.');
            }
        } catch (error) {
            console.error('게시글 비활성화 오류:', error);
            showModal('오류', error.message || '게시글 비활성화 중 오류가 발생했습니다.', 'error');
        }
    });
}

// 게시글 활성화
function activatePost() {
    if (!currentPost || !currentUser) return;
    
    showConfirmModal('게시글 활성화', '이 게시글을 활성화하시겠습니까?\n활성화된 게시글은 목록에서 보이며, 관련 댓글도 함께 활성화됩니다.', async () => {
        try {
            const response = await fetch(`/api/boards/${currentPost.boardNo}/activate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userNo: currentUser.userNo
                })
            });
            
            if (response.ok) {
                showModal('활성화 완료', '게시글이 활성화되었습니다.', 'success', () => {
                    // 페이지 새로고침하여 변경사항 반영
                    window.location.reload();
                });
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || '게시글 활성화에 실패했습니다.');
            }
        } catch (error) {
            console.error('게시글 활성화 오류:', error);
            showModal('오류', error.message || '게시글 활성화 중 오류가 발생했습니다.', 'error');
        }
    });
}

// 현재 사용자 정보 가져오기
function getCurrentUser() {
    // localStorage에서 사용자 정보 가져오기 (login.js와 일치)
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
        try {
            return JSON.parse(userInfo);
        } catch (e) {
            console.error('사용자 정보 파싱 오류:', e);
            return null;
        }
    }
    return null;
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

// 로그인 성공 후 처리
function onLoginSuccess(data) {
    // 현재 사용자 정보 업데이트
    currentUser = data.user;
    
    // 페이지 새로고침하여 UI 업데이트
    setTimeout(() => {
        window.location.reload();
    }, 1500);
}

// 모달 표시
function showModal(title, message, type = 'info', callback = null) {
    const modal = document.getElementById('messageModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalBtn = document.getElementById('modalBtn');
    
    if (modal && modalTitle && modalMessage && modalBtn) {
        // 기존 타입 클래스 제거
        modal.classList.remove('error', 'success', 'info');
        
        // 새로운 타입 클래스 추가
        modal.classList.add(type);
        
        modalTitle.textContent = title;
        modalMessage.textContent = message;
        
        modal.style.display = 'flex';
        
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

// 확인 모달 표시
function showConfirmModal(title, message, callback) {
    const modal = document.getElementById('confirmModal');
    const modalTitle = document.getElementById('confirmTitle');
    const modalMessage = document.getElementById('confirmMessage');
    const confirmBtn = document.getElementById('confirmBtn');
    
    if (modal && modalTitle && modalMessage && confirmBtn) {
        modalTitle.textContent = title;
        modalMessage.textContent = message;
        
        modal.style.display = 'flex';
        
        confirmBtn.onclick = function() {
            closeConfirmModal();
            if (callback) callback();
        };
    }
}

// 확인 모달 닫기
function closeConfirmModal() {
    const modal = document.getElementById('confirmModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 뒤로 가기
function goBack() {
    window.history.back();
}

// 글쓰기 페이지로 이동
function goToWrite() {
    window.location.href = '/write.html';
} 