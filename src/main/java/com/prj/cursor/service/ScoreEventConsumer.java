package com.prj.cursor.service;

import com.prj.cursor.dto.RankingEntry;
import com.prj.cursor.dto.ScoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreEventConsumer {
    
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @KafkaListener(topics = "game-scores", groupId = "game-ranking-group")
    public void handleScoreEvent(ScoreEvent scoreEvent) {
        try {
            log.info("Kafka 메시지 수신: {}", scoreEvent);
            
            // 1. Redis에 랭킹 업데이트
            gameService.updateRanking(scoreEvent);
            
            // 2. 실시간 랭킹 전송
            List<RankingEntry> rankings = gameService.getTopRankings(scoreEvent.getGameType(), 10);
            String destination = "/topic/rankings/" + scoreEvent.getGameType();
            
            messagingTemplate.convertAndSend(destination, rankings);
            log.info("실시간 랭킹 전송 완료: destination={}, count={}", destination, rankings.size());
            
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: {}", scoreEvent, e);
        }
    }
}
