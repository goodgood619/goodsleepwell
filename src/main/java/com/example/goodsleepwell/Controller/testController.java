package com.example.goodsleepwell.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class testController {
    @GetMapping("")
    public String test() {
        return "HEllo World!!!!";
    }
}
