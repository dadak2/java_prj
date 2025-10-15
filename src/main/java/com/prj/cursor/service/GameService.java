package com.prj.cursor.service;

import com.prj.cursor.dto.RankingEntry;
import com.prj.cursor.dto.ScoreEvent;
import com.prj.cursor.entity.GameScore;
import com.prj.cursor.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    
    private final GameScoreRepository gameScoreRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public void submitScore(Long userId, String nickname, Integer score, String gameType) {
        try {
            // 1. DB에 점수 저장
            GameScore gameScore = GameScore.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .score(score)
                    .gameType(gameType)
                    .build();
            
            gameScoreRepository.save(gameScore);
            log.info("점수 저장 완료: userId={}, nickname={}, score={}, gameType={}", 
                    userId, nickname, score, gameType);
            
            // 2. Kafka로 점수 이벤트 전송
            ScoreEvent scoreEvent = ScoreEvent.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .score(score)
                    .gameType(gameType)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            kafkaTemplate.send("game-scores", scoreEvent);
            log.info("Kafka 이벤트 전송 완료: {}", scoreEvent);
            
        } catch (Exception e) {
            log.error("점수 제출 중 오류 발생: userId={}, score={}", userId, score, e);
            throw new RuntimeException("점수 제출에 실패했습니다.", e);
        }
    }
    
    public List<RankingEntry> getTopRankings(String gameType, int limit) {
        try {
            String key = "ranking:" + gameType;
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            
            // Redis에서 상위 랭킹 조회
            Set<ZSetOperations.TypedTuple<Object>> rankings = zSetOps.reverseRangeWithScores(key, 0, limit - 1);
            
            List<RankingEntry> result = new ArrayList<>();
            int rank = 1;
            
            for (ZSetOperations.TypedTuple<Object> ranking : rankings) {
                String nickname = (String) ranking.getValue();
                Double score = ranking.getScore();
                
                if (nickname != null && score != null) {
                    RankingEntry entry = RankingEntry.builder()
                            .nickname(nickname)
                            .score(score.intValue())
                            .rank(rank++)
                            .playedAt(LocalDateTime.now())
                            .build();
                    result.add(entry);
                }
            }
            
            log.info("랭킹 조회 완료: gameType={}, count={}", gameType, result.size());
            return result;
            
        } catch (Exception e) {
            log.error("랭킹 조회 중 오류 발생: gameType={}", gameType, e);
            return new ArrayList<>();
        }
    }
    
    public void updateRanking(ScoreEvent scoreEvent) {
        try {
            String key = "ranking:" + scoreEvent.getGameType();
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            
            // Redis에 점수 업데이트 (닉네임을 멤버로, 점수를 스코어로)
            zSetOps.add(key, scoreEvent.getNickname(), scoreEvent.getScore());
            
            // 상위 1000명만 유지 (메모리 절약)
            zSetOps.removeRange(key, 0, -1001);
            
            log.info("Redis 랭킹 업데이트 완료: nickname={}, score={}, gameType={}", 
                    scoreEvent.getNickname(), scoreEvent.getScore(), scoreEvent.getGameType());
            
        } catch (Exception e) {
            log.error("Redis 랭킹 업데이트 중 오류 발생: {}", scoreEvent, e);
        }
    }
}
