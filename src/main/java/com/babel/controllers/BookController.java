package com.babel.controllers;

import com.babel.entities.BookInformation;
import com.babel.entities.Item;
import com.babel.exceptions.*;
import com.babel.repositories.BookRepo;
import com.babel.services.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final GoogleApiService googleApiService;
    private final LibGenApiService libGenApiService;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookController(BookService bookService, BookRepo bookRepo, GoogleApiService googleApiService, LibGenApiService libGenApiService, UserService userService, ItemService itemService) {
        this.bookService = bookService;
        this.userService = userService;
        this.googleApiService = googleApiService;
        this.libGenApiService = libGenApiService;
        this.itemService = itemService;
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
    public ResponseEntity<Object> getBookInformation(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(bookService.getBookInformation(id), HttpStatus.OK);
        } catch (NotABookException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping
    @ApiOperation(value = "get a list of all books available in the library")
    public ResponseEntity<Object> getAllBookInfo() {
        try {
            return new ResponseEntity<>(bookService.getAllBookInfo(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


   /* @PostMapping(value = "/old", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "upload book - deprecated")
    public ResponseEntity<Object> oldUploadBook(@RequestParam("bookFile") MultipartFile book) {
        try {
            bookService.oldAddBook(book, userService.getUser("alpaca"));
        } catch (IOException e) {
            return new ResponseEntity<>("Uploading error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalFileFormatException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (NotAUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("File uploaded", HttpStatus.OK);
    }*/

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "begin book upload contract")
    public ResponseEntity<Object> beginUploadBook(@RequestParam("bookFile") MultipartFile bookFile) {
        try {
            //TODO: Get current session user
            Item item = bookService.startSaveContract(bookFile, userService.getUser("alpaca"));
            return new ResponseEntity<>(item.getId(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Uploading error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalFileFormatException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (NotAUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (GenericDatabaseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/metadata/{id}")
    @ApiOperation(value = "fetch file information from metadata of item")
    public ResponseEntity<Object> fetchMetadata(@PathVariable("id") int id) {
        try {
            return new ResponseEntity(bookService.getBookMetadata(itemService.getItem(id)), HttpStatus.OK);
        } catch (NotAnItemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("File not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "remove any book from database and disk")
    public ResponseEntity<Object> deleteBook(@PathVariable("id") int id) {
        try {
            bookService.deleteBook(bookService.getBook(id));
            return new ResponseEntity("Book " + id + " deleted", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File not found", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotABookException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /*@GetMapping(value = "/libgen-api")
    @ApiOperation(value = "fetch file information from libGen scraping")
    public ResponseEntity<Object> fetchLibGenData(@RequestParam("title") String title){
        try{
            return new ResponseEntity<>()
        }
    }*/

    @PostMapping
    @ApiOperation(value = "complete book upload contract")
    public ResponseEntity<Object> endUploadBook(@RequestBody BookInformation bookInfo, @RequestParam("itemId") int id) {
        try {
            return new ResponseEntity<>(bookService.endSaveContract(bookInfo, id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: Further testing
    @GetMapping("/{id}/file")
    @ApiOperation(value = "get the book file")
    public ResponseEntity<Object> getBookFile(@PathVariable("id") int id) {
        try {
            return new ResponseEntity(itemService.fetchItemFile(bookService.getBook(id).getItem()), HttpStatus.OK);
        } catch (NotABookException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
