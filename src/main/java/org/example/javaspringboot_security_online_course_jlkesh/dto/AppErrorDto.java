package org.example.javaspringboot_security_online_course_jlkesh.dto;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class AppErrorDto {
    private String errorPath;
    private String errorMessage;
    private Integer errorCode;
    private LocalDateTime timestamp;

    public AppErrorDto(String errorPath, String errorMessage, Integer errorCode, LocalDateTime timestamp) {
        this.errorPath = errorPath;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Tashkent")));
    }
}
