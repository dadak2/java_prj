package com.prj.cursor.repository;

import com.prj.cursor.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 팔로우 Repository
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    
    /**
     * 사용자가 팔로우하는 카테고리 목록 조회
     */
    @Query("SELECT DISTINCT uf.category FROM UserFollow uf WHERE uf.followerId = :followerId")
    List<String> findFollowedCategoriesByFollowerId(@Param("followerId") Long followerId);
    
    /**
     * 사용자가 특정 카테고리를 팔로우하는지 확인
     */
    boolean existsByFollowerIdAndCategory(Long followerId, String category);
    
    /**
     * 사용자의 모든 팔로우 삭제
     */
    void deleteByFollowerId(Long followerId);
    
    /**
     * 특정 카테고리를 팔로우하는 사용자 수 조회
     */
    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.category = :category")
    long countByCategory(@Param("category") String category);
}
