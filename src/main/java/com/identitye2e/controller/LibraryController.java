package com.identitye2e.controller;

import com.identitye2e.exceptions.BookNotFoundException;
import com.identitye2e.exceptions.InsufficientCopiesException;
import com.identitye2e.model.Book;
import com.identitye2e.response.Response;
import com.identitye2e.service.LibraryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryServiceImpl libraryService;

    public LibraryController(LibraryServiceImpl libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/book/add")
    public ResponseEntity<Response> addBook(@RequestBody Book book) {
        if (book.getIsbn() == null) {
            return new ResponseEntity<>(new Response("isbn is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);

        }
        libraryService.addBook(book);
        return new ResponseEntity<>(new Response("Book added successfully", HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/book/remove")
    public ResponseEntity<Response> removeBook(@RequestParam("isbn") String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return new ResponseEntity<>(new Response("isbn is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        try {
            libraryService.removeBook(isbn);
            return new ResponseEntity<>(new Response("Book: " + isbn + " removed successfully", HttpStatus.NO_CONTENT.value()), HttpStatus.NO_CONTENT);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>(new Response(e.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findBookByIsbn")
    public ResponseEntity<Response> findBookByISBN(@RequestParam("isbn") String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ResponseEntity<>(new Response("isbn is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        try {
            Book book = libraryService.findBookByISBN(isbn);
            return new ResponseEntity<>(new Response(book, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>(new Response(e.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findBooksByAuthor")
    public ResponseEntity<?> findBooksByAuthor(@RequestParam("author") String author) {
        if (author == null || author.trim().isEmpty()) {
            return new ResponseEntity<>(new Response("author is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        try {
            List<Book> books = libraryService.findBooksByAuthor(author);
            return new ResponseEntity<>(new Response(books, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>(new Response(e.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/borrowBook")
    public ResponseEntity<?> borrowBook(@RequestParam("isbn") String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ResponseEntity<>(new Response("isbn is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        try {
            libraryService.borrowBook(isbn);
            return new ResponseEntity<>(new Response("Book borrowed!", HttpStatus.OK.value()), HttpStatus.OK);
        } catch (InsufficientCopiesException | BookNotFoundException e) {
            return new ResponseEntity<>(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/returnBook")
    public ResponseEntity<?> returnBook(@RequestParam("isbn") String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ResponseEntity<>(new Response("isbn is empty", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        try {
            libraryService.returnBook(isbn);
            return new ResponseEntity<>(new Response("Book returned!", HttpStatus.OK.value()), HttpStatus.OK);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }
}
