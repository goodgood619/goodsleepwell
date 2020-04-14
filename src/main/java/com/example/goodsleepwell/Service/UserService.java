package com.example.goodsleepwell.Service;

import com.example.goodsleepwell.Model.DefaultRes;
import com.example.goodsleepwell.Model.sleepWellBoardContent;
import com.example.goodsleepwell.Utils.ResponseMessage;
import com.example.goodsleepwell.Utils.StatusCode;
import com.example.goodsleepwell.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@EnableAsync
public class UserService {
    private UserMapper userMapper;
    private ThreadPoolTaskExecutor one,two,three;

    public UserService(UserMapper userMapper,ThreadPoolTaskExecutor one,ThreadPoolTaskExecutor two, ThreadPoolTaskExecutor three) {
        this.userMapper = userMapper;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Async("three")
    public CompletableFuture<DefaultRes> getAllList() {
        return CompletableFuture.supplyAsync(()->{
            return CompletableFuture.supplyAsync(()->userMapper.findAll(),one);
        },one).thenApply(s->{
            if (s.join().isEmpty()) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, s.join());
        });
    }

    /*
     */
    @Async("one")
    public CompletableFuture<DefaultRes> save(sleepWellBoardContent boardContent) throws ParseException, JSONException {
        CompletableFuture<DefaultRes> ret = CompletableFuture.supplyAsync(()->{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.put("link", Collections.singletonList(boardContent.getLinkUrl()));
            RestTemplate rt = new RestTemplate();
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> ret2 = rt.postForEntity("http://localhost:3000/outsideApi", request, String.class);
            String body = ret2.getBody();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(body, JsonObject.class);
            boardContent.setLinkChannel(json.get("linkChannel").getAsString());
            boardContent.setLinkUrl(json.get("linkAddress").getAsString());
            boardContent.setLinkTitle(json.get("linkTitle").getAsString());
            boardContent.setLikeCount(0);
            boardContent.setDislikeCount(0);
            boardContent.setFireCount(0);
            return boardContent;
        },two).thenApply(s->{
            try {
                userMapper.save(s);
            }
            catch (Exception e) {
                log.error("{}",e.getMessage());
                return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
            }
            return null;
        });
        if(ret == null) {
            return CompletableFuture.supplyAsync(()-> DefaultRes.res(StatusCode.CREATED,ResponseMessage.CREATED_USER));
        }
        return ret;
    }
}
