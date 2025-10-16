package com.prj.cursor.service;

import com.prj.cursor.dto.RankingEntry;
import com.prj.cursor.dto.ScoreEvent;
import com.prj.cursor.entity.GameScore;
import com.prj.cursor.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class GameService {
    
    @Autowired
    private GameScoreRepository gameScoreRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;
    
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
            
            // 2. Redis에 직접 랭킹 업데이트 (Kafka 없이)
            updateRankingDirectly(userId, nickname, score, gameType);
            
            // 3. Kafka로 점수 이벤트 전송 (선택적)
            if (kafkaTemplate != null) {
                try {
                    ScoreEvent scoreEvent = ScoreEvent.builder()
                            .userId(userId)
                            .nickname(nickname)
                            .score(score)
                            .gameType(gameType)
                            .timestamp(System.currentTimeMillis())
                            .build();
                    
                    kafkaTemplate.send("game-scores", scoreEvent);
                    log.info("Kafka 이벤트 전송 완료: {}", scoreEvent);
                } catch (Exception kafkaError) {
                    log.warn("Kafka 전송 실패, Redis만 업데이트: {}", kafkaError.getMessage());
                }
            } else {
                log.info("Kafka가 설정되지 않음, Redis만 업데이트");
            }
            
        } catch (Exception e) {
            log.error("점수 제출 중 오류 발생: userId={}, score={}", userId, score, e);
            throw new RuntimeException("점수 제출에 실패했습니다.", e);
        }
    }
    
    /**
     * Redis에 직접 랭킹 업데이트 (Kafka 없이)
     */
    private void updateRankingDirectly(Long userId, String nickname, Integer score, String gameType) {
        try {
            String key = "ranking:" + gameType;
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            
            // Redis에 점수 업데이트 (닉네임을 멤버로, 점수를 스코어로)
            zSetOps.add(key, nickname, score);
            
            // 상위 1000명만 유지 (메모리 절약)
            zSetOps.removeRange(key, 0, -1001);
            
            log.info("Redis 랭킹 직접 업데이트 완료: nickname={}, score={}, gameType={}", 
                    nickname, score, gameType);
            
        } catch (Exception e) {
            log.warn("Redis 직접 업데이트 실패: {}", e.getMessage());
        }
    }
    
    public List<RankingEntry> getTopRankings(String gameType, int limit) {
        try {
            // Redis 연결 시도
            String key = "ranking:" + gameType;
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            
            // Redis에서 상위 랭킹 조회 (RedisTestController와 같은 방식)
            Set<Object> rankings = zSetOps.reverseRange(key, 0, limit - 1);
            
            List<RankingEntry> result = new ArrayList<>();
            int rank = 1;
            
            if (rankings != null && !rankings.isEmpty()) {
                for (Object ranking : rankings) {
                    String nickname = (String) ranking;
                    
                    if (nickname != null) {
                        // 점수는 별도로 조회
                        Double score = zSetOps.score(key, nickname);
                        
                        RankingEntry entry = RankingEntry.builder()
                                .nickname(nickname)
                                .score(score != null ? score.intValue() : 0)
                                .rank(rank++)
                                .playedAt(LocalDateTime.now())
                                .build();
                        result.add(entry);
                    }
                }
                log.info("Redis 랭킹 조회 완료: gameType={}, count={}", gameType, result.size());
                return result;
            } else {
                log.info("Redis에 랭킹 데이터가 없음. DB에서 조회합니다.");
                return getRankingsFromDatabase(gameType, limit);
            }
            
        } catch (Exception e) {
            log.warn("Redis 랭킹 조회 실패, DB에서 조회합니다: gameType={}, error={}", gameType, e.getMessage());
            return getRankingsFromDatabase(gameType, limit);
        }
    }
    
    /**
     * DB에서 랭킹 조회 (Redis 연결 실패 시 대체)
     */
    private List<RankingEntry> getRankingsFromDatabase(String gameType, int limit) {
        try {
            List<GameScore> scores = gameScoreRepository.findTopByGameTypeOrderByScoreDesc(gameType, 
                org.springframework.data.domain.PageRequest.of(0, limit));
            
            List<RankingEntry> result = new ArrayList<>();
            int rank = 1;
            
            for (GameScore score : scores) {
                RankingEntry entry = RankingEntry.builder()
                        .nickname(score.getNickname())
                        .score(score.getScore())
                        .rank(rank++)
                        .playedAt(score.getCreatedAt())
                        .build();
                result.add(entry);
            }
            
            log.info("DB 랭킹 조회 완료: gameType={}, count={}", gameType, result.size());
            return result;
            
        } catch (Exception e) {
            log.error("DB 랭킹 조회 중 오류 발생: gameType={}", gameType, e);
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
