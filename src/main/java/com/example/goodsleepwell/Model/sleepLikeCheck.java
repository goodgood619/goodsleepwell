package com.example.goodsleepwell.Model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class sleepLikeCheck {
    private Timestamp likeTime;
    private int id;
    private String boardIp;
}
