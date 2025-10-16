package com.prj.cursor.dto;

import com.prj.cursor.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String imageUrl;
    private String sourceUrl;
    private String author;
    private String authorNickname;
    private Long authorId;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * News 엔티티를 NewsResponse로 변환
     */
    public static NewsResponse from(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .category(news.getCategory())
                .imageUrl(news.getImageUrl())
                .sourceUrl(news.getSourceUrl())
                .author(news.getAuthor())
                .authorId(news.getAuthorId())
                .authorNickname(news.getAuthor()) // 임시로 author 필드 사용
                .viewCount(news.getViewCount())
                .likeCount(news.getLikeCount())
                .commentCount(news.getCommentCount())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
