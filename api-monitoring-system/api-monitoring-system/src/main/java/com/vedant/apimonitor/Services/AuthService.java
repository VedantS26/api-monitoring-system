package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.User;
import com.vedant.apimonitor.Repository.UserRepository;
import com.vedant.apimonitor.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String register(String email, String password){

        if(userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already Registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword_hash(passwordEncoder.encode(password));

        userRepository.save(user);

        return jwtUtil.generateToken(email);


    }

    public String login(String email, String password){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));


        if (!passwordEncoder.matches(password, user.getPassword_hash())){

            throw new RuntimeException("Invalid Password");
        }

        return jwtUtil.generateToken(email);

    }


}
