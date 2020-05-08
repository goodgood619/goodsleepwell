package com.example.goodsleepwell.Controller;

import com.example.goodsleepwell.Model.sleepBoardRereply;
import com.example.goodsleepwell.Service.ReReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.example.goodsleepwell.Model.DefaultRes.FAIL_DEFAULT_RES;
import static com.example.goodsleepwell.Model.DefaultRes.FAIL_POST;

@Slf4j
@RestController
@EnableAsync
@RequestMapping("/rereply")
public class ReReplyController {
    private final ReReplyService reReplyService;
    private final ThreadPoolTaskExecutor one;

    public ReReplyController(ReReplyService reReplyService, ThreadPoolTaskExecutor one) {
        this.reReplyService = reReplyService;
        this.one = one;
    }

    @Async("threadPoolTaskExecutor")
    @PostMapping("")
    public CompletableFuture<ResponseEntity> reReplyUpload(sleepBoardRereply rereply) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            return reReplyService.checkPostorNot(rereply.getBoardIp());
        }, one).join();
        try {
            log.info("try");
            if (ret.get()) {
                log.info("reReplyPostOK");
                return CompletableFuture.completedFuture(new ResponseEntity<>(reReplyService.save(rereply).get(), HttpStatus.OK));
            } else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_POST, HttpStatus.NOT_ACCEPTABLE));
        } catch (Exception e) {
            log.info("error");
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
