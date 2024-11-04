package org.example.javaspringboot_security_online_course_jlkesh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hi(){
        return "Hello";
    }

}
