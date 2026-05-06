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
        String normalizedEmail = normalizeEmail(email);

        if(userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()){
            throw new RuntimeException("Email already Registered");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword_hash(passwordEncoder.encode(password));

        userRepository.save(user);

        return jwtUtil.generateToken(normalizedEmail);


    }

    public String login(String email, String password){
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User Not found"));

        String storedPassword = user.getPassword_hash();
        boolean passwordMatches = passwordEncoder.matches(password, storedPassword);

        if (!passwordMatches && password.equals(storedPassword)) {
            user.setPassword_hash(passwordEncoder.encode(password));
            userRepository.save(user);
            passwordMatches = true;
        }

        if (!passwordMatches){

            throw new RuntimeException("Invalid Password");
        }

        return jwtUtil.generateToken(user.getEmail());

    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new RuntimeException("Email is required");
        }
        return email.trim().toLowerCase();
    }

}
