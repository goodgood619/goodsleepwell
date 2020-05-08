package com.example.goodsleepwell.Controller;

import com.example.goodsleepwell.Model.sleepWellBoardContent;
import com.example.goodsleepwell.Service.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.goodsleepwell.Model.DefaultRes.*;

@Slf4j
@RestController
@EnableAsync
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor,one;
    public UserController(UserService userService,final ThreadPoolTaskExecutor threadPoolTaskExecutor,ThreadPoolTaskExecutor one) {
        this.userService = userService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.one = one;
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
    public CompletableFuture<ResponseEntity> downAndUpload(sleepWellBoardContent boardContent) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(()->{
            return userService.checkPostorNot(boardContent.getBoardIp());
        },one).get();
        try {
            log.info("try");
            if(ret.get()) {
                log.info("postcheckok");
                return CompletableFuture.completedFuture(new ResponseEntity<>(userService.save(boardContent).get(), HttpStatus.OK));
            }
            else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_POST,HttpStatus.NOT_ACCEPTABLE));
        }
        catch (Exception e) {
            log.info("error");
            log.info(e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("like")
    public CompletableFuture<ResponseEntity> likeUpdate(@Param("id") final int id,@Param("boardIp") final String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(()->{
            return userService.checkLike(id,boardIp);
        },one).join();
        try {
            if(ret.join()) {
                CompletableFuture.supplyAsync(()->userService.saveLike(id,boardIp));
                return CompletableFuture.completedFuture(new ResponseEntity<>(userService.likeUpload(id).get(), HttpStatus.OK));
            }
            else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_LIKE,HttpStatus.NOT_ACCEPTABLE));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @PutMapping("dislike")
    public CompletableFuture<ResponseEntity> dislikeUpdate(@Param("id") final int id,@Param("boardIp") final String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(()->{
            return userService.checkLike(id,boardIp);
        },one).join();
        try {
            if(ret.join()) {
                CompletableFuture.supplyAsync(() -> userService.saveLike(id, boardIp));
                return CompletableFuture.completedFuture(new ResponseEntity<>(userService.dislikeUpload(id).get(), HttpStatus.OK));
            }
            else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_LIKE,HttpStatus.NOT_ACCEPTABLE));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Async("threadPoolTaskExecutor")
    @DeleteMapping("delete")
    public CompletableFuture<ResponseEntity> deleteContent(@Param("id") final int id,@RequestParam(value = "password", defaultValue = "") final String password) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(()-> {
            return userService.checkDelete(id, password);
        },one).join();
        try {
            if(ret.join()) {
                return CompletableFuture.completedFuture(new ResponseEntity<>(userService.delete(id,password).get(),HttpStatus.OK));
            }
            else return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DELETE,HttpStatus.NOT_ACCEPTABLE));
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
