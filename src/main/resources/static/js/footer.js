/**
 * 푸터 HTML 생성
 */
function getFooterHTML() {
    return `
        <footer class="footer">
            <div class="footer-content">
                <p>&copy; 2025 Spring Project. All rights reserved.</p>
                <div class="footer-links">
                    <a href="/about.html">소개</a>
                    <a href="/terms.html">이용약관</a>
                    <a href="/privacy.html">개인정보처리방침</a>
                </div>
            </div>
        </footer>
    `;
}

/**
 * 푸터 로드
 */
function loadFooter() {
    const footerContainer = document.getElementById('footer-container');
    if (footerContainer) {
        footerContainer.innerHTML = getFooterHTML();
    }
}

// 페이지 로드 시 푸터 로드
document.addEventListener('DOMContentLoaded', function() {
    loadFooter();
});
