package com.prj.cursor.repository;

import com.prj.cursor.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    void saveAndFindUser() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getUserNo());
        
        // then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getNickname());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }
    
    @Test
    @DisplayName("사용자명으로 사용자 찾기 테스트")
    void findByUsername() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        userRepository.save(user);
        
        // when
        Optional<User> foundUser = userRepository.findByNickname("testuser");
        
        // then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getNickname());
    }
    
    @Test
    @DisplayName("이메일로 사용자 찾기 테스트")
    void findByEmail() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        userRepository.save(user);
        
        // when
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // then
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }
    
    @Test
    @DisplayName("사용자명 중복 확인 테스트")
    void existsByUsername() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        userRepository.save(user);
        
        // when & then
        assertTrue(userRepository.existsByNickname("testuser"));
        assertFalse(userRepository.existsByNickname("nonexistent"));
    }
    
    @Test
    @DisplayName("이메일 중복 확인 테스트")
    void existsByEmail() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        userRepository.save(user);
        
        // when & then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
    
    @Test
    @DisplayName("활성 사용자만 조회 테스트")
    void findAllActiveUsers() {
        // given
        User activeUser1 = User.builder()
                .nickname("active1")
                .email("active1@example.com")
                .password("password123")
                .isActive(true)
                .build();
        
        User activeUser2 = User.builder()
                .nickname("active2")
                .email("active2@example.com")
                .password("password123")
                .isActive(true)
                .build();
        
        User inactiveUser = User.builder()
                .nickname("inactive")
                .email("inactive@example.com")
                .password("password123")
                .isActive(false)
                .build();
        
        userRepository.save(activeUser1);
        userRepository.save(activeUser2);
        userRepository.save(inactiveUser);
        
        // when
        List<User> activeUsers = userRepository.findAllActiveUsers();
        
        // then
        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(User::isActive));
    }
} 