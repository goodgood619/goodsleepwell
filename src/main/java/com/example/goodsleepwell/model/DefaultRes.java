package com.example.goodsleepwell.model;

import com.example.goodsleepwell.util.ResponseMessage;
import com.example.goodsleepwell.util.StatusCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableAsync
public class DefaultRes<T> {
    private int status;
    private String message;
    private T data;

    public DefaultRes(final int status, final String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static <T> DefaultRes<T> res(final int status, final String message) {
        return res(status, message, null);
    }

    public static <T> DefaultRes<T> res(final int status, final String message, final T t) {
        return DefaultRes.<T>builder()
                .data(t)
                .status(status)
                .message(message)
                .build();
    }

    public static final DefaultRes FAIL_DEFAULT_RES = new DefaultRes(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR);
    public static final DefaultRes FAIL_POST = new DefaultRes(StatusCode.POST_ERROR,ResponseMessage.POST_ERROR);
    public static final DefaultRes FAIL_LIKE = new DefaultRes(StatusCode.LIKE_ERROR,ResponseMessage.LIKE_ERROR);
    public static final DefaultRes FAIL_DELETE= new DefaultRes(StatusCode.DELETE_ERROR, ResponseMessage.DELETE_ERROR);
}
