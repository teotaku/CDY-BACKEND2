package com.cdy.cdy.common.exception;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(hidden = true)
public class ErrorResponse {

    private final int status;
    private final String messages;
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String messages) {

        this.status = status;
        this.messages = messages;
        this.timestamp = LocalDateTime.now();

    }
}
