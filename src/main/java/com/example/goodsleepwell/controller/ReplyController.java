package com.example.goodsleepwell.controller;

import com.example.goodsleepwell.model.sleepBoardReply;
import com.example.goodsleepwell.service.ReplyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.example.goodsleepwell.model.DefaultRes.*;

@Slf4j
@RestController
@EnableAsync
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService replyService;
    private final ThreadPoolTaskExecutor one;

    public ReplyController(ReplyService replyService, final ThreadPoolTaskExecutor one) {
        this.replyService = replyService;
        this.one = one;
    }

    @Async("threadPoolTaskExecutor")
    @GetMapping("")
    public CompletableFuture<ResponseEntity> getAllReply(@Param("id") final int id,@Param("page") final int page) {
        CompletableFuture<ResponseEntity> result;
        try {
            result = CompletableFuture.completedFuture(new ResponseEntity<>(replyService.getAllReplylist(id,page).get(), HttpStatus.OK));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return result;
    }

    @Async("threadPoolTaskExecutor")
    @GetMapping("best")
    public CompletableFuture<ResponseEntity> getBestReply(@Param("id") final int id) {
        CompletableFuture<ResponseEntity> result;
        try {
            result = CompletableFuture.completedFuture(new ResponseEntity<>(replyService.getBestReply(id).join(),HttpStatus.OK));
        }
        catch (Exception e) {
            result = CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return result;
    }
    @Async("threadPoolTaskExecutor")
    @PostMapping("")
    public CompletableFuture<ResponseEntity> replyUpload(sleepBoardReply reply) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> replyService.checkPostorNot(reply.getBoardIp()), one).join();
        try {
            log.info("try");
            if (ret.get()) {
                log.info("replyPostOK");
                return CompletableFuture.completedFuture(new ResponseEntity<>(replyService.save(reply).get(), HttpStatus.OK));
            } else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_POST, HttpStatus.NOT_ACCEPTABLE));
        } catch (Exception e) {
            log.info("error");
            log.info(e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("like")
    public CompletableFuture<ResponseEntity> likeUpdate(@Param("rid") final int rid, @Param("boardIp") final String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> replyService.checkLike(rid, boardIp), one).join();
        try {
            if (ret.join()) {
                CompletableFuture.supplyAsync(() -> replyService.saveLike(rid, boardIp));
                return CompletableFuture.completedFuture(new ResponseEntity<>(replyService.likeUpload(rid).get(), HttpStatus.OK));
            } else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_LIKE, HttpStatus.NOT_ACCEPTABLE));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @DeleteMapping("delete")
    public CompletableFuture<ResponseEntity> deleteContent(@Param("rid") final int rid, @RequestParam(value = "password", defaultValue = "") final String password) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> replyService.checkDelete(rid, password), one).join();
        try {
            if (ret.join()) {
                return CompletableFuture.completedFuture(new ResponseEntity<>(replyService.delete(rid, password).get(), HttpStatus.OK));
            } else
                return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DELETE, HttpStatus.NOT_ACCEPTABLE));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("fire")
    public CompletableFuture<ResponseEntity> fireUpdate(@Param("rid") final int rid,@Param("boardIp") final String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> replyService.checkFire(rid, boardIp), one).join();
        try {
            if (ret.join()) {
                CompletableFuture.supplyAsync(() -> replyService.saveFire(rid, boardIp));
                return CompletableFuture.completedFuture(new ResponseEntity<>(replyService.fireUpload(rid).get(), HttpStatus.OK));
            } else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_LIKE, HttpStatus.NOT_ACCEPTABLE));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
