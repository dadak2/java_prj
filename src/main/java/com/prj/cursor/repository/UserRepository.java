package com.prj.cursor.repository;

import com.prj.cursor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 데이터 접근 계층 (Repository)
 * 
 * Spring Data JPA를 사용하여 User 엔티티의 데이터베이스 작업을 처리합니다.
 * JpaRepository를 상속받아 기본적인 CRUD 작업과 함께 커스텀 쿼리 메서드를 제공합니다.
 * 
 * 주요 기능:
 * - 사용자 조회 (사용자명, 이메일 기준)
 * - 사용자 존재 여부 확인
 * - 활성화된 사용자 필터링
 * - 커스텀 JPQL 쿼리 실행
 * 
 * Spring Boot의 JpaRepositoriesAutoConfiguration에 의해 자동으로 구성됩니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see User
 * @see JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 사용자별명으로 사용자 조회
     * 
     * Spring Data JPA의 메서드 이름 규칙을 사용하여 자동으로 쿼리가 생성됩니다.
     * 사용자별명은 고유값이므로 최대 1개의 결과만 반환됩니다.
     * 
     * @param nickname 조회할 사용자별명
     * @return 사용자 정보를 담은 Optional 객체 (사용자가 없으면 empty)
     * @see Optional
     */
    Optional<User> findByNickname(String nickname);
    
    /**
     * 이메일로 사용자 조회
     * 
     * 이메일 주소를 기준으로 사용자를 조회합니다.
     * 이메일은 고유값이므로 최대 1개의 결과만 반환됩니다.
     * 
     * @param email 조회할 이메일 주소
     * @return 사용자 정보를 담은 Optional 객체 (사용자가 없으면 empty)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자별명 또는 이메일로 사용자 존재 여부 확인
     * 
     * 회원가입 시 중복 확인을 위해 사용됩니다.
     * 사용자별명이나 이메일 중 하나라도 존재하면 true를 반환합니다.
     * 
     * @param nickname 확인할 사용자별명
     * @param email 확인할 이메일 주소
     * @return 사용자별명 또는 이메일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByNicknameOrEmail(String nickname, String email);
    
    /**
     * 사용자별명 존재 여부 확인
     * 
     * 회원가입 시 사용자별명 중복 확인을 위해 사용됩니다.
     * 
     * @param nickname 확인할 사용자별명
     * @return 사용자별명이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 이메일 존재 여부 확인
     * 
     * 회원가입 시 이메일 중복 확인을 위해 사용됩니다.
     * 
     * @param email 확인할 이메일 주소
     * @return 이메일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);
    
    /**
     * 활성화된 사용자만 조회
     * 
     * JPQL을 사용하여 활성화된 사용자만 필터링하여 조회합니다.
     * isActive = true인 사용자들만 반환됩니다.
     * 
     * @return 활성화된 사용자 목록
     * @see User#isActive
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
    
    /**
     * 사용자별명 또는 이메일로 활성화된 사용자 찾기
     * 
     * 로그인 시 사용자별명이나 이메일로 로그인할 수 있도록 지원합니다.
     * 활성화된 사용자만 조회 대상에 포함됩니다.
     * 
     * @param nicknameOrEmail 사용자별명 또는 이메일 주소
     * @return 활성화된 사용자 정보를 담은 Optional 객체
     */
    @Query("SELECT u FROM User u WHERE (u.nickname = :nicknameOrEmail OR u.email = :nicknameOrEmail) AND u.isActive = true")
    Optional<User> findByNicknameOrEmailAndActive(@Param("nicknameOrEmail") String nicknameOrEmail);
} 