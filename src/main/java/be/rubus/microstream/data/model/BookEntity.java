package be.rubus.microstream.data.model;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.time.Year;
import java.util.Objects;

@Entity
public class BookEntity {
    // Called BookEntity so that it Doesn't clash with Book within DataFaker.

    @Id
    private String isbn;
    @Column
    private String title;

    @Column
    private Integer edition;

    @Column
    private Year release;

    @Column
    private String author;

    @Column
    private boolean active;

    BookEntity(String isbn, String title, Integer edition, Year release, String author, boolean active) {
        this.isbn = isbn;
        this.title = title;
        this.edition = edition;
        this.release = release;
        this.author = author;
        this.active = active;
    }

    BookEntity() {
    }

    public String isbn() {
        return isbn;
    }

    public String title() {
        return title;
    }

    public Integer edition() {
        return edition;
    }

    public Year release() {
        return release;
    }

    public String author() {
        return author;
    }

    public boolean active() {
        return active;
    }

    // We want to see if updates to instance make use of the efficient store of the instance itself
    public void setEdition(Integer edition) {
        this.edition = edition;
    }

    public void setRelease(Year release) {
        this.release = release;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookEntity bookEntity = (BookEntity) o;
        return Objects.equals(isbn, bookEntity.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", edition=" + edition +
                ", release=" + release +
                ", author='" + author + '\'' +
                ", active=" + active +
                '}';
    }

    public static BookBuilder builder() {
        return new BookBuilder();
    }
}
