package com.example.FanDemo.Service;

import com.example.FanDemo.Model.User;
import com.example.FanDemo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    public User register(User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
}
