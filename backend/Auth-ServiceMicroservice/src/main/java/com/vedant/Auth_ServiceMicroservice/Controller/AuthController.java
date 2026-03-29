package com.vedant.Auth_ServiceMicroservice.Controller;

import com.vedant.Auth_ServiceMicroservice.Dao.UserRepository;
import com.vedant.Auth_ServiceMicroservice.Dto.LoginRequestDto;
import com.vedant.Auth_ServiceMicroservice.Dto.SignupRequestDto;
import com.vedant.Auth_ServiceMicroservice.Entity.User;
import com.vedant.Auth_ServiceMicroservice.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken( user.getUsername(), user.getRole());
        return ResponseEntity.ok(token);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto request) {
        // 🔴 Validation
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 4) {
            return ResponseEntity.badRequest().body("Password must be at least 4 characters");
        }

        // 🔴 Check if user already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User already exists");
        }

        // ✅ Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER"); // 🔥 default role

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(token);

    }



    @PostMapping("/admin/create")
    public ResponseEntity<?> createAdmin(@RequestBody SignupRequestDto request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);

        return ResponseEntity.ok("Admin created");
    }
}