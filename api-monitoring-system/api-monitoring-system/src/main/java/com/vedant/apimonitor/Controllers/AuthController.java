package com.vedant.apimonitor.Controllers;

import com.vedant.apimonitor.Services.AuthService;
import com.vedant.apimonitor.dto.LoginRequest;
import com.vedant.apimonitor.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        String token = authService.register(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(Map.of("token",token));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        String token = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(Map.of("token",token));
    }

}
