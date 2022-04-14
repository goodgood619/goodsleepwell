package com.example.goodsleepwell.model;

import lombok.Data;

@Data
public class sleepBoardReply {
    private int rid;
    private int id;
    private String writer;
    private String replyContent;
    private String password;
    private int likeCount;
    private int fireCount;
    private String boardIp;
}
