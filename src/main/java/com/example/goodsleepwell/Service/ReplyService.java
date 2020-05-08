package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Model.sleepBoardReply;
import com.example.goodsleepwell.Model.sleepBoardRereply;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.ReReplyMapper;
import com.example.goodsleepwell.mapper.ReplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@EnableAsync
public class ReplyService {
    private ReplyMapper replyMapper;
    private ReReplyMapper reReplyMapper;
    private ThreadPoolTaskExecutor one, two, three;

    public ReplyService(ReplyMapper replyMapper, ReReplyMapper reReplyMapper, ThreadPoolTaskExecutor one, ThreadPoolTaskExecutor two, ThreadPoolTaskExecutor three) {
        this.replyMapper = replyMapper;
        this.reReplyMapper = reReplyMapper;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Async("one")
    public CompletableFuture<DefaultRes> getAllReplylist(int id) {
        Map<Object, Object> map = new HashMap<>();
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> replyMapper.findAll(id), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return s;
            }
            return s;
        }).thenApply(s -> {
            if (s.join().size() == 0) return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            int cnt = 1;
            for (sleepBoardReply reply : s.join()) {
                try {
                    StringBuilder sb1 = new StringBuilder("rid");
                    sb1.append(cnt);
                    map.put(sb1.toString(), reply);
                    StringBuilder sb2 = new StringBuilder("rrid");
                    sb2.append(cnt);
                    List<sleepBoardRereply> ret2 = reReplyMapper.findAll(reply.getRid());
                    if (!ret2.isEmpty()) map.put(sb2.toString(), ret2);
                    cnt++;
                    log.info(reReplyMapper.findAll(reply.getRid()).toString());
                } catch (Exception e) {
                    return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
                }
            }
            s.join();
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, map);
        });
        return ret;
    }

    @Async("three")
    @Transactional
    public CompletableFuture<Boolean> checkPostorNot(String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> replyMapper.checkPostorNot(boardIp), three);
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
                return CompletableFuture.supplyAsync(() -> replyMapper.checkPostorNot2(boardIp), three);
            }, three).thenApply(s -> {
                if (s.join() == 1) return 1;
                return 0;
            });
            if (ret.join() == 1) return CompletableFuture.completedFuture(true);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("two")
    public CompletableFuture<DefaultRes> save(sleepBoardReply reply) {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            reply.setLikeCount(0);
            reply.setFireCount(0);
            return reply;
        }, two).thenApply(s -> {
            try {
                replyMapper.save(s);
            } catch (Exception e) {
                log.info("replyPostDBError");
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        });
        if (ret.join() == null)
            return CompletableFuture.completedFuture(DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_USER));
        return ret;
    }
}
