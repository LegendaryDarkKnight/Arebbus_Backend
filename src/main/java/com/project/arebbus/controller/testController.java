package com.project.arebbus.controller;
import com.project.arebbus.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-test")
public class testController {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public String nextGameState() {
        return "Hello World Actions 2";
    }

    @GetMapping("/whoamii")
    public ResponseEntity<String> whoAmI() {
        System.out.println("authentication");
        String email = JwtUtil.extractEmail();
        if(email==null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        else
            return ResponseEntity.ok("Hello "+ email);
    }
}
