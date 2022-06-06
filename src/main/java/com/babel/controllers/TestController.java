package com.babel.controllers;

import com.babel.services.BookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/test")
public class TestController {
    private final BookService bookService;

    public TestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/remove-extension")
    @ApiOperation(value = "test for extension remover")
    public ResponseEntity<Object> removeExtension(@RequestParam("word") String word){
        return new ResponseEntity<>(bookService.removeOneDotExtension(word), HttpStatus.OK);
    }
}
