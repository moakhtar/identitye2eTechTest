package com.identitye2e.model;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class Library {
    /* This class is to manage a collection of books and provide operations for adding, removing, and searching books.
    It also supports borrowing and returning books  */
    private final Map<String, Book> books;

    public Library() {
        this.books = new ConcurrentHashMap<>();
    }
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public void removeBook(String isbn) {
        books.remove(isbn);
    }


    public Optional<Book> findBookByISBN(String isbn) {
        Book book = books.get(isbn);
        return Optional.ofNullable(book);
    }

    public List<Book> findBooksByAuthor(String author) {
        return books.values().stream()
                        .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                        .collect(Collectors.toList());
    }

    public boolean borrowBook(String isbn) {
        Optional<Book> optionalBook = findBookByISBN(isbn);
            if (optionalBook.isPresent() && optionalBook.get().getAvailableCopies() > 0) {
                Book book = optionalBook.get();
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                return true;
            }
            return false;
    }

    public boolean returnBook(String isbn) {
            Optional<Book> optionalBook = findBookByISBN(isbn);
            if (optionalBook.isPresent() && optionalBook.get().getAvailableCopies() > 0) {
                Book book = optionalBook.get();
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                return true;
            }
            return false;
    }
}
