package be.rubus.microstream.data.model;

import java.time.Year;

public class BookBuilder {
    private String isbn;
    private String title;
    private Integer edition;
    private Year release;
    private String author;

    private boolean active;

    public BookBuilder isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookBuilder title(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder edition(Integer edition) {
        this.edition = edition;
        return this;
    }

    public BookBuilder release(Year release) {
        this.release = release;
        return this;
    }

    public BookBuilder author(String author) {
        this.author = author;
        return this;
    }

    public BookBuilder active() {
        this.active = true;
        return this;
    }

    public BookEntity build() {
        return new BookEntity(isbn, title, edition, release, author, active);
    }
}