package com.identitye2e.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {
    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("title")
    private String title;
    @JsonProperty("author")
    private String author;
    @JsonProperty("publicationYear")
    private int publicationYear;
    @JsonProperty("availableCopies")
    private int availableCopies;

    public Book(String isbn, String title, String author, int publicationYear, int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return String.format("Book[ISBN: %s, Title: %s, Author: %s, Year: %d, Copies: %d]",
                isbn, title, author, publicationYear, availableCopies);
    }
}
