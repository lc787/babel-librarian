package com.babel.controllers;

import com.babel.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/test")
public class TestController {
    private final BookService bookService;

    public TestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/remove-extension")
    public ResponseEntity<Object> removeExtension(@RequestParam("word") String word){
        return new ResponseEntity<>(bookService.removeOneDotExtension(word), HttpStatus.OK);
    }
}
