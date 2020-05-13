package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Model.sleepBoardRereply;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.ReReplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@EnableAsync
public class ReReplyService {
    private ReReplyMapper rereplyMapper;
    private ThreadPoolTaskExecutor one, two, three;


    public ReReplyService(ReReplyMapper rereplyMapper, ThreadPoolTaskExecutor one, ThreadPoolTaskExecutor two, ThreadPoolTaskExecutor three) {
        this.rereplyMapper = rereplyMapper;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Async("one")
    public CompletableFuture<DefaultRes> getAllReReplylist(int id) {
        return CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> rereplyMapper.findAll(id), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, s.join());
        });
    }

    @Async("three")
    @Transactional
    public CompletableFuture<Boolean> checkPostorNot(String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> rereplyMapper.checkPostorNot(boardIp), three);
        }, three).thenApply(s -> {
            if (s.join() == 0) {
                return 1;
            }
            return 0;
        });
        if (ret.join() == 1) return CompletableFuture.completedFuture(true);
        else {
            log.info("postcheck2");
            ret = CompletableFuture.supplyAsync(() -> {
                return CompletableFuture.supplyAsync(() -> rereplyMapper.checkPostorNot2(boardIp), three);
            }, three).thenApply(s -> {
                if (s.join() == 1) return 1;
                return 0;
            });
            if (ret.join() == 1) return CompletableFuture.completedFuture(true);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("two")
    public CompletableFuture<DefaultRes> save(sleepBoardRereply rereply) {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            rereply.setLikeCount(0);
            rereply.setFireCount(0);
            return rereply;
        }, two).thenApply(s -> {
            try {
                rereplyMapper.save(s);
            } catch (Exception e) {
                log.info("reReplyPostDBError");
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        });
        if (ret.join() == null)
            return CompletableFuture.completedFuture(DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_USER));
        return ret;
    }

    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> checkLike(int rrid, String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> rereplyMapper.checkLike(boardIp, rrid), two);
        if (ret.join() == 1) return CompletableFuture.completedFuture(false);
        return CompletableFuture.completedFuture(true);
    }


    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> saveLike(int rrid, String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            try {
                rereplyMapper.likeSave(boardIp, rrid);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                return false;
            }
            return true;
        }, two);
        if (ret.join()) return CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }

    @Async("three")
    @Transactional
    public CompletableFuture<DefaultRes> likeUpload(int rrid) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                rereplyMapper.likeUpdate(rrid);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        });
        if (ret.get() == null) {
            return CompletableFuture.supplyAsync(() -> DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_USER));
        } else return ret;
    }

    @Async("one")
    public CompletableFuture<Boolean> checkDelete(int rrid, String password) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> rereplyMapper.checkDelete(password, rrid), one);
        if (ret.join() == 1) return CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }

    @Async("three")
    public CompletableFuture<DefaultRes<?>> delete(int rrid, String password) {
        CompletableFuture<DefaultRes<?>> ret = CompletableFuture.supplyAsync(() -> {
            try {
                rereplyMapper.delete(password, rrid); // 게시글 제거
                rereplyMapper.likeBoardDelete(rrid); // 중복 좋아요(게시글) 제거
            } catch (Exception e) {
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        }, three);
        if (ret.join() == null) {
            return CompletableFuture.supplyAsync(() -> DefaultRes.res(StatusCode.OK, ResponseMessage.DELETE_USER));
        }
        return ret;
    }
}
