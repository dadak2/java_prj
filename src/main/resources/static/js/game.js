class SnakeGame {
    constructor() {
        this.canvas = document.getElementById('gameCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.gridSize = 20;
        this.tileCountX = this.canvas.width / this.gridSize;  // 가로 타일 수
        this.tileCountY = this.canvas.height / this.gridSize; // 세로 타일 수
        
        this.snake = [{x: 10, y: 10}];
        this.food = {};
        this.dx = 0;
        this.dy = 0;
        this.score = 0;
        this.isPlaying = false;
        this.gameLoop = null;
        this.gameType = 'snake';
        
        this.initWebSocket();
        this.initEventListeners();
        this.generateFood();
        this.loadRankings();
    }
    
    initWebSocket() {
        this.socket = new SockJS('/ws');
        this.stompClient = Stomp.over(this.socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('WebSocket 연결됨: ' + frame);
            
            // 실시간 랭킹 구독
            this.stompClient.subscribe('/topic/rankings/' + this.gameType, (message) => {
                const rankings = JSON.parse(message.body);
                this.updateRankingDisplay(rankings);
            });
        }, (error) => {
            console.error('WebSocket 연결 실패:', error);
        });
    }
    
    initEventListeners() {
        document.getElementById('startBtn').addEventListener('click', () => this.startGame());
        document.getElementById('pauseBtn').addEventListener('click', () => this.pauseGame());
        document.getElementById('resetBtn').addEventListener('click', () => this.resetGame());
        
        // 키보드 이벤트
        document.addEventListener('keydown', (e) => {
            if (!this.isPlaying) return;
            
            // 방향키인 경우 기본 동작(스크롤) 방지
            if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
                e.preventDefault();
            }
            
            switch(e.key) {
                case 'ArrowUp':
                    if (this.dy !== 1) {
                        this.dx = 0;
                        this.dy = -1;
                    }
                    break;
                case 'ArrowDown':
                    if (this.dy !== -1) {
                        this.dx = 0;
                        this.dy = 1;
                    }
                    break;
                case 'ArrowLeft':
                    if (this.dx !== 1) {
                        this.dx = -1;
                        this.dy = 0;
                    }
                    break;
                case 'ArrowRight':
                    if (this.dx !== -1) {
                        this.dx = 1;
                        this.dy = 0;
                    }
                    break;
            }
        });
    }
    
    startGame() {
        if (this.isPlaying) return;
        
        this.isPlaying = true;
        document.getElementById('startBtn').disabled = true;
        document.getElementById('pauseBtn').disabled = false;
        
        // 게임 시작 안내
        alert('방향키를 눌러서 뱀을 움직이세요!');
        
        this.gameLoop = setInterval(() => {
            this.update();
            this.draw();
        }, 150);
    }
    
    pauseGame() {
        this.isPlaying = false;
        clearInterval(this.gameLoop);
        document.getElementById('startBtn').disabled = false;
        document.getElementById('pauseBtn').disabled = true;
    }
    
    resetGame() {
        this.pauseGame();
        this.snake = [{x: 10, y: 10}];
        this.dx = 0;
        this.dy = 0;
        this.score = 0;
        this.generateFood();
        this.updateScore();
        this.draw();
        
        // 리셋 완료 메시지
        console.log('게임이 리셋되었습니다. 새 게임을 시작하세요!');
    }
    
    update() {
        if (!this.isPlaying) return;
        
        // 뱀이 움직이지 않으면 업데이트하지 않음
        if (this.dx === 0 && this.dy === 0) return;
        
        const head = {x: this.snake[0].x + this.dx, y: this.snake[0].y + this.dy};
        
        // 벽 충돌 검사
        if (head.x < 0 || head.x >= this.tileCountX || head.y < 0 || head.y >= this.tileCountY) {
            this.gameOver();
            return;
        }
        
        // 자기 자신과 충돌 검사 (머리와 몸통이 겹치는지 확인)
        for (let i = 1; i < this.snake.length; i++) {
            if (head.x === this.snake[i].x && head.y === this.snake[i].y) {
                this.gameOver();
                return;
            }
        }
        
        this.snake.unshift(head);
        
        // 음식 먹기 검사
        if (this.food && this.food.x !== undefined && this.food.y !== undefined && 
            head.x === this.food.x && head.y === this.food.y) {
            this.score += 10;
            this.updateScore();
            this.generateFood();
        } else {
            this.snake.pop();
        }
    }
    
    draw() {
        // 캔버스 클리어
        this.ctx.fillStyle = '#f8f9fa';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        
        // 뱀 그리기
        this.ctx.fillStyle = '#28a745';
        for (let segment of this.snake) {
            this.ctx.fillRect(segment.x * this.gridSize, segment.y * this.gridSize, this.gridSize - 2, this.gridSize - 2);
        }
        
        // 음식 그리기
        if (this.food && this.food.x !== undefined && this.food.y !== undefined) {
            this.ctx.fillStyle = '#dc3545';
            this.ctx.fillRect(this.food.x * this.gridSize, this.food.y * this.gridSize, this.gridSize - 2, this.gridSize - 2);
        }
    }
    
    generateFood() {
        let newFood;
        let isColliding = true;
        let attempts = 0;
        const maxAttempts = 100; // 무한 루프 방지
        
        // 뱀과 겹치지 않는 위치를 찾을 때까지 반복
        while (isColliding && attempts < maxAttempts) {
            newFood = {
                x: Math.floor(Math.random() * this.tileCountX),
                y: Math.floor(Math.random() * this.tileCountY)
            };
            
            isColliding = false;
            for (let segment of this.snake) {
                if (newFood.x === segment.x && newFood.y === segment.y) {
                    isColliding = true;
                    break;
                }
            }
            attempts++;
        }
        
        this.food = newFood;
        console.log('음식 생성:', this.food); // 디버깅용
    }
    
    updateScore() {
        document.getElementById('currentScore').textContent = this.score;
    }
    
    gameOver() {
        // 즉시 게임 상태 변경
        this.isPlaying = false;
        clearInterval(this.gameLoop);
        
        // 버튼 상태 즉시 변경
        document.getElementById('startBtn').disabled = false;
        document.getElementById('pauseBtn').disabled = true;
        
        // 최종 그리기 (충돌 지점 표시)
        this.draw();
        
        // 점수 제출 및 알림
        this.submitScore();
        alert(`게임 종료! 최종 점수: ${this.score}점`);
        
        // 2초 후 자동 리셋
        setTimeout(() => {
            this.resetGame();
        }, 2000);
    }
    
    submitScore() {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        
        if (!userInfo.userNo) {
            showModal('로그인 필요', '점수를 저장하려면 로그인이 필요합니다.', 'info');
            return;
        }
        
        const scoreData = {
            userId: userInfo.userNo,
            nickname: userInfo.nickname,
            score: this.score,
            gameType: this.gameType
        };
        
        // WebSocket으로 점수 전송
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send('/app/game/score', {}, JSON.stringify(scoreData));
        } else {
            // WebSocket이 연결되지 않은 경우 HTTP API 사용
            this.submitScoreViaAPI(scoreData);
        }
    }
    
    async submitScoreViaAPI(scoreData) {
        try {
            const response = await fetch('/api/game/score', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(scoreData)
            });
            
            if (response.ok) {
                console.log('점수 제출 성공');
            } else {
                console.error('점수 제출 실패');
            }
        } catch (error) {
            console.error('점수 제출 중 오류:', error);
        }
    }
    
    async loadRankings() {
        try {
            const response = await fetch(`/api/game/rankings/${this.gameType}?limit=10`);
            const rankings = await response.json();
            this.updateRankingDisplay(rankings);
        } catch (error) {
            console.error('랭킹 로드 오류:', error);
            document.getElementById('rankingList').innerHTML = '<div class="loading">랭킹을 불러올 수 없습니다.</div>';
        }
    }
    
    updateRankingDisplay(rankings) {
        const rankingList = document.getElementById('rankingList');
        
        if (rankings.length === 0) {
            rankingList.innerHTML = '<div class="loading">아직 랭킹이 없습니다.</div>';
            return;
        }
        
        rankingList.innerHTML = '';
        
        rankings.forEach((entry, index) => {
            const rankItem = document.createElement('div');
            rankItem.className = 'rank-item';
            rankItem.innerHTML = `
                <span class="rank">${entry.rank || index + 1}</span>
                <span class="nickname">${entry.nickname}</span>
                <span class="score">${entry.score}</span>
            `;
            rankingList.appendChild(rankItem);
        });
    }
}

// 게임 시작
document.addEventListener('DOMContentLoaded', () => {
    new SnakeGame();
});
