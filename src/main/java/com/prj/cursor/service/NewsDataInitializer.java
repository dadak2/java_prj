package com.prj.cursor.service;

import com.prj.cursor.entity.News;
import com.prj.cursor.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 테스트용 뉴스 데이터 생성 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsDataInitializer implements CommandLineRunner {
    
    private final NewsRepository newsRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 이미 데이터가 있는지 확인
        if (newsRepository.count() > 0) {
            log.info("뉴스 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }
        
        log.info("테스트용 뉴스 데이터를 생성합니다.");
        createSampleNews();
    }
    
    private void createSampleNews() {
        List<News> sampleNews = Arrays.asList(
            createNews("Spring Boot 3.0의 새로운 기능들", 
                "Spring Boot 3.0에서는 Java 17을 기본으로 지원하며, 많은 새로운 기능들이 추가되었습니다. 특히 GraalVM 네이티브 이미지 지원이 강화되어 더 빠른 애플리케이션 시작이 가능해졌습니다.",
                "TECH", 1L, 150, 25, 8),
                
            createNews("월드컵 결승전, 아르헨티나 우승", 
                "카타르 월드컵 결승전에서 아르헨티나가 프랑스를 승부차기로 꺾고 우승을 차지했습니다. 메시의 마지막 월드컵에서 드디어 꿈을 이뤘습니다.",
                "SPORTS", 2L, 320, 45, 12),
                
            createNews("새로운 AI 기술의 발전과 윤리적 고민", 
                "ChatGPT와 같은 대화형 AI의 등장으로 인공지능 기술이 한 단계 더 발전했습니다. 하지만 이에 따른 윤리적 문제들도 함께 제기되고 있습니다.",
                "TECH", 3L, 280, 38, 15),
                
            createNews("경제 정책 변화와 시장 반응", 
                "정부의 새로운 경제 정책 발표에 따라 주식 시장이 급등했습니다. 투자자들은 긍정적인 반응을 보이고 있습니다.",
                "BUSINESS", 4L, 180, 22, 6),
                
            createNews("한류 콘텐츠의 세계적 영향력", 
                "K-POP과 K-드라마가 전 세계적으로 큰 인기를 끌고 있습니다. 문화 콘텐츠 수출이 크게 증가했습니다.",
                "ENTERTAINMENT", 5L, 250, 35, 18),
                
            createNews("정치 개혁과 민주주의 발전", 
                "새로운 정치 개혁안이 국회를 통과했습니다. 더 투명하고 민주적인 정치 시스템 구축을 위한 노력이 계속되고 있습니다.",
                "POLITICS", 6L, 200, 28, 9),
                
            createNews("스마트폰 기술의 혁신", 
                "최신 스마트폰들은 폴더블 디스플레이와 향상된 카메라 기술로 사용자 경험을 한 단계 끌어올렸습니다.",
                "TECH", 7L, 190, 30, 11),
                
            createNews("프리미어리그 시즌 개막", 
                "새로운 프리미어리그 시즌이 시작되었습니다. 맨체스터 시티와 아스날이 초반 리그를 선도하고 있습니다.",
                "SPORTS", 8L, 160, 20, 7),
                
            createNews("환경 보호를 위한 새로운 정책", 
                "탄소 중립을 위한 새로운 환경 정책이 발표되었습니다. 기업들의 친환경 경영이 더욱 중요해지고 있습니다.",
                "POLITICS", 9L, 170, 25, 5),
                
            createNews("엔터테인먼트 산업의 디지털 전환", 
                "OTT 플랫폼의 성장으로 엔터테인먼트 산업이 급속히 변화하고 있습니다. 전통적인 방송사들도 새로운 전략을 모색하고 있습니다.",
                "ENTERTAINMENT", 10L, 140, 18, 4)
        );
        
        newsRepository.saveAll(sampleNews);
        log.info("{}개의 테스트 뉴스 데이터가 생성되었습니다.", sampleNews.size());
    }
    
    private News createNews(String title, String content, String category, Long authorId, 
                           int viewCount, int likeCount, int commentCount) {
        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setCategory(category);
        news.setAuthorId(authorId);
        news.setViewCount(viewCount);
        news.setLikeCount(likeCount);
        news.setCommentCount(commentCount);
        news.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
        return news;
    }
}
