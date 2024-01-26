package com.pjh.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/name")
public class name {

    @GetMapping("/get")
    public void testGet(HttpServletRequest request){
        System.out.println("hello,get "+request.getRequestURI());
    }

    @PostMapping("/post")
    public void testPost(HttpServletRequest request){
        System.out.println("hello,post "+request.getRequestURI());
    }

}