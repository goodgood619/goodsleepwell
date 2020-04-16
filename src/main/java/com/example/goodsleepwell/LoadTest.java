package com.example.goodsleepwell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();
//        String url = "http://ec2-52-79-169-96.ap-northeast-2.compute.amazonaws.com:8080/users";
        String localurl = "http://localhost:8080/user";
//        String localurl2 = "http://localhost:8080/users";
        String localurl3 = "http://localhost:8080/users";
        String toeicurl = "http://localhost:8080/toeic";
        CyclicBarrier barrier = new CyclicBarrier(101);
//        List<User> ret = rt.exchange("http://localhost:8080", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
//        }).getBody();
        StopWatch main = new StopWatch();
        main.start();
        forPostTest(localurl);
//        for (int i = 0; i < 100; i++) {
//            int temp = i;
//            es.submit(() -> {
//                int idx = counter.addAndGet(1);
//                // blocking시키고, thread 갯수가 101개에 도달하는 순간 blocking 해제(main도 포함해서 101개임 ㅇㅇ)
//
//                barrier.await();
//                log.info("Thread " + idx);
//
//                StopWatch stopWatch = new StopWatch();
//                stopWatch.start();
////                get test
////                ResponseEntity<String> res = rt.getForEntity(localurl3, String.class, idx);
//                ResponseEntity<String> res= rt.getForEntity(localurl,String.class,idx);
//                //for delete
////                if (temp == 0) {
////                    rt.delete("http://localhost:8080/users/264");
////                } else if (temp == 1) {
////                    rt.delete("http://localhost:8080/users/265");
////                } else {
////                    rt.delete("http://localhost:8080/users/266");
////                }
//                stopWatch.stop();
//                log.info("Elapsed: {} {} / {} {}", idx, stopWatch.getTotalTimeSeconds(), res.getStatusCode(), res.getBody());
//                return null;
//            });
//        }
//        barrier.await();
//
//        es.shutdown();
//        es.awaitTermination(100, TimeUnit.SECONDS);
//
//        main.stop();
//        log.info("Total : {}", main.getTotalTimeSeconds());
    }

    private static class User {
        String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    //postTest
    public static void forPostTest(String localurl) {
        //                if(temp==0) {
//                    ResponseEntity<?> res = forPostTest(localurl3, temp);
//
//                }
//                if(temp==1) {
//                    ResponseEntity<?> res = forPostTest(localurl3, temp);
//                }
//                if(temp==2) {
//                    ResponseEntity<?> res = forPostTest(localurl3, temp);
//                }
        for(int i=0;i<100;i++) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("writer", "posttest");
            map.add("linkUrl", "https://youtu.be/oLRU2ZIDEWE");
            map.add("writerTitle", "posttest"+i);
            RestTemplate rt = new RestTemplate();
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
            rt.postForEntity(localurl, request, String.class);
        }
    }

    // for delete test
    private static void forDeleteTest(int temp) {
        RestTemplate rt = new RestTemplate();
    }
}

