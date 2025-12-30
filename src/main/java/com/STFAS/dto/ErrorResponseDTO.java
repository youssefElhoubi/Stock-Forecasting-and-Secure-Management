package com.STFAS.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int httpCode;
    private String errorType;
    private String message;
    private String path;
}