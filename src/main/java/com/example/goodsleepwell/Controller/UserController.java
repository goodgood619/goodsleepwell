package com.example.goodsleepwell.Controller;

import com.example.goodsleepwell.Model.sleepWellBoardContent;
import com.example.goodsleepwell.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.example.goodsleepwell.Model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@EnableAsync
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    public UserController(UserService userService,final ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.userService = userService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Async("threadPoolTaskExecutor")
    @GetMapping("")
    public CompletableFuture<ResponseEntity> getAll() {
        CompletableFuture<ResponseEntity> result;
        try {
            result = CompletableFuture.completedFuture(new ResponseEntity<>(userService.getAllList().get(), HttpStatus.OK));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return result;
    }


    @Async("threadPoolTaskExecutor")
    @PostMapping("")
    public CompletableFuture<ResponseEntity> downAndUpload(sleepWellBoardContent boardContent) {
        try {
            return CompletableFuture.completedFuture(new ResponseEntity<>(userService.save(boardContent).get(), HttpStatus.OK));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("like")
    public CompletableFuture<ResponseEntity> likeUpdate(@Param("id") final int id) {
        try {
            return CompletableFuture.completedFuture(new ResponseEntity<>(userService.likeUpload(id).get(),HttpStatus.OK));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("dislike")
    public CompletableFuture<ResponseEntity> dislikeUpdate(@Param("id") final int id) {
        try {
            return CompletableFuture.completedFuture(new ResponseEntity<>(userService.dislikeUpload(id).get(),HttpStatus.OK));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
