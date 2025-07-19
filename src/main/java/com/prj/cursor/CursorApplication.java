package com.prj.cursor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CursorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursorApplication.class, args);
	}
/**
	 * 애플리케이션 시작 완료 후 실행되는 이벤트 리스너
	 * 메인 페이지 접근 정보를 출력
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		System.out.println("==========================================");
		System.out.println("## Cursor 프로젝트가 성공적으로 시작되었습니다!");
		System.out.println("==========================================");
		System.out.println("📱 메인 페이지: http://localhost:9090");
		System.out.println("==========================================");
	}
}
