package com.prj.cursor.service;

import com.prj.cursor.dto.NewsResponse;
import com.prj.cursor.entity.News;
import com.prj.cursor.entity.UserFollow;
import com.prj.cursor.repository.NewsRepository;
import com.prj.cursor.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 뉴스 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    
    private final NewsRepository newsRepository;
    private final UserFollowRepository userFollowRepository;
    
    /**
     * 뉴스 목록 조회
     */
    public Page<NewsResponse> getNews(int page, int size, String category, String sort) {
        log.info("뉴스 목록 조회 - 페이지: {}, 크기: {}, 카테고리: {}, 정렬: {}", page, size, category, sort);
        
        Pageable pageable = createPageable(page, size, sort);
        Page<News> newsPage;
        
        if (category != null && !category.isEmpty()) {
            newsPage = newsRepository.findByCategory(category, pageable);
        } else {
            newsPage = newsRepository.findLatestNews(pageable);
        }
        
        return newsPage.map(NewsResponse::from);
    }
    
    /**
     * 사용자별 맞춤 뉴스 피드 조회
     */
    public Page<NewsResponse> getUserFeed(Long userId, int page, int size) {
        log.info("사용자 맞춤 피드 조회 - 사용자: {}, 페이지: {}, 크기: {}", userId, page, size);
        
        List<String> followedCategories = userFollowRepository.findFollowedCategoriesByFollowerId(userId);
        
        if (followedCategories.isEmpty()) {
            // 팔로우한 카테고리가 없으면 전체 뉴스 반환
            return getNews(page, size, null, "latest");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<News> newsPage = newsRepository.findByCategoryInOrderByCreatedAtDesc(followedCategories, pageable);
        
        return newsPage.map(NewsResponse::from);
    }
    
    /**
     * 뉴스 상세 조회
     */
    public NewsResponse getNewsById(Long id) {
        log.info("뉴스 상세 조회 - ID: {}", id);
        
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("뉴스를 찾을 수 없습니다: " + id));
        
        // 조회수 증가
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);
        
        return NewsResponse.from(news);
    }
    
    /**
     * 사용자 팔로우 카테고리 저장
     */
    @Transactional
    public void saveUserFollows(Long userId, List<String> categories) {
        log.info("사용자 팔로우 저장 - 사용자: {}, 카테고리: {}", userId, categories);
        
        // 기존 팔로우 삭제
        userFollowRepository.deleteByFollowerId(userId);
        
        // 새로운 팔로우 저장
        for (String category : categories) {
            UserFollow userFollow = new UserFollow();
            userFollow.setFollowerId(userId);
            userFollow.setCategory(category);
            userFollowRepository.save(userFollow);
        }
    }
    
    /**
     * 사용자 팔로우 카테고리 조회
     */
    public List<String> getUserFollows(Long userId) {
        log.info("사용자 팔로우 조회 - 사용자: {}", userId);
        
        return userFollowRepository.findFollowedCategoriesByFollowerId(userId);
    }
    
    /**
     * 인기 뉴스 조회
     */
    public Page<NewsResponse> getPopularNews(int page, int size) {
        log.info("인기 뉴스 조회 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        Page<News> newsPage = newsRepository.findPopularNews(pageable);
        
        return newsPage.map(NewsResponse::from);
    }
    
    /**
     * 뉴스 검색
     */
    public Page<NewsResponse> searchNews(String keyword, int page, int size) {
        log.info("뉴스 검색 - 키워드: {}, 페이지: {}, 크기: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<News> newsPage = newsRepository.findByTitleOrContentContaining(keyword, pageable);
        
        return newsPage.map(NewsResponse::from);
    }
    
    /**
     * 페이지네이션 객체 생성
     */
    private Pageable createPageable(int page, int size, String sort) {
        Sort sortObj;
        
        switch (sort) {
            case "popular":
                sortObj = Sort.by("viewCount").descending();
                break;
            case "latest":
            default:
                sortObj = Sort.by("createdAt").descending();
                break;
        }
        
        return PageRequest.of(page, size, sortObj);
    }
}
