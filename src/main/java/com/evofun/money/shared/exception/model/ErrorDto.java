package com.evofun.money.shared.exception.model;

import com.evofun.money.shared.exception.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private ErrorCode errorCode;
    private String errorId;
    private String message;
    private List<FieldErrorDto> errors;
}