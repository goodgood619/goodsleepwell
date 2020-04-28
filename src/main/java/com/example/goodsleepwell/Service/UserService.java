package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Model.sleepWellBoardContent;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public CompletableFuture<DefaultRes> getAllList() {
        return CompletableFuture.supplyAsync(() -> {
            return CompletableFuture.supplyAsync(() -> userMapper.findAll(), three);
        }, one).thenApply(s -> {
            if (s.join().isEmpty()) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, s.join());
        });
    }

    @Async("three")
    @Transactional
    public CompletableFuture<DefaultRes> likeUpload(int id) throws ExecutionException, InterruptedException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            try {
                userMapper.likeupdate(id);
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
                userMapper.dislikeupdate(id);
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
    public CompletableFuture<Boolean> checkPostorNot(String boardIp) {
        CompletableFuture<Integer> ret= CompletableFuture.supplyAsync(()->{
            return CompletableFuture.supplyAsync(()->userMapper.checkPostorNot(boardIp),three);
        },three).thenApply(s->{
            if(s.join() == 0) {
                return 1;
            }
            return 0;
        });
        if(ret.join() == 1) return CompletableFuture.completedFuture(true);
        else {
            log.info("postcheck2");
            ret = CompletableFuture.supplyAsync(()->{
                return CompletableFuture.supplyAsync(()->userMapper.checkPostorNot2(boardIp),three);
            },three).thenApply(s->{
                if(s.join() == 1) return 1;
                return 0;
            });
            if(ret.join() == 1) return CompletableFuture.completedFuture(true);
            return CompletableFuture.completedFuture(false);
        }
    }
    /*
     */
    @Async("two")
    public CompletableFuture<DefaultRes> save(sleepWellBoardContent boardContent) throws JSONException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> ret2 = apiAxiosWithNode(boardContent, containsApi(boardContent.getLinkUrl()));
            String body = ret2.getBody();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(body, JsonObject.class);
            boardContent.setLinkChannel(json.get("linkChannel").getAsString());
            boardContent.setLinkUrl(json.get("linkAddress").getAsString());
            boardContent.setLinkTitle(json.get("linkTitle").getAsString());
            boardContent.setThumbnailUrl(json.get("thumbnailUrl").getAsString());
            boardContent.setLikeCount(0);
            boardContent.setDislikeCount(0);
            boardContent.setFireCount(0);
            return boardContent;
        }, one).thenApply(s -> {
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
        if (s.contains("youtube") || s.contains("youtu.be")) return 1;
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
        else return rt.postForEntity("http://localhost:3000/axios/twitch", request, String.class);
    }
}
