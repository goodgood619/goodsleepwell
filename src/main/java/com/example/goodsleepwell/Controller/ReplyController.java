package com.example.goodsleepwell.Controller;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Service.ReplyService;
import com.example.goodsleepwell.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.example.goodsleepwell.Model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@EnableAsync
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService replyService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    public ReplyController(ReplyService replyService,final ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.replyService = replyService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Async("threadPoolTaskExecutor")
    @GetMapping("")
    public CompletableFuture<ResponseEntity> getAllReply(@Param("id") final int id) {
        CompletableFuture<ResponseEntity> result;
        try {
            result = CompletableFuture.completedFuture(new ResponseEntity<>(replyService.getAllReplylist(id).get(), HttpStatus.OK));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return result;
    }
}
