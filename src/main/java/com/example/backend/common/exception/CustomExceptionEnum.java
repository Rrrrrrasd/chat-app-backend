package com.example.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CustomExceptionEnum {
    USERNAME_ALREADY_EXISTS("이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("아이디 혹은 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("Refresh Token이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    CHAT_ROOM_NOT_FOUND("채팅방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    EXPIRED_TOKEN("JWT 만료됨", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 형식", HttpStatus.BAD_REQUEST),
    MALFORMED_TOKEN("잘못된 JWT 형식", HttpStatus.BAD_REQUEST),
    INVALID_SIGNATURE("JWT 서명 검증 실패", HttpStatus.UNAUTHORIZED),
    JWT_VALIDATION_FAILED("JWT 검증 실패", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FRIEND("친구 관계가 아닙니다.", HttpStatus.CONFLICT),
    BLOCKED_USER("차단된 사용자와는 채팅할 수 없습니다.", HttpStatus.CONFLICT),
    CHAT_ROOM_BLOCKED("차단된 사용자와는 채팅할 수 없습니다.", HttpStatus.CONFLICT),
    ;
    private final String message;
    private final HttpStatus status;
}
