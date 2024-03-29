package com.example.goodsleepwell.service;

import com.example.goodsleepwell.mapper.UserMapper;
import com.example.goodsleepwell.model.DefaultRes;
import com.example.goodsleepwell.model.sleepWellBoardContent;
import com.example.goodsleepwell.util.ResponseMessage;
import com.example.goodsleepwell.util.StatusCode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@EnableAsync
public class UserService {
    private UserMapper userMapper;
    private ThreadPoolTaskExecutor one, two, three;

    public UserService(UserMapper userMapper, ThreadPoolTaskExecutor one, ThreadPoolTaskExecutor two, ThreadPoolTaskExecutor three) {
        this.userMapper = userMapper;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Async("one")
    @Transactional
    public CompletableFuture<DefaultRes> getAllList(int page,int pageChoice) {
        return CompletableFuture.supplyAsync(() -> {
            if(pageChoice==0) {
                return CompletableFuture.supplyAsync(() -> userMapper.findAllLikeOrder(page*20,pageChoice,20), three);
            }
            else if(pageChoice == 1) return CompletableFuture.supplyAsync(() -> userMapper.findAllrecentOrder(page*20,pageChoice,20), three);
            else return CompletableFuture.supplyAsync(() -> userMapper.findAllHotOrder(page*20,pageChoice,20), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            }
            JSONArray jsonArray = new JSONArray();
            for (sleepWellBoardContent value : s.join()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    int replyCnt = userMapper.replyCount(value.getId());
                    int reReplyCnt = userMapper.reReplyCount(value.getId());
                    jsonObject.put("id", value.getId());
                    jsonObject.put("linkUrl", value.getLinkUrl());
                    jsonObject.put("linkChannel", value.getLinkChannel());
                    jsonObject.put("thumbnailUrl", value.getThumbnailUrl());
                    jsonObject.put("dislikeCount", value.getDislikeCount());
                    jsonObject.put("fireCount", value.getFireCount());
                    jsonObject.put("likeCount", value.getLikeCount());
                    jsonObject.put("boardIp", value.getBoardIp());
                    jsonObject.put("linkTitle", value.getLinkTitle());
                    jsonObject.put("writer", value.getWriter());
                    jsonObject.put("writerTitle", value.getWriterTitle());
                    jsonObject.put("replyCnt", replyCnt + reReplyCnt);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, jsonArray.toString());
        });
    }

    @Async("three")
    @Transactional
    public CompletableFuture<DefaultRes> likeUpload(int id) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.likeUpdate(id);
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

    @Async("three")
    @Transactional
    public CompletableFuture<DefaultRes> dislikeUpload(int id) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.dislikeUpdate(id);
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
    public CompletableFuture<Boolean> checkLike(int id, String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> userMapper.checkLike(boardIp, id), two);
        if (ret.join() == 1) return CompletableFuture.completedFuture(false);
        else return CompletableFuture.completedFuture(true);
    }


    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> saveLike(int id, String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.likeSave(boardIp, id);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                return false;
            }
            return true;
        }, two);
        if (ret.join()) return CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }


    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> checkFire(int id, String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> userMapper.checkFire(boardIp, id), two);
        if (ret.join() == 1) return CompletableFuture.completedFuture(false);
        else return CompletableFuture.completedFuture(true);
    }

    @Async("two")
    @Transactional
    public CompletableFuture<Boolean> saveFire(int id, String boardIp) {
        CompletableFuture<Boolean> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.fireSave(boardIp, id);
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
    public CompletableFuture<DefaultRes> fireUpload(int id) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.fireUpdate(id);
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
    public CompletableFuture<Boolean> checkDelete(int id, String password) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> userMapper.checkDelete(password, id), one);
        if (ret.join() == 1) return CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }

    @Async("three")
    public CompletableFuture<DefaultRes<?>> delete(int id, String password) {
        CompletableFuture<DefaultRes<?>> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.delete(password, id); // 게시글 제거
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

    @Async("three")
    @Transactional
    public CompletableFuture<Boolean> checkPostorNot(String boardIp) {
        CompletableFuture<Integer> ret = CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> userMapper.checkPostorNot(boardIp), three);
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
                return CompletableFuture.supplyAsync(() -> userMapper.checkPostorNot2(boardIp), three);
            }, three).thenApply(s -> {
                if (s.join() == 1) return 1;
                return 0;
            });
            if (ret.join() == 1) return CompletableFuture.completedFuture(true);
            return CompletableFuture.completedFuture(false);
        }
    }

    /*
     */
    @Async("two")
    public CompletableFuture<DefaultRes> save(sleepWellBoardContent boardContent) throws JSONException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> ret2 = apiAxiosWithNode(boardContent, containsApi(boardContent.getLinkUrl()));
            if (ret2.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return boardContent;
            }
            String body = ret2.getBody();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(body, JsonObject.class);
            boardContent.setLinkUrl(json.get("linkAddress").getAsString());
            boardContent.setThumbnailUrl(json.get("thumbnailUrl").getAsString());
            boardContent.setLikeCount(0);
            boardContent.setDislikeCount(0);
            boardContent.setFireCount(0);
            return boardContent;
        }, one).thenApply(s -> {
            if (boardContent.getLinkChannel().equals("")) {
                log.info(boardContent.getLinkChannel());
                return DefaultRes.res(StatusCode.BAD_REQUEST, ResponseMessage.INTERNAL_SERVER_ERROR);
            }
            try {
                userMapper.save(s);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        });
        if (ret == null) {
            return CompletableFuture.supplyAsync(() -> DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_USER));
        }
        return ret;
    }

    private int containsApi(String s) {
        if (s.contains("youtube.com/watch") || s.contains("youtu.be")) return 1;
        if (s.contains("twitch")) return 2;
        return 0;
    }

    public ResponseEntity<String> apiAxiosWithNode(sleepWellBoardContent boardContent, int choice) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("link", Collections.singletonList(boardContent.getLinkUrl()));
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        if (choice == 1) return rt.postForEntity("http://localhost:3000/axios/youtube", request, String.class);
        else if (choice == 2) return rt.postForEntity("http://localhost:3000/axios/twitch", request, String.class);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
