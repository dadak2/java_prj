package com.prj.cursor.service;

import com.prj.cursor.dto.UserResponse;
import com.prj.cursor.dto.UserSignupRequest;
import com.prj.cursor.dto.UserLoginRequest;
import com.prj.cursor.entity.User;
import com.prj.cursor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 비즈니스 로직 서비스 클래스
 * 
 * 사용자 관련 모든 비즈니스 로직을 처리하는 서비스 계층입니다.
 * Spring의 @Service 어노테이션을 사용하여 서비스 빈으로 등록됩니다.
 * 
 * 주요 기능:
 * - 회원가입 처리 및 검증
 * - 사용자 정보 조회
 * - 사용자 계정 활성화/비활성화
 * - 비밀번호 암호화
 * - 트랜잭션 관리
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see User
 * @see UserRepository
 * @see UserResponse
 * @see UserSignupRequest
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    /**
     * 사용자 데이터 접근 계층
     * 
     * Spring의 의존성 주입을 통해 UserRepository 인스턴스를 주입받습니다.
     * final 키워드를 사용하여 불변성을 보장합니다.
     */
    private final UserRepository userRepository;
    
    /**
     * 비밀번호 암호화 인코더
     * 
     * Spring Security의 PasswordEncoder를 사용하여 비밀번호를 안전하게 암호화합니다.
     * BCrypt 알고리즘을 사용하여 단방향 해시화를 수행합니다.
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 회원가입 처리
     * 
     * 새로운 사용자 계정을 생성하고 데이터베이스에 저장합니다.
     * 회원가입 과정에서 다양한 검증을 수행합니다.
     * 
     * 처리 과정:
     * 1. 이메일 중복 확인
     * 2. 비밀번호 확인 (입력된 비밀번호와 확인 비밀번호 일치 여부)
     * 3. 비밀번호 암호화
     * 4. 사용자 엔티티 생성 및 저장
     * 5. 응답 DTO 변환 및 반환
     * 
     * @param request 회원가입 요청 데이터 (사용자명, 이메일, 비밀번호 등)
     * @return 생성된 사용자 정보를 담은 UserResponse 객체
     * @throws IllegalArgumentException 검증 실패 시 발생 (비밀번호 불일치, 중복된 사용자명/이메일)
     * @see UserSignupRequest
     * @see UserResponse
     */
    public UserResponse signup(UserSignupRequest request) {
        // 이메일 중복 확인 - 이미 존재하는 이메일인지 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 확인 - 입력된 비밀번호와 확인 비밀번호가 일치하는지 검증
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }  

        // 사용자닉네임 중복 확인 - 이미 존재하는 사용자닉네임인지 검증
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자닉네임입니다.");
        }   
        
        // 사용자 엔티티 생성 - Builder 패턴을 사용하여 객체 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .userRole(User.UserRole.USER) // 기본 역할은 USER로 설정
                .isActive(true) // 기본적으로 활성화된 상태로 생성
                .build();
        
        // 데이터베이스에 사용자 저장
        User savedUser = userRepository.save(user);
        
        // 로그 기록 - 새로운 사용자 가입 정보를 로그로 남김
        log.info("새로운 사용자가 가입했습니다: {}", savedUser.getEmail());
        
        // 응답 DTO로 변환하여 반환
        return UserResponse.from(savedUser);
    }
    
    /**
     * 사용자별명으로 사용자 조회
     * 
     * 사용자별명을 기준으로 사용자 정보를 조회합니다.
     * 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
     * 
     * @param nickname 조회할 사용자별명
     * @return 사용자 정보를 담은 UserResponse 객체
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     * @see UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse findByNickname(String nickname) {
        // 사용자명으로 사용자 조회 후 Optional에서 추출
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + nickname));
        
        // 엔티티를 응답 DTO로 변환하여 반환
        return UserResponse.from(user);
    }
    
    /**
     * 이메일로 사용자 조회
     * 
     * 이메일 주소를 기준으로 사용자 정보를 조회합니다.
     * 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
     * 
     * @param email 조회할 이메일 주소
     * @return 사용자 정보를 담은 UserResponse 객체
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        // 이메일로 사용자 조회 후 Optional에서 추출
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        
        // 엔티티를 응답 DTO로 변환하여 반환
        return UserResponse.from(user);
    }
    
    /**
     * 모든 활성 사용자 조회
     * 
     * 활성화된 사용자들만 조회하여 반환합니다.
     * Stream API를 사용하여 엔티티 목록을 DTO 목록으로 변환합니다.
     * 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
     * 
     * @return 활성화된 사용자 목록을 담은 UserResponse 리스트
     * @see UserResponse
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findAllActiveUsers() {
        // 활성화된 사용자들만 조회
        return userRepository.findAllActiveUsers()
                .stream()
                .map(UserResponse::from) // 각 엔티티를 DTO로 변환
                .collect(Collectors.toList()); // 리스트로 수집
    }
    
    /**
     * 사용자 계정 비활성화
     * 
     * 사용자 계정을 비활성화하여 로그인을 차단합니다.
     * 실제로 데이터를 삭제하지 않고 isActive 필드만 false로 설정합니다.
     * 이메일을 식별자로 사용하여 사용자를 찾습니다.
     * 
     * @param email 비활성화할 사용자의 이메일 주소
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public void deactivateUser(String email) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        
        // 사용자 계정을 비활성화로 설정
        user.setActive(false);
        
        // 변경사항을 데이터베이스에 저장
        userRepository.save(user);
        
        // 로그 기록
        log.info("사용자가 비활성화되었습니다: {} ({})", user.getNickname(), email);
    }
    
    /**
     * 로그인 처리
     * 
     * 사용자 인증을 처리하는 메서드입니다.
     * 사용자명 또는 이메일로 사용자를 찾고, 비밀번호를 검증합니다.
     * 
     * 처리 과정:
     * 1. 사용자명 또는 이메일로 사용자 조회
     * 2. 사용자 존재 여부 확인
     * 3. 계정 활성화 상태 확인
     * 4. 비밀번호 검증
     * 5. 성공 시 사용자 정보 반환
     * 
     * @param request 로그인 요청 데이터 (사용자명/이메일, 비밀번호)
     * @return 인증된 사용자 정보를 담은 UserResponse 객체
     * @throws IllegalArgumentException 인증 실패 시 발생 (사용자 없음, 비밀번호 불일치, 계정 비활성화)
     * @see UserLoginRequest
     * @see UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse login(UserLoginRequest request) {
        // 사용자명 또는 이메일로 사용자 조회
        User user = null;
        
        // 이메일 형식인지 확인
        if (request.getLoginInfo().contains("@")) {
            // 이메일로 조회 시도
            user = userRepository.findByEmail(request.getLoginInfo()).orElse(null);
        } else {
            // 사용자명으로 조회 시도
            user = userRepository.findByNickname(request.getLoginInfo()).orElse(null);
        }
        
        // 사용자가 존재하지 않는 경우
        if (user == null) {
            throw new IllegalArgumentException("사용자별명 또는 이메일이 올바르지 않습니다.");
        }
        
        // 계정이 비활성화된 경우
        if (!user.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        
        // 로그인 성공 로그 기록
        log.info("사용자 로그인 성공: {} ({})", user.getNickname(), user.getEmail());
        
        // 응답 DTO로 변환하여 반환
        return UserResponse.from(user);
    }
    
    /**
     * 사용자 계정 활성화
     * 
     * 비활성화된 사용자 계정을 다시 활성화하여 로그인을 허용합니다.
     * 이메일을 식별자로 사용하여 사용자를 찾습니다.
     * 
     * @param email 활성화할 사용자의 이메일 주소
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public void activateUser(String email) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        
        // 사용자 계정을 활성화로 설정
        user.setActive(true);
        
        // 변경사항을 데이터베이스에 저장
        userRepository.save(user);
        
        // 로그 기록
        log.info("사용자가 활성화되었습니다: {} ({})", user.getNickname(), email);
    }
} 