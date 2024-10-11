package com.identitye2e.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
    }

    @Test
    void addBook_ShouldAddBookToLibraryAndCache() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2008, 3);

        library.addBook(book);
        Optional<Book> optionalFoundBook = library.findBookByISBN(isbn);

        assertTrue(optionalFoundBook.isPresent());

        Book foundBook = optionalFoundBook.get();
        assertEquals("Title 1", foundBook.getTitle());
        assertEquals("Author 1", foundBook.getAuthor());
    }

    @Test
    void removeBook_ShouldRemoveBookFromLibraryAndCache() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2008, 3);
        library.addBook(book);

        library.removeBook(isbn);
        Optional<Book> foundBook = library.findBookByISBN(isbn);

        assertFalse(foundBook.isPresent());
    }

    @Test
    void findBookByISBN_ShouldReturnBookFromCache_WhenBookExistsInCache() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2006, 2);
        library.addBook(book);

        library.findBookByISBN(isbn);

        Optional<Book> optionalFoundBook = library.findBookByISBN(isbn);
        assertTrue(optionalFoundBook.isPresent());

        Book foundBook = optionalFoundBook.get();
        assertEquals("Title 1", foundBook.getTitle());
        assertEquals("Author 1", foundBook.getAuthor());
    }

    @Test
    void findBooksByAuthor_ShouldReturnBooksByAuthorFromCache() {
        String author = "Author 1";
        Book book1 = new Book("123", "Title 1", author, 2017, 3);
        Book book2 = new Book("124", "Title 2", author, 2008, 3);
        library.addBook(book1);
        library.addBook(book2);

        List<Book> books = library.findBooksByAuthor(author);

        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(book -> book.getTitle().equals("Title 1")));
        assertTrue(books.stream().anyMatch(book -> book.getTitle().equals("Title 2")));
    }

    @Test
    void borrowBook_ShouldReduceAvailableCopies_WhenBookIsAvailable() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2008, 2);
        library.addBook(book);

        boolean borrowed = library.borrowBook(isbn);

        Optional<Book> optionalFoundBook = library.findBookByISBN(isbn);
        assertTrue(optionalFoundBook.isPresent());

        Book foundBook = optionalFoundBook.get();

        assertTrue(borrowed);
        assertEquals(1, foundBook.getAvailableCopies());
    }

    @Test
    void borrowBook_ShouldNotReduceAvailableCopies_WhenBookIsUnavailable() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2008, 0); // No copies available
        library.addBook(book);

        boolean borrowed = library.borrowBook(isbn);
        Optional<Book> optionalFoundBook = library.findBookByISBN(isbn);
        assertTrue(optionalFoundBook.isPresent());

        Book foundBook = optionalFoundBook.get();

        assertFalse(borrowed);
        assertEquals(0, foundBook.getAvailableCopies());
    }

    @Test
    void returnBook_ShouldIncreaseAvailableCopies_WhenBookIsReturned() {
        String isbn = "123";
        Book book = new Book(isbn, "Title 1", "Author 1", 2008, 1);
        library.addBook(book);

        boolean returned = library.returnBook(isbn);
        Optional<Book> optionalFoundBook = library.findBookByISBN(isbn);
        assertTrue(optionalFoundBook.isPresent());

        Book foundBook = optionalFoundBook.get();
        assertTrue(returned);
        assertEquals(2, foundBook.getAvailableCopies());
    }

    @Test
    void returnBook_ShouldNotChangeAnything_WhenBookIsNotFound() {
        boolean returned = library.returnBook("999");

        assertFalse(returned);
    }

}
