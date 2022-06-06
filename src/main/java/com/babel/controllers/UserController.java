package com.babel.controllers;

import com.babel.entities.LoginForm;
import com.babel.entities.User;
import com.babel.exceptions.IllegalPasswordException;
import com.babel.exceptions.IllegalUsernameException;
import com.babel.exceptions.NotAUserException;
import com.babel.exceptions.WrongPasswordException;
import com.babel.services.PLSQLService;
import com.babel.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/users")
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

    @GetMapping("/sql")
    @ApiOperation(value = "call plsql function to compare user account creation dates")
    public ResponseEntity<Object> callFunction(@RequestParam("id1") int id1, @RequestParam("id2") int id2) {
        PLSQLService plsqlService = new PLSQLService();
        try {
            return new ResponseEntity<>(plsqlService.callFunction(id1, id2), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "fetch user info via id")
    public ResponseEntity<Object> getUser(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        } catch (NotAUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    @ApiOperation(value = "fetch user info via username")
    public ResponseEntity<Object> getUser(@RequestParam String username) {
        try {
            return new ResponseEntity<>(userService.getUser(username), HttpStatus.OK);
        } catch (NotAUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    @ApiOperation(value = "register a user")
    public ResponseEntity<String> addUser(@RequestBody LoginForm loginForm) {
        try {
            userService.addUser(loginForm.getUsername(), loginForm.getPassword());
        } catch (IllegalPasswordException | IllegalUsernameException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User added!", HttpStatus.OK);

    }

    @GetMapping("/login")
    @ApiOperation(value = "login a user")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        try {
            userService.validateCredentials(loginForm.getUsername(), loginForm.getPassword());
        } catch (NotAUserException | WrongPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User logged in!", HttpStatus.OK);

    }

}
