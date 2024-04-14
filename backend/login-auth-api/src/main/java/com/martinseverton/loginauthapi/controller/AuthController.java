package com.martinseverton.loginauthapi.controller;

import com.martinseverton.loginauthapi.domain.user.User;
import com.martinseverton.loginauthapi.dto.LoginRequestDTO;
import com.martinseverton.loginauthapi.dto.RegisterRequestDTO;
import com.martinseverton.loginauthapi.dto.UserResponseDTO;
import com.martinseverton.loginauthapi.infra.security.TokenService;
import com.martinseverton.loginauthapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        User user = userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found!"));
        if (passwordEncoder.matches(body.password(), user.getPassword())){
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new UserResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = userRepository.findByEmail(body.email());
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            this.userRepository.save(newUser);

            String token = tokenService.generateToken(newUser);

            return ResponseEntity.ok(new UserResponseDTO(newUser.getName(), token));
            }
        return ResponseEntity.badRequest().build();
    }
}
