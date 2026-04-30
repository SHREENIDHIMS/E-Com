package com.app.ecom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.service.UserService;

import java.util.List;

@RestController

@RequestMapping("/api/users")
public class UserController {
	
	
	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers(){
		return new ResponseEntity<>(userService.fetchAllUsers(),
				HttpStatus.OK);
//		return ResponseEntity.ok(userService.fetchAllUsers());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
		return userService.fetchUser(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
		userService.addUser(userRequest);
		return ResponseEntity.ok("User Added Successfully");
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateUser(@PathVariable Long id,
			@RequestBody UserRequest updatedUserRequest){
		boolean updated = userService.updateUser(id, updatedUserRequest);
		if(updated)
		return ResponseEntity.ok("User Updated Successfully");
		return ResponseEntity.notFound().build();
	}
	 

}
