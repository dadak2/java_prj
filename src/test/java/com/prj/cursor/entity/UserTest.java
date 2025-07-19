package com.prj.cursor.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    @Test
    @DisplayName("User 엔티티 생성 테스트")
    void createUser() {
        // given
        String nickname = "testuser";
        String email = "test@example.com";
        String password = "password123";
        
        // when
        User user = User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
        
        // then
        assertNotNull(user);
        assertEquals(nickname, user.getNickname());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(User.UserRole.USER, user.getUserRole());
        assertTrue(user.isActive());
    }
    
    @Test
    @DisplayName("User 엔티티 기본값 테스트")
    void userDefaultValues() {
        // when
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        // then
        assertEquals(User.UserRole.USER, user.getUserRole());
        assertTrue(user.isActive());
    }
    
    @Test
    @DisplayName("User 엔티티 ADMIN 역할 설정 테스트")
    void userAdminRole() {
        // when
        User user = User.builder()
                .nickname("admin")
                .email("admin@example.com")
                .password("admin123")
                .userRole(User.UserRole.ADMIN)
                .build();
        
        // then
        assertEquals(User.UserRole.ADMIN, user.getUserRole());
    }
    
    @Test
    @DisplayName("User 엔티티 비활성화 테스트")
    void userDeactivation() {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        // when
        user.setActive(false);
        
        // then
        assertFalse(user.isActive());
    }
} 