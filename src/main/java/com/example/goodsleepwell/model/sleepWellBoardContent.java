package com.example.goodsleepwell.model;

import lombok.Data;

@Data
public class sleepWellBoardContent {
    private int id;
    private String writer;
    private String writerTitle;
    private String linkUrl;
    private String linkTitle;
    private String linkChannel;
    private String thumbnailUrl;
    private int likeCount;
    private int dislikeCount;
    private int fireCount;
    private String boardIp;
    private String password;
}
