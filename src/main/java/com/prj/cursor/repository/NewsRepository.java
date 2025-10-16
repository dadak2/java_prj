package com.prj.cursor.repository;

import com.prj.cursor.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 뉴스 Repository
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    /**
     * 카테고리별 뉴스 조회
     */
    Page<News> findByCategory(String category, Pageable pageable);
    
    /**
     * 작성자별 뉴스 조회
     */
    Page<News> findByAuthorId(Long authorId, Pageable pageable);
    
    /**
     * 제목 또는 내용으로 검색
     */
    @Query("SELECT n FROM News n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword%")
    Page<News> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 인기 뉴스 조회 (조회수 기준)
     */
    @Query("SELECT n FROM News n ORDER BY n.viewCount DESC")
    Page<News> findPopularNews(Pageable pageable);
    
    /**
     * 최신 뉴스 조회
     */
    @Query("SELECT n FROM News n ORDER BY n.createdAt DESC")
    Page<News> findLatestNews(Pageable pageable);
    
    /**
     * 사용자가 팔로우한 카테고리의 뉴스 조회
     */
    @Query("SELECT n FROM News n WHERE n.category IN :categories ORDER BY n.createdAt DESC")
    Page<News> findByCategoryInOrderByCreatedAtDesc(@Param("categories") List<String> categories, Pageable pageable);
}
