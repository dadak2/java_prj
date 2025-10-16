package com.prj.cursor.controller;

import com.prj.cursor.dto.NewsResponse;
import com.prj.cursor.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 뉴스 컨트롤러
 */
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {
    
    private final NewsService newsService;
    
    /**
     * 뉴스 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort) {
        
        log.info("뉴스 목록 조회 API 호출 - 페이지: {}, 크기: {}, 카테고리: {}, 정렬: {}", page, size, category, sort);
        
        Page<NewsResponse> news = newsService.getNews(page, size, category, sort);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 뉴스 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        log.info("뉴스 상세 조회 API 호출 - ID: {}", id);
        
        NewsResponse news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 사용자 맞춤 피드 조회
     */
    @GetMapping("/feed/{userId}")
    public ResponseEntity<Page<NewsResponse>> getUserFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("사용자 맞춤 피드 조회 API 호출 - 사용자: {}, 페이지: {}, 크기: {}", userId, page, size);
        
        Page<NewsResponse> feed = newsService.getUserFeed(userId, page, size);
        return ResponseEntity.ok(feed);
    }
    
    /**
     * 사용자 팔로우 카테고리 조회
     */
    @GetMapping("/follows/{userId}")
    public ResponseEntity<List<String>> getUserFollows(@PathVariable Long userId) {
        log.info("사용자 팔로우 조회 API 호출 - 사용자: {}", userId);
        
        List<String> follows = newsService.getUserFollows(userId);
        return ResponseEntity.ok(follows);
    }
    
    /**
     * 사용자 팔로우 카테고리 저장
     */
    @PostMapping("/follows/{userId}")
    public ResponseEntity<String> saveUserFollows(
            @PathVariable Long userId,
            @RequestBody List<String> categories) {
        
        log.info("사용자 팔로우 저장 API 호출 - 사용자: {}, 카테고리: {}", userId, categories);
        
        newsService.saveUserFollows(userId, categories);
        return ResponseEntity.ok("팔로우 설정이 저장되었습니다.");
    }
    
    /**
     * 인기 뉴스 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<Page<NewsResponse>> getPopularNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("인기 뉴스 조회 API 호출 - 페이지: {}, 크기: {}", page, size);
        
        Page<NewsResponse> news = newsService.getPopularNews(page, size);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 뉴스 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<NewsResponse>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("뉴스 검색 API 호출 - 키워드: {}, 페이지: {}, 크기: {}", keyword, page, size);
        
        Page<NewsResponse> news = newsService.searchNews(keyword, page, size);
        return ResponseEntity.ok(news);
    }
}
