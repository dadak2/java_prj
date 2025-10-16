package com.prj.cursor.controller;

import com.prj.cursor.dto.RankingEntry;
import com.prj.cursor.dto.ScoreSubmission;
import com.prj.cursor.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {
    
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/game/score")
    public void submitScore(ScoreSubmission scoreSubmission) {
        try {
            log.info("WebSocket 점수 제출: {}", scoreSubmission);
            
            // 점수 제출
            gameService.submitScore(
                scoreSubmission.getUserId(),
                scoreSubmission.getNickname(),
                scoreSubmission.getScore(),
                scoreSubmission.getGameType()
            );
            
            // 업데이트된 랭킹 조회 및 전송
            List<RankingEntry> rankings = gameService.getTopRankings(scoreSubmission.getGameType(), 10);
            messagingTemplate.convertAndSend("/topic/rankings/" + scoreSubmission.getGameType(), rankings);
            
            log.info("실시간 랭킹 전송 완료: gameType={}, count={}", scoreSubmission.getGameType(), rankings.size());
            
        } catch (Exception e) {
            log.error("WebSocket 점수 제출 중 오류 발생: {}", scoreSubmission, e);
        }
    }
    
    /**
     * 랭킹 요청 처리
     */
    @MessageMapping("/game/rankings")
    @SendTo("/topic/rankings")
    public List<RankingEntry> getRankings(String gameType) {
        try {
            log.info("WebSocket 랭킹 요청: gameType={}", gameType);
            return gameService.getTopRankings(gameType, 10);
        } catch (Exception e) {
            log.error("WebSocket 랭킹 조회 중 오류 발생: gameType={}", gameType, e);
            return List.of();
        }
    }
}
