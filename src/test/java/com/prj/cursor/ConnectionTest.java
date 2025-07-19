package com.prj.cursor;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ConnectionTest {
    
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private HikariDataSource dataSource;

    @Test
	void testDatabaseConnection() {
		System.out.println("=== 데이터베이스 연결 테스트 시작 ===");
		System.out.println("JdbcTemplate 주입 확인: " + (jdbcTemplate != null ? "성공" : "실패"));
		
		try {
			String result = jdbcTemplate.queryForObject("SELECT 'MariaDB JDBC 연결 성공!' as message", String.class);
			System.out.println("연결 테스트 결과: " + result);
			assertNotNull(result);
			assertTrue(result.contains("MariaDB JDBC 연결 성공"));
			System.out.println("=== 연결 테스트 성공 ===");
		} catch (Exception e) {
			System.err.println("=== 연결 테스트 실패 ===");
			System.err.println("에러 타입: " + e.getClass().getSimpleName());
			System.err.println("에러 메시지: " + e.getMessage());
			e.printStackTrace();
			fail("데이터베이스 연결 실패: " + e.getMessage());
		}
	}

	@Test
	void testDatabaseInfo() {
		System.out.println("=== 데이터베이스 정보 테스트 시작 ===");
		try {
			String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
			String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
			
			System.out.println("MariaDB 버전: " + version);
			System.out.println("현재 데이터베이스: " + database);
			
			assertNotNull(version);
			assertNotNull(database);
			assertTrue(version.contains("8.") || version.contains("5.") || version.contains("10."));
			assertEquals("cursor_db", database);
			System.out.println("=== 데이터베이스 정보 테스트 성공 ===");
		} catch (Exception e) {
			System.err.println("=== 데이터베이스 정보 테스트 실패 ===");
			System.err.println("에러 타입: " + e.getClass().getSimpleName());
			System.err.println("에러 메시지: " + e.getMessage());
			e.printStackTrace();
			fail("데이터베이스 정보 조회 실패: " + e.getMessage());
		}
	}

	@Test
	void testConnectionPool() {
		System.out.println("=== 커넥션 풀 테스트 시작 ===");
		try {
			// 커넥션 풀 정보 출력
			System.out.println("커넥션 풀 이름: " + dataSource.getPoolName());
			System.out.println("최대 풀 크기: " + dataSource.getMaximumPoolSize());
			System.out.println("최소 유휴 커넥션: " + dataSource.getMinimumIdle());
			System.out.println("현재 활성 커넥션: " + dataSource.getHikariPoolMXBean().getActiveConnections());
			System.out.println("현재 유휴 커넥션: " + dataSource.getHikariPoolMXBean().getIdleConnections());
			System.out.println("총 커넥션: " + dataSource.getHikariPoolMXBean().getTotalConnections());
			
			// 커넥션 풀 상태 확인
			assertNotNull(dataSource);
			assertTrue(dataSource.getMaximumPoolSize() > 0);
			assertTrue(dataSource.getMinimumIdle() >= 0);
			
			System.out.println("=== 커넥션 풀 테스트 성공 ===");
		} catch (Exception e) {
			System.err.println("=== 커넥션 풀 테스트 실패 ===");
			System.err.println("에러 타입: " + e.getClass().getSimpleName());
			System.err.println("에러 메시지: " + e.getMessage());
			e.printStackTrace();
			fail("커넥션 풀 테스트 실패: " + e.getMessage());
		}
	}

	@Test
	void testDatabaseOperations() {
		System.out.println("=== 데이터베이스 작업 테스트 시작 ===");
		try {
			// 테이블 생성 테스트
			jdbcTemplate.execute("""
				CREATE TABLE IF NOT EXISTS test_table (
					id INT AUTO_INCREMENT PRIMARY KEY,
					name VARCHAR(100) NOT NULL,
					created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
				)
			""");
			System.out.println("테이블 생성 성공");

			// 데이터 삽입 테스트
			int insertResult = jdbcTemplate.update("""
				INSERT INTO test_table (name) VALUES (?)
			""", "테스트 데이터");
			System.out.println("데이터 삽입 결과: " + insertResult + "행");
			assertEquals(1, insertResult);

			// 데이터 조회 테스트
			String name = jdbcTemplate.queryForObject("""
				SELECT name FROM test_table WHERE id = ?
			""", String.class, 1);
			System.out.println("조회된 데이터: " + name);
			assertEquals("테스트 데이터", name);

			// 테이블 삭제 (정리)
			jdbcTemplate.execute("DROP TABLE test_table");
			System.out.println("테이블 삭제 완료");
			System.out.println("=== 데이터베이스 작업 테스트 성공 ===");
		} catch (Exception e) {
			System.err.println("=== 데이터베이스 작업 테스트 실패 ===");
			System.err.println("에러 타입: " + e.getClass().getSimpleName());
			System.err.println("에러 메시지: " + e.getMessage());
			e.printStackTrace();
			fail("데이터베이스 작업 테스트 실패: " + e.getMessage());
		}
	}
}
