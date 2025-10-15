package com.prj.cursor.service;

import com.prj.cursor.entity.Board;
import com.prj.cursor.entity.User;
import com.prj.cursor.repository.BoardRepository;
import com.prj.cursor.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BoardService 단위 테스트
 * 
 * 게시판 서비스의 모든 기능을 검증하는 테스트 클래스입니다.
 * TDD 방식으로 작성되어 각 기능의 동작을 명확히 정의합니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    private User testUser;
    private Board testBoard;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
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
    @DisplayName("게시글 생성 - 성공")
    void createBoard_Success() {
        // given
        String title = "새 게시글";
        String content = "새 게시글 내용";
        String category = "일반";
        Long userNo = 1L;

        when(userRepository.findById(userNo)).thenReturn(Optional.of(testUser));
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        // when
        Board result = boardService.createBoard(title, content, category, userNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getAuthor()).isEqualTo(testUser);
        assertThat(result.getStatus()).isEqualTo(Board.BoardStatus.ACTIVE);

        verify(userRepository).findById(userNo);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 - 사용자 없음")
    void createBoard_UserNotFound() {
        // given
        Long userNo = 999L;
        when(userRepository.findById(userNo)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> 
            boardService.createBoard("제목", "내용", "카테고리", userNo)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("사용자를 찾을 수 없습니다.");

        verify(userRepository).findById(userNo);
        verify(boardRepository, never()).save(any());
    }

    @Test
    @DisplayName("게시글 조회 - 성공")
    void getBoard_Success() {
        // given
        Long boardNo = 1L;
        when(boardRepository.findById(boardNo)).thenReturn(Optional.of(testBoard));

        // when
        Board result = boardService.getBoard(boardNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBoardNo()).isEqualTo(boardNo);
        assertThat(result.getTitle()).isEqualTo("테스트 게시글");

        verify(boardRepository).findById(boardNo);
    }

    @Test
    @DisplayName("게시글 조회 - 게시글 없음")
    void getBoard_NotFound() {
        // given
        Long boardNo = 999L;
        when(boardRepository.findById(boardNo)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> 
            boardService.getBoard(boardNo)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("게시글을 찾을 수 없습니다.");

        verify(boardRepository).findById(boardNo);
    }



    @Test
    @DisplayName("게시글 수정 - 성공")
    void updateBoard_Success() {
        // given
        Long boardNo = 1L;
        String newTitle = "수정된 제목";
        String newContent = "수정된 내용";
        String newCategory = "공지";

        when(boardRepository.findById(boardNo)).thenReturn(Optional.of(testBoard));
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        // when
        Board result = boardService.updateBoard(boardNo, newTitle, newContent, newCategory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getContent()).isEqualTo(newContent);
        assertThat(result.getCategory()).isEqualTo(newCategory);

        verify(boardRepository).findById(boardNo);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글 수정 - 게시글 없음")
    void updateBoard_NotFound() {
        // given
        Long boardNo = 999L;
        when(boardRepository.findById(boardNo)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> 
            boardService.updateBoard(boardNo, "제목", "내용", "카테고리")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("게시글을 찾을 수 없습니다.");

        verify(boardRepository).findById(boardNo);
        verify(boardRepository, never()).save(any());
    }



 

    @Test
    @DisplayName("조회수 증가 - 성공")
    void incrementViewCount_Success() {
        // given
        Long boardNo = 1L;
        doNothing().when(boardRepository).incrementViewCount(boardNo);

        // when
        boardService.incrementViewCount(boardNo);

        // then
        verify(boardRepository).incrementViewCount(boardNo);
    }

    @Test
    @DisplayName("좋아요 수 증가 - 성공")
    void incrementLikeCount_Success() {
        // given
        Long boardNo = 1L;
        doNothing().when(boardRepository).incrementLikeCount(boardNo);

        // when
        boardService.incrementLikeCount(boardNo);

        // then
        verify(boardRepository).incrementLikeCount(boardNo);
    }

    @Test
    @DisplayName("좋아요 수 감소 - 성공")
    void decrementLikeCount_Success() {
        // given
        Long boardNo = 1L;
        doNothing().when(boardRepository).decrementLikeCount(boardNo);

        // when
        boardService.decrementLikeCount(boardNo);

        // then
        verify(boardRepository).decrementLikeCount(boardNo);
    }

    @Test
    @DisplayName("댓글 수 증가 - 성공")
    void incrementCommentCount_Success() {
        // given
        Long boardNo = 1L;
        doNothing().when(boardRepository).incrementCommentCount(boardNo);

        // when
        boardService.incrementCommentCount(boardNo);

        // then
        verify(boardRepository).incrementCommentCount(boardNo);
    }

    @Test
    @DisplayName("댓글 수 감소 - 성공")
    void decrementCommentCount_Success() {
        // given
        Long boardNo = 1L;
        doNothing().when(boardRepository).decrementCommentCount(boardNo);

        // when
        boardService.decrementCommentCount(boardNo);

        // then
        verify(boardRepository).decrementCommentCount(boardNo);
    }

} 