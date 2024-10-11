package com.identitye2e.service;

import com.identitye2e.exceptions.BookNotFoundException;
import com.identitye2e.exceptions.InsufficientCopiesException;
import com.identitye2e.model.Book;
import com.identitye2e.model.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

        @Mock
        private Library library;

        @InjectMocks
        private LibraryServiceImpl service;

        @BeforeEach
        void setup() {
            library = new Library();
            service = new LibraryServiceImpl(library);
        }

    @Test
    void addBook_ShouldSaveBook_WhenBookIsAddedSuccessfully() {
        Book book = new Book("123", "Java", "Author", 2020, 5);

        service.addBook(book);

        Optional<Book> foundBook = ofNullable(service.findBookByISBN("123"));

        assertTrue(foundBook.isPresent(), "Book should be present after adding it to the library");
        assertEquals("Java", foundBook.get().getTitle(), "The book title should match the one added.");
    }

    @Test
    void addBook_ShouldThrowException_WhenBookIsNull() {
            String isbn = "123455";

            BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.findBookByISBN(isbn);});

            assertEquals("Book with ISBN " +  isbn +  " not found", exception.getMessage());
    }

    @Test
    void removeBook_ShouldRemoveBook_WhenBookIsRemovedSuccessfully() {
        String isbn = "123455";
        Book book = new Book(isbn, "Java", "Author", 2020, 5);

        service.addBook(book);
        Optional<Book> foundBook = ofNullable(service.findBookByISBN(isbn));

        service.removeBook(isbn);

        assertTrue(foundBook.isPresent(), "Book should not present after adding it to the library");
        assertEquals(isbn, foundBook.get().getIsbn(), "The book title should match the one added.");
    }

    @Test
    void removeBook_ShouldThrowException_WhenBookIsNull() {
        String isbn = "123455";
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.removeBook(isbn);});

        assertEquals("Book with ISBN " +  isbn +  " not found", exception.getMessage());
    }

    @Test
    void removeBook_ShouldEvictBookFromCache_WhenBookRemoved() {

    }

    @Test
    void findBookByISBN_ShouldFindBookByIsbn_WhenBookIsFoundSuccessfully() {
        String isbn = "123455";
        Book book = new Book(isbn, "Java", "Author", 2020, 5);

        service.addBook(book);

        Optional<Book> foundBook = ofNullable(service.findBookByISBN(isbn));

        assertTrue(foundBook.isPresent(), "Book should be present after adding it to the library");
        assertEquals(isbn, foundBook.get().getIsbn(), "The book isbn should match the one added.");
        assertEquals(book.getTitle(), foundBook.get().getTitle(), "The book title should match the one added.");
    }

    @Test
    void findBookByISBN_ShouldThrowException_WhenBookIsReturnedNull() {
        String isbn = "123455";

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.findBookByISBN(isbn);});

        assertEquals("Book with ISBN " +  isbn +  " not found", exception.getMessage());
    }

    @Test
    void findBookByISBN_ShouldFindBookFromCache_WhenBookIsRetrieved() {

    }


    @Test
    void findBookByAuthor_ShouldFindBookByAuthor_WhenBookIsFoundSuccessfully() {
        String author = "Author Name";
        Book book = new Book("12345", "Java", author, 2020, 5);
        Book book1 = new Book("56789", "Spring", author, 2020, 5);
        Book book2 = new Book("11234", "Java", "Tom", 2020, 5);

        service.addBook(book);
        service.addBook(book1);
        service.addBook(book2);

        List<Book> foundBooks = service.findBooksByAuthor(author);

        assertEquals(2, foundBooks.size()," Two Books should return by the author");
    }

    @Test
    void findBookByAuthor_ShouldThrowException_WhenBooksAreNotFound() {
        String author = "Author Name";

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.findBooksByAuthor(author);});

        assertEquals("No books found for author: " + author, exception.getMessage());
    }

    @Test
    void findBookByAuthor_ShouldFindBooksFromCache_WhenAuthorIsFiltered() {

    }

    @Test
    void borrowBook_ShouldBorrowBook_WhenBookIsAvailable() {
        String isbn = "123455";
        Book book = new Book(isbn, "Java", "author", 2020, 2);

        service.addBook(book);
        service.borrowBook(isbn);

        Optional<Book> bookWithReducedCopies = ofNullable(service.findBookByISBN(isbn));

        assertTrue(bookWithReducedCopies.isPresent(), "Book should be present before borrowing it from the library");
        assertEquals(1, bookWithReducedCopies.get().getAvailableCopies(), "The available copies will be reduced by 1.");
    }

    @Test
    void borrowBook_ShouldThrowException_WhenBookIsUnavailable() {
        String isbn = "123455";
        Book book = new Book(isbn, "Java", "author", 2020, 0);

        service.addBook(book);

        InsufficientCopiesException exception = assertThrows(InsufficientCopiesException.class, () -> {
            service.borrowBook(isbn);});

        assertEquals("No available copies for book with ISBN " + isbn, exception.getMessage());
    }

    @Test
    void borrowBook_ShouldThrowException_WhenBookIsNull() {
        String isbn = "123455";

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.borrowBook(isbn);});

        assertEquals("Book with ISBN " + isbn + " not found", exception.getMessage());
    }

    @Test
    void returnBook_ShouldReturnBook_WhenBookIsAvailable() {
        String isbn = "123455";
        Book book = new Book(isbn, "Java", "author", 2020, 2);

        service.addBook(book);
        service.returnBook(isbn);

        Optional<Book> bookWithIncreasedCopies = ofNullable(service.findBookByISBN(isbn));

        assertTrue(bookWithIncreasedCopies.isPresent(), "Book should be present before borrowing it from the library");
        assertEquals(3, bookWithIncreasedCopies.get().getAvailableCopies(), "The available copies will be reduced by 1.");
    }

    @Test
    void returnBook_ShouldThrowException_WhenBookIsNull() {
        String isbn = "123455";

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            service.returnBook(isbn);});

        assertEquals("Book with ISBN " + isbn + " not found", exception.getMessage());
    }
}
