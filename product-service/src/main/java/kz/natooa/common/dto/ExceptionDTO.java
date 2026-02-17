package kz.natooa.common.dto;

import java.time.LocalDateTime;

public record ExceptionDTO(
        String errorMessage,
        String detailErrorMessage,
        LocalDateTime errorTime
) {
}
