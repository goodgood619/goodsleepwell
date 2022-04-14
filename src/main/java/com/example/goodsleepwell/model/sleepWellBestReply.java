package com.example.goodsleepwell.model;

import lombok.Data;

@Data
public class sleepWellBestReply {
    private String writer;
    private String Content;
    private int likeCount;
    private int fireCount;
    private int numRid;
    private boolean replyCheck;
}
