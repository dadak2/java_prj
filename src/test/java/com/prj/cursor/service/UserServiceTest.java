package com.prj.cursor.service;

import com.prj.cursor.dto.UserResponse;
import com.prj.cursor.dto.UserSignupRequest;
import com.prj.cursor.entity.User;
import com.prj.cursor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트 클래스
 * 
 * UserService의 모든 비즈니스 로직을 테스트합니다.
 * Mockito를 사용하여 의존성을 모킹하고 격리된 테스트를 수행합니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserSignupRequest signupRequest;
    
    /**
     * 각 테스트 메서드 실행 전 초기화
     * 
     * Mock 객체들을 초기화하고 공통으로 사용할 테스트 데이터를 설정합니다.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 테스트용 사용자 데이터 설정
        testUser = User.builder()
                .userNo(1L)
                .nickname("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .userRole(User.UserRole.USER)
                .isActive(true)
                .build();
        
        // 테스트용 회원가입 요청 데이터 설정
        signupRequest = new UserSignupRequest();
        signupRequest.setNickname("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");
        
        // PasswordEncoder 모킹 설정
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
    }
    
    /**
     * 회원가입 성공 테스트
     * 
     * 정상적인 회원가입 요청이 성공적으로 처리되는지 테스트합니다.
     */
    @Test
    @DisplayName("정상적인 회원가입 테스트")
    void signupSuccess() {
        // given
        when(userRepository.existsByNickname("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        UserResponse response = userService.signup(signupRequest);
        
        // then
        assertNotNull(response);
        assertEquals("testuser", response.getNickname());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(User.UserRole.USER, response.getUserRole());
        assertTrue(response.isActive());
        
        // verify
        verify(userRepository).existsByNickname("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }
    
    /**
     * 비밀번호 불일치 회원가입 실패 테스트
     * 
     * 비밀번호와 확인 비밀번호가 일치하지 않을 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("비밀번호 불일치로 인한 회원가입 실패 테스트")
    void signupPasswordMismatch() {
        // given
        signupRequest.setConfirmPassword("differentPassword");
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(signupRequest);
        });
        
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        
        // verify - 저장소 메서드가 호출되지 않았는지 확인
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * 이메일 중복으로 인한 회원가입 실패 테스트
     * 
     * 이미 존재하는 이메일로 회원가입 시도 시 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패 테스트")
    void signupEmailExists() {
        // given
        when(userRepository.existsByNickname("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(signupRequest);
        });
        
        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        
        // verify
        verify(userRepository).existsByNickname("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * 사용자명으로 사용자 조회 성공 테스트
     * 
     * 존재하는 사용자명으로 조회 시 정상적으로 사용자 정보가 반환되는지 테스트합니다.
     */
    @Test
    @DisplayName("사용자명으로 사용자 조회 성공 테스트")
    void findByUsernameSuccess() {
        // given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(testUser));
        
        // when
        UserResponse response = userService.findByNickname("testuser");
        
        // then
        assertNotNull(response);
        assertEquals("testuser", response.getNickname());
        assertEquals("test@example.com", response.getEmail());
        
        // verify
        verify(userRepository).findByNickname("testuser");
    }
    
    /**
     * 존재하지 않는 사용자명으로 조회 실패 테스트
     * 
     * 존재하지 않는 사용자명으로 조회 시 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("존재하지 않는 사용자명으로 조회 실패 테스트")
    void findByUsernameNotFound() {
        // given
        when(userRepository.findByNickname("nonexistent")).thenReturn(Optional.empty());
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByNickname("nonexistent");
        });
        
        assertEquals("사용자를 찾을 수 없습니다: nonexistent", exception.getMessage());
        
        // verify
        verify(userRepository).findByNickname("nonexistent");
    }
    
    /**
     * 이메일로 사용자 조회 성공 테스트
     * 
     * 존재하는 이메일로 조회 시 정상적으로 사용자 정보가 반환되는지 테스트합니다.
     */
    @Test
    @DisplayName("이메일로 사용자 조회 성공 테스트")
    void findByEmailSuccess() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // when
        UserResponse response = userService.findByEmail("test@example.com");
        
        // then
        assertNotNull(response);
        assertEquals("testuser", response.getNickname());
        assertEquals("test@example.com", response.getEmail());
        
        // verify
        verify(userRepository).findByEmail("test@example.com");
    }
    
    /**
     * 존재하지 않는 이메일로 조회 실패 테스트
     * 
     * 존재하지 않는 이메일로 조회 시 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("존재하지 않는 이메일로 조회 실패 테스트")
    void findByEmailNotFound() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByEmail("nonexistent@example.com");
        });
        
        assertEquals("사용자를 찾을 수 없습니다: nonexistent@example.com", exception.getMessage());
        
        // verify
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    /**
     * 활성 사용자 목록 조회 테스트
     * 
     * 활성화된 사용자들만 정상적으로 조회되는지 테스트합니다.
     */
    @Test
    @DisplayName("활성 사용자 목록 조회 테스트")
    void findAllActiveUsers() {
        // given
        User activeUser1 = User.builder().userNo(1L).nickname("user1").email("user1@example.com").isActive(true).build();
        User activeUser2 = User.builder().userNo(2L).nickname("user2").email("user2@example.com").isActive(true).build();
        List<User> activeUsers = Arrays.asList(activeUser1, activeUser2);
        
        when(userRepository.findAllActiveUsers()).thenReturn(activeUsers);
        
        // when
        List<UserResponse> responses = userService.findAllActiveUsers();
        
        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("user1", responses.get(0).getNickname());
        assertEquals("user2", responses.get(1).getNickname());
        
        // verify
        verify(userRepository).findAllActiveUsers();
    }
    
    /**
     * 사용자 계정 비활성화 성공 테스트
     * 
     * 이메일을 기준으로 사용자 계정이 정상적으로 비활성화되는지 테스트합니다.
     */
    @Test
    @DisplayName("사용자 계정 비활성화 성공 테스트")
    void deactivateUserSuccess() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        userService.deactivateUser("test@example.com");
        
        // then
        assertFalse(testUser.isActive());
        
        // verify
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(testUser);
    }
    
    /**
     * 존재하지 않는 이메일로 비활성화 실패 테스트
     * 
     * 존재하지 않는 이메일로 비활성화 시도 시 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("존재하지 않는 이메일로 비활성화 실패 테스트")
    void deactivateUserNotFound() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deactivateUser("nonexistent@example.com");
        });
        
        assertEquals("사용자를 찾을 수 없습니다: nonexistent@example.com", exception.getMessage());
        
        // verify
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * 사용자 계정 활성화 성공 테스트
     * 
     * 이메일을 기준으로 사용자 계정이 정상적으로 활성화되는지 테스트합니다.
     */
    @Test
    @DisplayName("사용자 계정 활성화 성공 테스트")
    void activateUserSuccess() {
        // given
        testUser.setActive(false); // 비활성화된 상태로 설정
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        userService.activateUser("test@example.com");
        
        // then
        assertTrue(testUser.isActive());
        
        // verify
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(testUser);
    }
    
    /**
     * 존재하지 않는 이메일로 활성화 실패 테스트
     * 
     * 존재하지 않는 이메일로 활성화 시도 시 예외가 발생하는지 테스트합니다.
     */
    @Test
    @DisplayName("존재하지 않는 이메일로 활성화 실패 테스트")
    void activateUserNotFound() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.activateUser("nonexistent@example.com");
        });
        
        assertEquals("사용자를 찾을 수 없습니다: nonexistent@example.com", exception.getMessage());
        
        // verify
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
} 