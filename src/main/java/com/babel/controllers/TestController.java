package com.babel.controllers;

import com.babel.exceptions.NoQueryResultsException;
import com.babel.services.BookService;
import com.babel.services.LibGenApiService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/test")
public class TestController {
    private final BookService bookService;
    private final LibGenApiService libGenApiService;

    public TestController(BookService bookService, LibGenApiService libGenApiService) {
        this.bookService = bookService;
        this.libGenApiService = libGenApiService;
    }

    @GetMapping("/separate-authors")
    @ApiOperation(value = "test for author separator")
    public ResponseEntity<Object> separateAuthors(@RequestParam("authorString") String authors) {
        return new ResponseEntity<>(bookService.separateAuthors(authors), HttpStatus.OK);
    }


    @GetMapping("/remove-extension")
    @ApiOperation(value = "test for extension remover")
    public ResponseEntity<Object> removeExtension(@RequestParam("word") String word) {
        return new ResponseEntity<>(bookService.removeOneDotExtension(word), HttpStatus.OK);
    }

    @GetMapping("/fetch-md5")
    @ApiOperation(value = "query test for libgenapi")
    public ResponseEntity<Object> queueTest(@RequestParam("query") String query, @RequestParam("resultLimit") int resultLimit) {
        try {
            return new ResponseEntity<>(libGenApiService.fetchPossibleBookInfo(query, resultLimit), HttpStatus.OK);
        } catch (NoQueryResultsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetch-ids")
    @ApiOperation(value = "md5 to id test for libgenapi")
    public ResponseEntity<Object> idTest(@RequestParam("md5") String md5) {
        return new ResponseEntity<>(libGenApiService.getLibGenBookId(md5), HttpStatus.OK);
    }

    @GetMapping("/fetch-info")
    @ApiOperation(value = "id to bookInfo test for libgenapi")
    public ResponseEntity<Object> bookInfoTestLibGen(@RequestParam("id") String id) {
        return new ResponseEntity<>(libGenApiService.getBooksInfoFromLibGenBookId(Collections.singletonList(id)), HttpStatus.OK);
    }
}
