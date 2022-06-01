package com.babel.controllers;

import com.babel.entities.LoginForm;
import com.babel.entities.User;
import com.babel.exceptions.IllegalPasswordException;
import com.babel.exceptions.IllegalUsernameException;
import com.babel.exceptions.NotAUserException;
import com.babel.exceptions.WrongPasswordException;
import com.babel.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        } catch (NotAUserException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<User> getUser(@RequestParam String username) {
        try {
            return new ResponseEntity<>(userService.getUser(username), HttpStatus.OK);
        } catch (NotAUserException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody LoginForm loginForm) {
        try {
            userService.addUser(loginForm.getUsername(), loginForm.getPassword());
        } catch (IllegalPasswordException | IllegalUsernameException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User added!", HttpStatus.OK);

    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        try {
            userService.validateCredentials(loginForm.getUsername(), loginForm.getPassword());
        } catch (NotAUserException | WrongPasswordException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User logged in!", HttpStatus.OK);

    }

}
