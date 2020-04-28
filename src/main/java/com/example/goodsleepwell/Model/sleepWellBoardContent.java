package com.example.goodsleepwell.Model;

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
}
