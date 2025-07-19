package com.prj.cursor.service;

import com.prj.cursor.entity.Board;
import com.prj.cursor.entity.User;
import com.prj.cursor.repository.BoardRepository;
import com.prj.cursor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시판 비즈니스 로직 서비스
 * 
 * 게시글과 관련된 모든 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * Repository 계층을 통해 데이터베이스 작업을 수행하며,
 * 비즈니스 규칙과 유효성 검사를 담당합니다.
 * 
 * 주요 기능:
 * - 게시글 CRUD 작업
 * - 페이징 및 검색
 * - 통계 정보 관리
 * - 사용자 권한 검증
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see Board
 * @see User
 * @see BoardRepository
 * @see UserRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 생성
     * 
     * 새로운 게시글을 생성하고 데이터베이스에 저장합니다.
     * 작성자 정보를 검증하고 게시글의 기본 정보를 설정합니다.
     * 
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @param category 게시글 카테고리
     * @param userNo 작성자 번호
     * @return 생성된 게시글 엔티티
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public Board createBoard(String title, String content, String category, Long userNo) {
        log.info("게시글 생성 요청 - 제목: {}, 작성자: {}", title, userNo);
        
        // 사용자 존재 여부 확인
        User author = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 게시글 생성
        Board board = Board.builder()
                .title(title)
                .content(content)
                .category(category)
                .author(author)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .status(Board.BoardStatus.ACTIVE)
                .build();
        
        Board savedBoard = boardRepository.save(board);
        log.info("게시글 생성 완료 - 게시글 번호: {}", savedBoard.getBoardNo());
        
        return savedBoard;
    }

    /**
     * 게시글 조회
     * 
     * 게시글 번호로 특정 게시글을 조회합니다.
     * 
     * @param boardNo 게시글 번호
     * @return 게시글 엔티티
     * @throws IllegalArgumentException 게시글을 찾을 수 없는 경우
     */
    public Board getBoard(Long boardNo) {
        log.info("게시글 조회 요청 - 게시글 번호: {}", boardNo);
        
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        log.info("게시글 조회 완료 - 제목: {}", board.getTitle());
        return board;
    }

    /**
     * 게시글 목록 조회
     * 
     * 활성 상태의 모든 게시글을 페이징하여 조회합니다.
     * 최신 게시글이 먼저 표시됩니다.
     * 
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    public Page<Board> getBoards(Pageable pageable) {
        log.info("게시글 목록 조회 요청 - 페이지: {}, 크기: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Board> boards = boardRepository.findByStatusOrderByCreatedAtDesc(
                Board.BoardStatus.ACTIVE, pageable);
        
        log.info("게시글 목록 조회 완료 - 총 개수: {}", boards.getTotalElements());
        return boards;
    }

    /**
     * 카테고리별 게시글 조회
     * 
     * 특정 카테고리의 활성 게시글을 페이징하여 조회합니다.
     * 
     * @param category 게시글 카테고리
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    public Page<Board> getBoardsByCategory(String category, Pageable pageable) {
        log.info("카테고리별 게시글 조회 요청 - 카테고리: {}", category);
        
        Page<Board> boards = boardRepository.findByCategoryAndStatusOrderByCreatedAtDesc(
                category, Board.BoardStatus.ACTIVE, pageable);
        
        log.info("카테고리별 게시글 조회 완료 - 카테고리: {}, 개수: {}", 
                category, boards.getTotalElements());
        return boards;
    }

    /**
     * 게시글 검색
     * 
     * 제목 또는 내용에 검색어가 포함된 게시글을 조회합니다.
     * 대소문자를 구분하지 않습니다.
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 페이지
     */
    public Page<Board> searchBoards(String keyword, Pageable pageable) {
        log.info("게시글 검색 요청 - 키워드: {}", keyword);
        
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContainingAndStatus(
                keyword, keyword, Board.BoardStatus.ACTIVE, pageable);
        
        log.info("게시글 검색 완료 - 키워드: {}, 결과 개수: {}", 
                keyword, boards.getTotalElements());
        return boards;
    }

    /**
     * 게시글 수정
     * 
     * 기존 게시글의 제목, 내용, 카테고리를 수정합니다.
     * 
     * @param boardNo 게시글 번호
     * @param title 수정할 제목
     * @param content 수정할 내용
     * @param category 수정할 카테고리
     * @return 수정된 게시글 엔티티
     * @throws IllegalArgumentException 게시글을 찾을 수 없는 경우
     */
    @Transactional
    public Board updateBoard(Long boardNo, String title, String content, String category) {
        log.info("게시글 수정 요청 - 게시글 번호: {}", boardNo);
        
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 게시글 정보 업데이트
        board.updateBoard(title, content, category);
        Board updatedBoard = boardRepository.save(board);
        
        log.info("게시글 수정 완료 - 게시글 번호: {}", boardNo);
        return updatedBoard;
    }

    /**
     * 게시글 삭제
     * 
     * 게시글을 논리적으로 삭제합니다 (상태를 DELETED로 변경).
     * 실제 데이터는 데이터베이스에서 삭제되지 않습니다.
     * 
     * @param boardNo 게시글 번호
     * @throws IllegalArgumentException 게시글을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteBoard(Long boardNo) {
        log.info("게시글 삭제 요청 - 게시글 번호: {}", boardNo);
        
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        boardRepository.updateStatus(boardNo, Board.BoardStatus.DELETED);
        log.info("게시글 삭제 완료 - 게시글 번호: {}", boardNo);
    }

    /**
     * 조회수 증가
     * 
     * 게시글의 조회수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Transactional
    public void incrementViewCount(Long boardNo) {
        log.debug("조회수 증가 요청 - 게시글 번호: {}", boardNo);
        boardRepository.incrementViewCount(boardNo);
    }

    /**
     * 좋아요 수 증가
     * 
     * 게시글의 좋아요 수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Transactional
    public void incrementLikeCount(Long boardNo) {
        log.debug("좋아요 수 증가 요청 - 게시글 번호: {}", boardNo);
        boardRepository.incrementLikeCount(boardNo);
    }

    /**
     * 좋아요 수 감소
     * 
     * 게시글의 좋아요 수를 1 감소시킵니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Transactional
    public void decrementLikeCount(Long boardNo) {
        log.debug("좋아요 수 감소 요청 - 게시글 번호: {}", boardNo);
        boardRepository.decrementLikeCount(boardNo);
    }

    /**
     * 댓글 수 증가
     * 
     * 게시글의 댓글 수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Transactional
    public void incrementCommentCount(Long boardNo) {
        log.debug("댓글 수 증가 요청 - 게시글 번호: {}", boardNo);
        boardRepository.incrementCommentCount(boardNo);
    }

    /**
     * 댓글 수 감소
     * 
     * 게시글의 댓글 수를 1 감소시킵니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Transactional
    public void decrementCommentCount(Long boardNo) {
        log.debug("댓글 수 감소 요청 - 게시글 번호: {}", boardNo);
        boardRepository.decrementCommentCount(boardNo);
    }

    /**
     * 인기 게시글 조회
     * 
     * 조회수 기준으로 인기 게시글을 조회합니다.
     * 
     * @param pageable 페이징 정보
     * @return 인기 게시글 페이지
     */
    public Page<Board> getPopularBoards(Pageable pageable) {
        log.info("인기 게시글 조회 요청");
        
        Page<Board> boards = boardRepository.findByStatusOrderByViewCountDescCreatedAtDesc(
                Board.BoardStatus.ACTIVE, pageable);
        
        log.info("인기 게시글 조회 완료 - 개수: {}", boards.getTotalElements());
        return boards;
    }

    /**
     * 작성자별 게시글 조회
     * 
     * 특정 사용자가 작성한 게시글을 조회합니다.
     * 
     * @param userNo 사용자 번호
     * @param pageable 페이징 정보
     * @return 작성자별 게시글 페이지
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public Page<Board> getBoardsByAuthor(Long userNo, Pageable pageable) {
        log.info("작성자별 게시글 조회 요청 - 사용자 번호: {}", userNo);
        
        User author = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Page<Board> boards = boardRepository.findByAuthorAndStatusOrderByCreatedAtDesc(
                author, Board.BoardStatus.ACTIVE, pageable);
        
        log.info("작성자별 게시글 조회 완료 - 사용자: {}, 개수: {}", 
                author.getNickname(), boards.getTotalElements());
        return boards;
    }

    /**
     * 게시글 통계 조회
     * 
     * 게시글과 관련된 통계 정보를 조회합니다.
     * 
     * @return 게시글 통계 정보
     */
    public BoardStatistics getBoardStatistics() {
        log.info("게시글 통계 조회 요청");
        
        long totalBoards = boardRepository.countByStatus(Board.BoardStatus.ACTIVE);
        long generalBoards = boardRepository.countByCategoryAndStatus("일반", Board.BoardStatus.ACTIVE);
        long noticeBoards = boardRepository.countByCategoryAndStatus("공지", Board.BoardStatus.ACTIVE);
        
        BoardStatistics statistics = BoardStatistics.builder()
                .totalBoards(totalBoards)
                .generalBoards(generalBoards)
                .noticeBoards(noticeBoards)
                .build();
        
        log.info("게시글 통계 조회 완료 - 총 게시글: {}", totalBoards);
        return statistics;
    }

    /**
     * 게시글 통계 정보 DTO
     */
    public static class BoardStatistics {
        private final long totalBoards;
        private final long generalBoards;
        private final long noticeBoards;

        public BoardStatistics(long totalBoards, long generalBoards, long noticeBoards) {
            this.totalBoards = totalBoards;
            this.generalBoards = generalBoards;
            this.noticeBoards = noticeBoards;
        }

        public long getTotalBoards() { return totalBoards; }
        public long getGeneralBoards() { return generalBoards; }
        public long getNoticeBoards() { return noticeBoards; }

        public static BoardStatisticsBuilder builder() {
            return new BoardStatisticsBuilder();
        }

        public static class BoardStatisticsBuilder {
            private long totalBoards;
            private long generalBoards;
            private long noticeBoards;

            public BoardStatisticsBuilder totalBoards(long totalBoards) {
                this.totalBoards = totalBoards;
                return this;
            }

            public BoardStatisticsBuilder generalBoards(long generalBoards) {
                this.generalBoards = generalBoards;
                return this;
            }

            public BoardStatisticsBuilder noticeBoards(long noticeBoards) {
                this.noticeBoards = noticeBoards;
                return this;
            }

            public BoardStatistics build() {
                return new BoardStatistics(totalBoards, generalBoards, noticeBoards);
            }
        }
    }
} 