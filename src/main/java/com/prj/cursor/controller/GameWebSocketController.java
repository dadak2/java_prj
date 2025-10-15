package com.prj.cursor.controller;

import com.prj.cursor.dto.ScoreSubmission;
import com.prj.cursor.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {
    
    private final GameService gameService;
    
    @MessageMapping("/game/score")
    @SendTo("/topic/rankings")
    public void submitScore(ScoreSubmission scoreSubmission) {
        try {
            log.info("WebSocket 점수 제출: {}", scoreSubmission);
            
            gameService.submitScore(
                scoreSubmission.getUserId(),
                scoreSubmission.getNickname(),
                scoreSubmission.getScore(),
                scoreSubmission.getGameType()
            );
            
        } catch (Exception e) {
            log.error("WebSocket 점수 제출 중 오류 발생: {}", scoreSubmission, e);
        }
    }
}
