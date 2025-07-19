package com.prj.cursor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.cursor.entity.Board;
import com.prj.cursor.entity.User;
import com.prj.cursor.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BoardController 단위 테스트
 * 
 * 게시판 컨트롤러의 모든 API 엔드포인트를 검증하는 테스트 클래스입니다.
 * TDD 방식으로 작성되어 각 API의 동작을 명확히 정의합니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardController boardController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private Board testBoard;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
        objectMapper = new ObjectMapper();

        // 테스트용 사용자 생성
        testUser = User.builder()
                .userNo(1L)
                .nickname("testuser")
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123")
                .build();

        // 테스트용 게시글 생성
        testBoard = Board.builder()
                .boardNo(1L)
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .category("일반")
                .author(testUser)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .status(Board.BoardStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("게시글 목록 조회 API - 성공")
    void getBoards_Success() throws Exception {
        // given
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());
        
        when(boardService.getBoards(any(Pageable.class))).thenReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/boards")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].boardNo").value(1))
                .andExpect(jsonPath("$.content[0].title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content[0].category").value("일반"));

        verify(boardService).getBoards(any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 상세 조회 API - 성공")
    void getBoard_Success() throws Exception {
        // given
        Long boardNo = 1L;
        when(boardService.getBoard(boardNo)).thenReturn(testBoard);

        // when & then
        mockMvc.perform(get("/api/boards/{boardNo}", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardNo").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.category").value("일반"));

        verify(boardService).getBoard(boardNo);
    }

    @Test
    @DisplayName("게시글 상세 조회 API - 게시글 없음")
    void getBoard_NotFound() throws Exception {
        // given
        Long boardNo = 999L;
        when(boardService.getBoard(boardNo))
                .thenThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/boards/{boardNo}", boardNo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."));

        verify(boardService).getBoard(boardNo);
    }

    @Test
    @DisplayName("게시글 생성 API - 성공")
    void createBoard_Success() throws Exception {
        // given
        BoardController.BoardCreateRequest request = new BoardController.BoardCreateRequest();
        request.setTitle("새 게시글");
        request.setContent("새 게시글 내용");
        request.setCategory("일반");
        request.setUserNo(1L);

        when(boardService.createBoard(
            eq("새 게시글"), 
            eq("새 게시글 내용"), 
            eq("일반"), 
            eq(1L)
        )).thenReturn(testBoard);

        // when & then
        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardNo").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"));

        verify(boardService).createBoard(
            eq("새 게시글"), 
            eq("새 게시글 내용"), 
            eq("일반"), 
            eq(1L)
        );
    }

    @Test
    @DisplayName("게시글 생성 API - 유효성 검사 실패")
    void createBoard_ValidationFailed() throws Exception {
        // given
        BoardController.BoardCreateRequest request = new BoardController.BoardCreateRequest();
        request.setTitle(""); // 빈 제목
        request.setContent("내용");
        request.setCategory("일반");
        request.setUserNo(1L);

        // when & then
        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(boardService, never()).createBoard(any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글 수정 API - 성공")
    void updateBoard_Success() throws Exception {
        // given
        Long boardNo = 1L;
        BoardController.BoardUpdateRequest request = new BoardController.BoardUpdateRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setCategory("공지");

        Board updatedBoard = Board.builder()
                .boardNo(boardNo)
                .title("수정된 제목")
                .content("수정된 내용")
                .category("공지")
                .author(testUser)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .status(Board.BoardStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(boardService.updateBoard(
            eq(boardNo), 
            eq("수정된 제목"), 
            eq("수정된 내용"), 
            eq("공지")
        )).thenReturn(updatedBoard);

        // when & then
        mockMvc.perform(put("/api/boards/{boardNo}", boardNo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.category").value("공지"));

        verify(boardService).updateBoard(
            eq(boardNo), 
            eq("수정된 제목"), 
            eq("수정된 내용"), 
            eq("공지")
        );
    }

    @Test
    @DisplayName("게시글 수정 API - 게시글 없음")
    void updateBoard_NotFound() throws Exception {
        // given
        Long boardNo = 999L;
        BoardController.BoardUpdateRequest request = new BoardController.BoardUpdateRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setCategory("공지");

        when(boardService.updateBoard(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(put("/api/boards/{boardNo}", boardNo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."));

        verify(boardService).updateBoard(any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글 삭제 API - 성공")
    void deleteBoard_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).deleteBoard(boardNo);

        // when & then
        mockMvc.perform(delete("/api/boards/{boardNo}", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 삭제되었습니다."));

        verify(boardService).deleteBoard(boardNo);
    }

    @Test
    @DisplayName("게시글 삭제 API - 게시글 없음")
    void deleteBoard_NotFound() throws Exception {
        // given
        Long boardNo = 999L;
        doThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다."))
                .when(boardService).deleteBoard(boardNo);

        // when & then
        mockMvc.perform(delete("/api/boards/{boardNo}", boardNo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."));

        verify(boardService).deleteBoard(boardNo);
    }

    @Test
    @DisplayName("카테고리별 게시글 조회 API - 성공")
    void getBoardsByCategory_Success() throws Exception {
        // given
        String category = "일반";
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());
        
        when(boardService.getBoardsByCategory(eq(category), any(Pageable.class)))
                .thenReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/boards/category/{category}", category)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].category").value(category));

        verify(boardService).getBoardsByCategory(eq(category), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 검색 API - 성공")
    void searchBoards_Success() throws Exception {
        // given
        String keyword = "테스트";
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());
        
        when(boardService.searchBoards(eq(keyword), any(Pageable.class)))
                .thenReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/boards/search")
                .param("keyword", keyword)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].title").value("테스트 게시글"));

        verify(boardService).searchBoards(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("인기 게시글 조회 API - 성공")
    void getPopularBoards_Success() throws Exception {
        // given
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());
        
        when(boardService.getPopularBoards(any(Pageable.class))).thenReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/boards/popular")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());

        verify(boardService).getPopularBoards(any(Pageable.class));
    }

    @Test
    @DisplayName("작성자별 게시글 조회 API - 성공")
    void getBoardsByAuthor_Success() throws Exception {
        // given
        Long userNo = 1L;
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());
        
        when(boardService.getBoardsByAuthor(eq(userNo), any(Pageable.class)))
                .thenReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/boards/author/{userNo}", userNo)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());

        verify(boardService).getBoardsByAuthor(eq(userNo), any(Pageable.class));
    }

    @Test
    @DisplayName("조회수 증가 API - 성공")
    void incrementViewCount_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).incrementViewCount(boardNo);

        // when & then
        mockMvc.perform(post("/api/boards/{boardNo}/view", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회수가 증가되었습니다."));

        verify(boardService).incrementViewCount(boardNo);
    }

    @Test
    @DisplayName("좋아요 증가 API - 성공")
    void incrementLikeCount_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).incrementLikeCount(boardNo);

        // when & then
        mockMvc.perform(post("/api/boards/{boardNo}/like", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요가 증가되었습니다."));

        verify(boardService).incrementLikeCount(boardNo);
    }

    @Test
    @DisplayName("좋아요 감소 API - 성공")
    void decrementLikeCount_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).decrementLikeCount(boardNo);

        // when & then
        mockMvc.perform(delete("/api/boards/{boardNo}/like", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요가 감소되었습니다."));

        verify(boardService).decrementLikeCount(boardNo);
    }

    @Test
    @DisplayName("댓글 수 증가 API - 성공")
    void incrementCommentCount_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).incrementCommentCount(boardNo);

        // when & then
        mockMvc.perform(post("/api/boards/{boardNo}/comment", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수가 증가되었습니다."));

        verify(boardService).incrementCommentCount(boardNo);
    }

    @Test
    @DisplayName("댓글 수 감소 API - 성공")
    void decrementCommentCount_Success() throws Exception {
        // given
        Long boardNo = 1L;
        doNothing().when(boardService).decrementCommentCount(boardNo);

        // when & then
        mockMvc.perform(delete("/api/boards/{boardNo}/comment", boardNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수가 감소되었습니다."));

        verify(boardService).decrementCommentCount(boardNo);
    }
} 