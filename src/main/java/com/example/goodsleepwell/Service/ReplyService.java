package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Model.sleepBoardReply;
import com.example.goodsleepwell.Model.sleepBoardRereply;
import com.example.goodsleepwell.Model.sleepWellBestReply;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.ReReplyMapper;
import com.example.goodsleepwell.mapper.ReplyMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    public CompletableFuture<DefaultRes> getAllReplylist(int id,int page) {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> replyMapper.findAll(id,page*10,10), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return s;
            }
            return s;
        }).thenApply(s -> {
            if (s.join().size() == 0) return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            JSONArray jsonArray = new JSONArray();
            for (sleepBoardReply reply : s.join()) {
                try {
                    JSONObject json1 = new JSONObject();
                    json1.put("rid", reply.getRid());
                    json1.put("id", reply.getId());
                    json1.put("writter", reply.getWriter());
                    json1.put("replyContent", reply.getReplyContent());
                    json1.put("likecnt", reply.getLikeCount());
                    json1.put("firecnt", reply.getFireCount());
                    List<sleepBoardRereply> ret2 = reReplyMapper.findAll(reply.getRid());
                    if (!ret2.isEmpty()) {
                        JSONArray js2 = new JSONArray();
                        for (sleepBoardRereply el : ret2) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("rrid", el.getRrid());
                            jsonObject.put("rid", el.getRid());
                            jsonObject.put("likeCount", el.getLikeCount());
                            jsonObject.put("fireCount", el.getFireCount());
                            jsonObject.put("writer", el.getWriter());
                            jsonObject.put("reReplyContent", el.getRereplyContent());
                            jsonObject.put("boardIp", el.getBoardIp());
                            js2.put(jsonObject);
                        }
                        json1.put("대댓글", js2);
                        json1.put("reReplyCnt", ret2.size());
                    }
                    jsonArray.put(json1);
                } catch (Exception e) {
                    return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
                }
            }
            s.join();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("replyCnt",s.join().size());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, jsonArray.toString());
        });
        return ret;
    }

    @Async("")
    @Transactional
    public CompletableFuture<DefaultRes> getBestReply(int id) {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(()->{
            return CompletableFuture.supplyAsync(()->replyMapper.findBestReply(id),three);
        },one).thenApply(s->{
            List<sleepBoardReply> ret1 = s.join();
            List<sleepBoardRereply> ret2 = replyMapper.findBestReReply(id);
            List<sleepWellBestReply> ans = new ArrayList<>();
            for(sleepBoardReply sb : ret1) {
                if(sb.getLikeCount()>=5) {
                    sleepWellBestReply t = new sleepWellBestReply();
                    t.setLikeCount(sb.getLikeCount());
                    t.setContent(sb.getReplyContent());
                    t.setFireCount(sb.getFireCount());
                    t.setNumRid(sb.getRid());
                    t.setWriter(sb.getWriter());
                    t.setReplyCheck(true);
                    ans.add(t);
                }
            }
            for(sleepBoardRereply sb : ret2) {
                if(sb.getLikeCount() >= 5) {
                    sleepWellBestReply t = new sleepWellBestReply();
                    t.setLikeCount(sb.getLikeCount());
                    t.setContent(sb.getRereplyContent());
                    t.setFireCount(sb.getFireCount());
                    t.setNumRid(sb.getRrid());
                    t.setWriter(sb.getWriter());
                    t.setReplyCheck(false);
                    ans.add(t);
                }
            }
            ans.sort((a,b)->{
                if(a.getLikeCount()>b.getLikeCount()) return -1;
                else if(a.getLikeCount()<b.getLikeCount()) return 1;
                else {
                    return Integer.compare(b.getNumRid(), a.getNumRid());
                }
            });
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER,ans);
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

    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> checkLike(int rid, String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> replyMapper.checkLike(boardIp, rid), two);
        if (ret.join() == 1) return CompletableFuture.completedFuture(false);
        return CompletableFuture.completedFuture(true);
    }


    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> saveLike(int rid, String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            try {
                replyMapper.likeSave(boardIp, rid);
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
    public CompletableFuture<DefaultRes> likeUpload(int rid) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                replyMapper.likeUpdate(rid);
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

    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> checkFire(int rid, String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> replyMapper.checkFire(boardIp, rid), two);
        if (ret.join() == 1) return CompletableFuture.completedFuture(false);
        return CompletableFuture.completedFuture(true);
    }

    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> saveFire(int rid, String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            try {
                replyMapper.fireSave(boardIp, rid);
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
    public CompletableFuture<DefaultRes> fireUpload(int rid) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                replyMapper.fireUpdate(rid);
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
    public CompletableFuture<Boolean> checkDelete(int rid, String password) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> replyMapper.checkDelete(password, rid), one);
        if (ret.join() == 1) return CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }

    @Async("three")
    public CompletableFuture<DefaultRes<?>> delete(int rid, String password) {
        CompletableFuture<DefaultRes<?>> ret = CompletableFuture.supplyAsync(() -> {
            try {
                replyMapper.delete(password, rid); // 게시글 제거
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
