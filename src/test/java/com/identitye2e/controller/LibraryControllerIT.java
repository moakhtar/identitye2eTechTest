package com.identitye2e.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.identitye2e.exceptions.BookNotFoundException;
import com.identitye2e.exceptions.InsufficientCopiesException;
import com.identitye2e.model.Book;
import com.identitye2e.service.LibraryServiceImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LibraryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryServiceImpl libraryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addBook_ShouldReturnCreated_WhenBookIsAddedSuccessfully() throws Exception {
        Book book = new Book("1234567890", "Test Title", "Test Author", 2024, 2);
        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(post("/api/library/book/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Book added successfully"));
    }

    @Test
    void addBook_ShouldReturnBadRequest_whenIsbnIsNull() throws Exception {
        Book book = new Book(null, "Test Title", "Test Author", 2024, 2);
        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(post("/api/library/book/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("isbn is empty"));
    }

    @Test
    void removeBook_ShouldReturnBadRequest_WhenIsbnIsEmpty() throws Exception {
        String isbn = "";

        mockMvc.perform(
                        delete("/api/library/book/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                                .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("isbn is empty"));
    }

    @Test
    public void removeBook_ShouldReturnNoContent_WhenBookIsRemovedSuccessfully() throws Exception {
        String isbn = "123456";

        mockMvc.perform(delete("/api/library/book/remove")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.statusCode").value(204))
                .andExpect(jsonPath("$.message").value("Book: " + isbn + " removed successfully"));
    }

    @Test
    public void removeBook_ShouldReturnNotFound_WhenBookIsNotFound() throws Exception {
        String isbn = "123456";

        doThrow(new BookNotFoundException("Book with ISBN " + isbn + " not found")).when(libraryService).removeBook(isbn);

        mockMvc.perform(delete("/api/library/book/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Book with ISBN 123456 not found"));
    }

    @Test
    void findBookByISBN_ShouldReturnNotFound_WhenBookIsNotFound() throws Exception {
        String isbn = "123456";

        doThrow(new BookNotFoundException("Book with ISBN " + isbn + " not found"))
                .when(libraryService).findBookByISBN(isbn);

        mockMvc.perform(get("/api/library/findBookByIsbn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Book with ISBN " + isbn + " not found"));
    }

    @Test
    void findBookByISBN_ShouldReturnOk_WhenBookIsFound() throws Exception {
        String isbn = "123456";
        Book book = new Book(isbn, "Test Title", "Test Author", 2024, 2);

        when(libraryService.findBookByISBN(isbn)).thenReturn(book);

        mockMvc.perform(get("/api/library/findBookByIsbn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.isbn").value(book.getIsbn()))
                .andExpect(jsonPath("$.data.title").value(book.getTitle()))
                .andExpect(jsonPath("$.data.author").value(book.getAuthor()))
                .andExpect(jsonPath("$.data.publicationYear").value(book.getPublicationYear()))
                .andExpect(jsonPath("$.data.availableCopies").value(book.getAvailableCopies()));
    }

    @Test
    void findBookByISBN_ShouldReturnBadRequest_WhenIsbnIsEmpty() throws Exception {
        String isbn = "";

        mockMvc.perform(get("/api/library/findBookByIsbn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("isbn is empty"));
    }


    @Test
    void findBooksByAuthor_ShouldReturnOk_WhenBooksAreFound() throws Exception {
        String author = "Author John";
        Book book = new Book("123456", "Test Title", author, 2024, 2);
        Book book2 = new Book("34567", "Test Title2", author, 2020, 1);

        List<Book> listOfBooks = List.of(book, book2);

        when(libraryService.findBooksByAuthor(author)).thenReturn(listOfBooks);

        mockMvc.perform(get("/api/library/findBooksByAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("author", author))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].isbn").value(book.getIsbn()))
                .andExpect(jsonPath("$.data[0].title").value(book.getTitle()))
                .andExpect(jsonPath("$.data[0].author").value(book.getAuthor()))
                .andExpect(jsonPath("$.data[0].publicationYear").value(book.getPublicationYear()))
                .andExpect(jsonPath("$.data[0].availableCopies").value(book.getAvailableCopies()))
                .andExpect(jsonPath("$.data[1].isbn").value(book2.getIsbn()))
                .andExpect(jsonPath("$.data[1].title").value(book2.getTitle()))
                .andExpect(jsonPath("$.data[1].author").value(book2.getAuthor()))
                .andExpect(jsonPath("$.data[1].publicationYear").value(book2.getPublicationYear()))
                .andExpect(jsonPath("$.data[1].availableCopies").value(book2.getAvailableCopies()));
    }

    @Test
    void findBooksByAuthor_ShouldReturnBadRequest_WhenAuthorIsEmpty() throws Exception {
        String author = "";

        mockMvc.perform(get("/api/library/findBooksByAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("author", author))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("author is empty"));
    }

    @Test
    void findBooksByAuthor_ShouldReturnNotFound_WhenBookIsNotFound() throws Exception {
        String author = "Author John";

        when(libraryService.findBooksByAuthor(author)).thenThrow(new BookNotFoundException("No books found for author: " + author));

        mockMvc.perform(get("/api/library/findBooksByAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("author", author))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("No books found for author: " + author));
    }

    @Test
    void borrowBook_ShouldReturnOk_WhenBookIsBorrowed() throws Exception {
        String isbn = "123456";
        Book book = new Book(isbn, "Test Title", "Test Author", 2024, 2);

        when(libraryService.findBookByISBN(isbn)).thenReturn(book);
        when(libraryService.borrowBook(isbn)).thenReturn(true);

        mockMvc.perform(post("/api/library/borrowBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Book borrowed!"));
    }

    @Test
    void borrowBook_ShouldReturnBadRequest_WhenIsbnIsEmpty() throws Exception {
        String isbn = "";
        Book book = new Book(isbn, "Test Title", "Test Author", 2024, 2);

        when(libraryService.findBookByISBN(isbn)).thenReturn(book);
        when(libraryService.borrowBook(isbn)).thenReturn(true);

        mockMvc.perform(post("/api/library/borrowBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("isbn is empty"));
    }

    @Test
    void borrowBook_ShouldReturnInsufficientCopies_WhenCopiesAreUnavailable() throws Exception {
        String isbn = "123456";

        doThrow(new InsufficientCopiesException("Book with ISBN " + isbn + " not found")).when(libraryService).borrowBook(isbn);

        mockMvc.perform(post("/api/library/borrowBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Book with ISBN " + isbn + " not found"));
    }

    @Test
    void returnBook_ShouldReturnOk_WhenBookIsReturned() throws Exception {
        String isbn = "123456";
        Book book = new Book(isbn, "Test Title", "Test Author", 2024, 2);

        when(libraryService.findBookByISBN(isbn)).thenReturn(book);
        when(libraryService.returnBook(isbn)).thenReturn(true);

        mockMvc.perform(post("/api/library/returnBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Book returned!"));
    }

    @Test
    void returnBook_ShouldReturnBadRequest_WhenIsbnIsEmpty() throws Exception {
        String isbn = "";
        Book book = new Book(isbn, "Test Title", "Test Author", 2024, 2);

        when(libraryService.findBookByISBN(isbn)).thenReturn(book);
        when(libraryService.returnBook(isbn)).thenReturn(true);

        mockMvc.perform(post("/api/library/returnBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("isbn is empty"));
    }

    @Test
    void returnBook_ShouldThrowBookNotFoundException_WhenBookIsNotFound() throws Exception {
        String isbn = "123456";

        doThrow(new BookNotFoundException("Book with ISBN " + isbn + " not found")).when(libraryService).returnBook(isbn);

        mockMvc.perform(post("/api/library/returnBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isbn", isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Book with ISBN " + isbn + " not found"));
    }
}
