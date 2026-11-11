
package com.tripmind.backend.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tripmind.backend.dto.LoginRequest;
import com.tripmind.backend.dto.LoginResponse;
import com.tripmind.backend.dto.RegisterRequest;
import com.tripmind.backend.dto.RegisterResponse;
import com.tripmind.backend.entity.Users;
import com.tripmind.backend.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Constructor Dependency Injection
    public UsersService(
            UsersRepository usersRepo,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Find user by ID
    public Optional<Users> findById(Long id) {
        return usersRepo.findById(id);
    }

    // Register user
    public RegisterResponse registerUser(RegisterRequest request) {

        // Check duplicate email
        if (usersRepo.existsByEmail(request.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        // Create user
        Users user = new Users();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encode password using BCrypt
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword_hash(encodedPassword);

        // Save user
        Users savedUser = usersRepo.save(user);

        // Return registration response
        return new RegisterResponse(
                true,
                "User registered successfully",
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    // Login user
    public LoginResponse loginUser(LoginRequest request) {

        // Find user by email
        Users user = usersRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        // Verify password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword_hash()
        );

        // Reject incorrect password
        if (!passwordMatches) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Generate JWT after successful authentication
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        // Return user details and token
        return new LoginResponse(
                true,
                "Login successful",
                user.getId(),
                user.getEmail(),
                token
        );
    }
}
