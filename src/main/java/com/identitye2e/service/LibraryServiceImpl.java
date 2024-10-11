package com.identitye2e.service;

import com.identitye2e.exceptions.BookNotFoundException;
import com.identitye2e.exceptions.InsufficientCopiesException;
import com.identitye2e.model.Book;
import com.identitye2e.model.Library;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final Library library;

    public LibraryServiceImpl(Library library) {
        this.library = library;
    }

    @Override
    public void addBook(Book book) {
        if (book != null) {
            library.addBook(book);
            System.out.println("Book added:" + book.getIsbn()); //Works as logger
        }
    }
    @Override
    @CacheEvict(value = "books", key = "#isbn")
    public void removeBook(String isbn) {
        Optional<Book> book = library.findBookByISBN(isbn);
        if (book.isEmpty()) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
        }
        library.removeBook(isbn);
        System.out.println("Book removed: " + isbn); //Works as logger
    }

    @Override
    @Cacheable(value = "books", key = "#isbn")
    public Book findBookByISBN(String isbn) {
        System.out.println("Fetching book with ISBN: " +  isbn); //Logging here
        Optional<Book> optionalBook = library.findBookByISBN(isbn);

        return optionalBook.orElseThrow(() ->
                new BookNotFoundException("Book with ISBN " + isbn + " not found")
        );
    }

    @Cacheable(value = "books", key = "#author")
    @Override
    public List<Book> findBooksByAuthor(String author) {
        List<Book> books = library.findBooksByAuthor(author);
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found for author: " + author);
        }
        return books;
    }

    @Override
    public boolean borrowBook(String isbn) {
        Optional<Book> optionalBook = library.findBookByISBN(isbn);

        Book book = optionalBook.orElseThrow(() ->
                new BookNotFoundException("Book with ISBN " + isbn + " not found")
        );
        if (book.getAvailableCopies() <= 0) {
            throw new InsufficientCopiesException("No available copies for book with ISBN " + isbn);
        }
        return library.borrowBook(isbn);
    }

    @Override
    public boolean returnBook(String isbn) {
        Optional<Book> optionalBook = library.findBookByISBN(isbn);
        Book book = optionalBook.orElseThrow(() ->
                new BookNotFoundException("Book with ISBN " + isbn + " not found")
        );
        return library.returnBook(book.getIsbn());
    }
}
