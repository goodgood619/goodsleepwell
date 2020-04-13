package com.example.goodsleepwell.Model;

import lombok.Data;

import java.sql.Time;

@Data
public class sleepWellBoardContent {
    private String writer;
    private String writerTitle;
    private String linkUrl;
    private String linkTitle;
    private String linkChannel;
    private int likeCount;
    private int dislikeCount;
    private int fireCount;
}
