package com.lms.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HistoryApplication {

	public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.http.fileupload.fileCountMax", "200");
        SpringApplication.run(HistoryApplication.class, args);
        System.out.println("History Application has been started");
	}

}
