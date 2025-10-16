package com.prj.cursor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Redis 연결 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/redis")
@CrossOrigin(origins = "*")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 연결 테스트
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Redis 연결 테스트
            String testKey = "test:connection";
            String testValue = "Redis 연결 성공!";
            
            // 데이터 저장
            redisTemplate.opsForValue().set(testKey, testValue);
            
            // 데이터 조회
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
            
            if (testValue.equals(retrievedValue)) {
                result.put("success", true);
                result.put("message", "Redis 연결 성공");
                result.put("testValue", retrievedValue);
            } else {
                result.put("success", false);
                result.put("message", "Redis 데이터 불일치");
            }
            
            // 테스트 데이터 삭제
            redisTemplate.delete(testKey);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis 연결 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 게임 랭킹 테스트 데이터 생성
     */
    @PostMapping("/ranking/test")
    public ResponseEntity<Map<String, Object>> createTestRanking() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String rankingKey = "ranking:snake";
            
            // 테스트 랭킹 데이터 추가
            redisTemplate.opsForZSet().add(rankingKey, "테스트유저1", 1000);
            redisTemplate.opsForZSet().add(rankingKey, "테스트유저2", 1500);
            redisTemplate.opsForZSet().add(rankingKey, "테스트유저3", 800);
            redisTemplate.opsForZSet().add(rankingKey, "테스트유저4", 2000);
            redisTemplate.opsForZSet().add(rankingKey, "테스트유저5", 1200);
            
            // 랭킹 조회
            Set<Object> rankings = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, 4);
            
            result.put("success", true);
            result.put("message", "테스트 랭킹 데이터 생성 완료");
            result.put("rankings", rankings);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "랭킹 데이터 생성 실패: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 게임 랭킹 조회
     */
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String rankingKey = "ranking:snake";
            
            // 상위 10개 랭킹 조회
            Set<Object> rankings = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, 9);
            Long totalCount = redisTemplate.opsForZSet().zCard(rankingKey);
            
            result.put("success", true);
            result.put("rankings", rankings);
            result.put("totalCount", totalCount);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "랭킹 조회 실패: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Redis 데이터 초기화
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearRedis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 게임 랭킹 데이터만 삭제
            redisTemplate.delete("game:ranking:snake");
            
            result.put("success", true);
            result.put("message", "Redis 데이터 초기화 완료");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "데이터 초기화 실패: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}
