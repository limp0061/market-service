package com.project.market_service.chatroom.exception;

import com.project.market_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements ErrorCode {

    CANNOT_CHAT_WITH_SELF(HttpStatus.BAD_REQUEST, "자기 자신과 채팅방을 생성할 수 없습니다"),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 채팅방입니다"),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다");
    private final HttpStatus httpStatus;
    private final String message;

}
