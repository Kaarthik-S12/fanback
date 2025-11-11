package com.example.FanDemo.Controller;

import com.example.FanDemo.Model.User;
import com.example.FanDemo.Repository.UserRepository;
import com.example.FanDemo.Security.JwtUtils;
import com.example.FanDemo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepo;

    // ---------- SIGNUP ----------
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email cannot be empty"));
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password cannot be empty"));
        }

        try {
            userService.register(user);
            return ResponseEntity.ok(Map.of("message", "Signup successful", "email", user.getEmail()));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }


    // ---------- LOGIN ----------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepo.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = optionalUser.get();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token, "email", request.getEmail()));
    }


    // ---------- GET PROFILE ----------
    @GetMapping("/me")
    public Object getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);
            User user = userService.findByEmail(email);
            if (user == null) {
                return Map.of("error", "User not found");
            }
            return user;
        } catch (Exception e) {
            return Map.of("error", "Invalid token");
        }
    }
}

// ---------- MANUAL CLASS FOR LOGIN REQUEST ----------
class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
