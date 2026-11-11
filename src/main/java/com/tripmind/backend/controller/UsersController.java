package com.tripmind.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.LoginRequest;
import com.tripmind.backend.dto.LoginResponse;
import com.tripmind.backend.dto.RegisterRequest;
import com.tripmind.backend.dto.RegisterResponse;
import com.tripmind.backend.service.UsersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UsersController {

	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
		RegisterResponse response = usersService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		LoginResponse response = usersService.loginUser(request);

		return ResponseEntity.ok(response);
	}

}
