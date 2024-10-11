package com.identitye2e.service;

import com.identitye2e.model.Book;
import java.util.List;

public interface LibraryService {

    void addBook(Book book);

    void removeBook(String isbn);

    Book findBookByISBN(String isbn);

    List<Book> findBooksByAuthor(String author);

    boolean borrowBook(String isbn);

    boolean returnBook(String isbn);
}
