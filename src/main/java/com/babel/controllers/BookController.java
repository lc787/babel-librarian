package com.babel.controllers;

import com.babel.entities.Book;
import com.babel.exceptions.IllegalFileFormatException;
import com.babel.exceptions.NoQueryResultsException;
import com.babel.exceptions.NotABookException;
import com.babel.exceptions.NotAUserException;
import com.babel.repositories.BookRepo;
import com.babel.services.BookService;
import com.babel.services.GoogleApiService;
import com.babel.services.LibGenApiService;
import com.babel.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final GoogleApiService googleApiService;
    private final LibGenApiService libGenApiService;
    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, BookRepo bookRepo, GoogleApiService googleApiService, LibGenApiService libGenApiService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        this.googleApiService = googleApiService;
        this.libGenApiService = libGenApiService;
    }


    @GetMapping("/foreign")
    @ApiOperation(value = "test for libgen api call")
    public ResponseEntity<Object> testApp(@RequestParam("bookName") String bookName) {
        try {
            return new ResponseEntity<>(libGenApiService.fetchPossibleBookInfo(bookName, 5), HttpStatus.OK);
        } catch (NoQueryResultsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{id}")
    @ApiOperation(value = "get book info via id")
    public ResponseEntity<Object> getBook(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(bookService.getBook(id), HttpStatus.OK);
        } catch (NotABookException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @ApiOperation(value = "get a list of all books available in the library")
    public List<Book> getBooks() {
        return bookService.getBooks();
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "upload book")
    public ResponseEntity<Object> uploadBook(@RequestParam("bookFile") MultipartFile book) {
        try {
            bookService.addBook(book, userService.getUser("alpaca"));
        } catch (IOException e) {
            return new ResponseEntity<>("Uploading error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalFileFormatException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (NotAUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("File uploaded", HttpStatus.OK);
    }
}
