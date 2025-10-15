package com.prj.cursor.repository;

import com.prj.cursor.entity.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    
    @Query("SELECT gs FROM GameScore gs WHERE gs.gameType = :gameType ORDER BY gs.score DESC")
    Page<GameScore> findTopScoresByGameType(@Param("gameType") String gameType, Pageable pageable);
    
    @Query("SELECT gs FROM GameScore gs WHERE gs.userId = :userId AND gs.gameType = :gameType ORDER BY gs.score DESC")
    List<GameScore> findUserScoresByGameType(@Param("userId") Long userId, @Param("gameType") String gameType);
    
    @Query("SELECT COUNT(gs) FROM GameScore gs WHERE gs.gameType = :gameType")
    Long countByGameType(@Param("gameType") String gameType);
}
