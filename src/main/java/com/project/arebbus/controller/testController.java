package com.project.arebbus.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class testController {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public String nextGameState() {
        return "Hello World";
    }
}
