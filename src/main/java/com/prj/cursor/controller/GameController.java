package com.prj.cursor.controller;

import com.prj.cursor.dto.RankingEntry;
import com.prj.cursor.dto.ScoreSubmission;
import com.prj.cursor.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    
    private final GameService gameService;
    
    @GetMapping("/rankings/{gameType}")
    public ResponseEntity<List<RankingEntry>> getRankings(
            @PathVariable String gameType,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            log.info("랭킹 조회 요청: gameType={}, limit={}", gameType, limit);
            
            List<RankingEntry> rankings = gameService.getTopRankings(gameType, limit);
            
            return ResponseEntity.ok(rankings);
            
        } catch (Exception e) {
            log.error("랭킹 조회 중 오류 발생: gameType={}, limit={}", gameType, limit, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/score")
    public ResponseEntity<String> submitScore(@RequestBody ScoreSubmission submission) {
        try {
            log.info("점수 제출 요청: {}", submission);
            
            gameService.submitScore(
                submission.getUserId(),
                submission.getNickname(),
                submission.getScore(),
                submission.getGameType()
            );
            
            return ResponseEntity.ok("점수가 성공적으로 제출되었습니다.");
            
        } catch (Exception e) {
            log.error("점수 제출 중 오류 발생: {}", submission, e);
            return ResponseEntity.internalServerError().body("점수 제출에 실패했습니다.");
        }
    }
}
