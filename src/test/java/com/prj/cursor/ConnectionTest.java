package com.prj.cursor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConnectionTest {
    
    @Autowired
	private JdbcTemplate jdbcTemplate;

    @Test
	void testDatabaseConnection() {
		try {
			String result = jdbcTemplate.queryForObject("SELECT 'MySQL JDBC 연결 성공!' as message", String.class);
			System.out.println("연결 테스트 결과: " + result);
			assertNotNull(result);
			assertTrue(result.contains("MySQL JDBC 연결 성공"));
		} catch (Exception e) {
			fail("데이터베이스 연결 실패: " + e.getMessage());
		}
	}

	@Test
	void testDatabaseInfo() {
		try {
			String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
			String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
			
			System.out.println("MySQL 버전: " + version);
			System.out.println("현재 데이터베이스: " + database);
			
			assertNotNull(version);
			assertNotNull(database);
			assertTrue(version.contains("8.") || version.contains("5."));
			assertEquals("cursor_db", database);
		} catch (Exception e) {
			fail("데이터베이스 정보 조회 실패: " + e.getMessage());
		}
	}

	@Test
	void testDatabaseOperations() {
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
		} catch (Exception e) {
			fail("데이터베이스 작업 테스트 실패: " + e.getMessage());
		}
	}
}
