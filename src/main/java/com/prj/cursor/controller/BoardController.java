package com.prj.cursor.controller;

import com.prj.cursor.entity.Board;
import com.prj.cursor.dto.BoardResponse;
import com.prj.cursor.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 게시판 REST API 컨트롤러
 * 
 * 게시글과 관련된 모든 HTTP 요청을 처리하는 REST API 컨트롤러입니다.
 * 클라이언트와 서버 간의 데이터 교환을 담당하며,
 * 요청의 유효성 검사와 응답 형식을 관리합니다.
 * 
 * 주요 기능:
 * - 게시글 CRUD API
 * - 페이징 및 검색 API
 * - 통계 정보 API
 * - 조회수/좋아요/댓글 수 관리 API
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see BoardService
 * @see Board
 */
@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회
     * 
     * 활성 상태의 모든 게시글을 페이징하여 조회합니다.
     * 
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 게시글 목록 페이지
     */
    @GetMapping
    public ResponseEntity<Page<Board>> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("게시글 목록 조회 API 호출 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardService.getBoards(pageable);
        
        return ResponseEntity.ok(boards);
    }

    /**
     * 게시글 상세 조회
     * 
     * 특정 게시글의 상세 정보를 조회합니다.
     * 조회 시 조회수가 자동으로 증가합니다.
     * 
     * @param boardNo 게시글 번호
     * @return 게시글 상세 정보 (userNo 제외)
     */
    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long boardNo) {
        log.info("게시글 상세 조회 API 호출 - 게시글 번호: {}", boardNo);
        
        try {
            Board board = boardService.getBoard(boardNo);
            // 조회수 증가
            boardService.incrementViewCount(boardNo);
            
            BoardResponse boardResponse = BoardResponse.from(board);
            return ResponseEntity.ok(boardResponse);
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 실패 - 게시글 번호: {}, 오류: {}", boardNo, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 게시글 생성
     * 
     * 새로운 게시글을 생성합니다.
     * 
     * @param request 게시글 생성 요청 데이터
     * @return 생성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<Board> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        log.info("게시글 생성 API 호출 - 제목: {}, 작성자: {}", request.getTitle(), request.getUserNo());
        
        try {
            Board board = boardService.createBoard(
                    request.getTitle(),
                    request.getContent(),
                    request.getCategory(),
                    request.getUserNo()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(board);
        } catch (IllegalArgumentException e) {
            log.error("게시글 생성 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 게시글 수정
     * 
     * 기존 게시글의 정보를 수정합니다.
     * 
     * @param boardNo 게시글 번호
     * @param request 게시글 수정 요청 데이터
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{boardNo}")
    public ResponseEntity<Board> updateBoard(
            @PathVariable Long boardNo,
            @Valid @RequestBody BoardUpdateRequest request) {
        
        log.info("게시글 수정 API 호출 - 게시글 번호: {}", boardNo);
        
        try {
            Board board = boardService.updateBoard(
                    boardNo,
                    request.getTitle(),
                    request.getContent(),
                    request.getCategory()
            );
            
            return ResponseEntity.ok(board);
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 실패 - 게시글 번호: {}, 오류: {}", boardNo, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 게시글 삭제
     * 
     * 게시글을 논리적으로 삭제합니다 (상태를 DELETED로 변경).
     * 
     * @param boardNo 게시글 번호
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable Long boardNo) {
        log.info("게시글 삭제 API 호출 - 게시글 번호: {}", boardNo);
        
        try {
            boardService.deleteBoard(boardNo);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "게시글이 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("게시글 삭제 실패 - 게시글 번호: {}, 오류: {}", boardNo, e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 카테고리별 게시글 조회
     * 
     * 특정 카테고리의 게시글을 페이징하여 조회합니다.
     * 
     * @param category 게시글 카테고리
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 카테고리별 게시글 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Board>> getBoardsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("카테고리별 게시글 조회 API 호출 - 카테고리: {}", category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardService.getBoardsByCategory(category, pageable);
        
        return ResponseEntity.ok(boards);
    }

    /**
     * 게시글 검색
     * 
     * 제목 또는 내용에 검색어가 포함된 게시글을 조회합니다.
     * 
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 검색 결과 게시글 목록
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Board>> searchBoards(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("게시글 검색 API 호출 - 키워드: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardService.searchBoards(keyword, pageable);
        
        return ResponseEntity.ok(boards);
    }

    /**
     * 인기 게시글 조회
     * 
     * 조회수 기준으로 인기 게시글을 조회합니다.
     * 
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 인기 게시글 목록
     */
    @GetMapping("/popular")
    public ResponseEntity<Page<Board>> getPopularBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("인기 게시글 조회 API 호출");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardService.getPopularBoards(pageable);
        
        return ResponseEntity.ok(boards);
    }

    /**
     * 작성자별 게시글 조회
     * 
     * 특정 사용자가 작성한 게시글을 조회합니다.
     * 
     * @param userNo 사용자 번호
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 작성자별 게시글 목록
     */
    @GetMapping("/author/{userNo}")
    public ResponseEntity<Page<Board>> getBoardsByAuthor(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("작성자별 게시글 조회 API 호출 - 사용자 번호: {}", userNo);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Board> boards = boardService.getBoardsByAuthor(userNo, pageable);
            
            return ResponseEntity.ok(boards);
        } catch (IllegalArgumentException e) {
            log.error("작성자별 게시글 조회 실패 - 사용자 번호: {}, 오류: {}", userNo, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 조회수 증가
     * 
     * 게시글의 조회수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     * @return 증가 결과 메시지
     */
    @PostMapping("/{boardNo}/view")
    public ResponseEntity<Map<String, String>> incrementViewCount(@PathVariable Long boardNo) {
        log.info("조회수 증가 API 호출 - 게시글 번호: {}", boardNo);
        
        boardService.incrementViewCount(boardNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "조회수가 증가되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 좋아요 수 증가
     * 
     * 게시글의 좋아요 수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     * @return 증가 결과 메시지
     */
    @PostMapping("/{boardNo}/like")
    public ResponseEntity<Map<String, String>> incrementLikeCount(@PathVariable Long boardNo) {
        log.info("좋아요 수 증가 API 호출 - 게시글 번호: {}", boardNo);
        
        boardService.incrementLikeCount(boardNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "좋아요가 증가되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 좋아요 수 감소
     * 
     * 게시글의 좋아요 수를 1 감소시킵니다.
     * 
     * @param boardNo 게시글 번호
     * @return 감소 결과 메시지
     */
    @DeleteMapping("/{boardNo}/like")
    public ResponseEntity<Map<String, String>> decrementLikeCount(@PathVariable Long boardNo) {
        log.info("좋아요 수 감소 API 호출 - 게시글 번호: {}", boardNo);
        
        boardService.decrementLikeCount(boardNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "좋아요가 감소되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수 증가
     * 
     * 게시글의 댓글 수를 1 증가시킵니다.
     * 
     * @param boardNo 게시글 번호
     * @return 증가 결과 메시지
     */
    @PostMapping("/{boardNo}/comment")
    public ResponseEntity<Map<String, String>> incrementCommentCount(@PathVariable Long boardNo) {
        log.info("댓글 수 증가 API 호출 - 게시글 번호: {}", boardNo);
        
        boardService.incrementCommentCount(boardNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글 수가 증가되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수 감소
     * 
     * 게시글의 댓글 수를 1 감소시킵니다.
     * 
     * @param boardNo 게시글 번호
     * @return 감소 결과 메시지
     */
    @DeleteMapping("/{boardNo}/comment")
    public ResponseEntity<Map<String, String>> decrementCommentCount(@PathVariable Long boardNo) {
        log.info("댓글 수 감소 API 호출 - 게시글 번호: {}", boardNo);
        
        boardService.decrementCommentCount(boardNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글 수가 감소되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 통계 조회
     * 
     * 게시글과 관련된 통계 정보를 조회합니다.
     * 
     * @return 게시글 통계 정보
     */
    @GetMapping("/statistics")
    public ResponseEntity<BoardService.BoardStatistics> getBoardStatistics() {
        log.info("게시글 통계 조회 API 호출");
        
        BoardService.BoardStatistics statistics = boardService.getBoardStatistics();
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 게시글 생성 요청 DTO
     */
    public static class BoardCreateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
        
        @NotBlank(message = "카테고리는 필수입니다.")
        private String category;
        
        @NotNull(message = "작성자 번호는 필수입니다.")
        private Long userNo;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Long getUserNo() { return userNo; }
        public void setUserNo(Long userNo) { this.userNo = userNo; }
    }

    /**
     * 게시글 수정 요청 DTO
     */
    public static class BoardUpdateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
        
        @NotBlank(message = "카테고리는 필수입니다.")
        private String category;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
} 