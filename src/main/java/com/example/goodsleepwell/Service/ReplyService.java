package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.ReplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@EnableAsync
public class ReplyService {
    private ReplyMapper replyMapper;
    private ThreadPoolTaskExecutor one, two, three;

    public ReplyService(ReplyMapper replyMapper, ThreadPoolTaskExecutor one, ThreadPoolTaskExecutor two, ThreadPoolTaskExecutor three) {
        this.replyMapper = replyMapper;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Async("one")
    public CompletableFuture<DefaultRes> getAllReplylist(int id) {
        return CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> replyMapper.findAll(id), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, s.join());
        });
    }
}
