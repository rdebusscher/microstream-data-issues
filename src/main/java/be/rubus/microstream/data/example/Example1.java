package be.rubus.microstream.data.example;

import be.rubus.microstream.data.MicrostreamLiteral;
import be.rubus.microstream.data.model.BookEntity;
import be.rubus.microstream.data.model.Version;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.nosql.Template;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Run this example multiple times. The first time, make sure there is NO data directory in the target folder.

// Empty database

// Version entity in database has value 1
// Added 3 books through insert

// Version entity in database has value 2
// Removed 1 book

// Version entity in database has value 3
// Updated 1 book - same instance

// Version entity in database has value 4
// Updated 1 book - different instance

// Version entity in database has value 5
// Insert book with same id

// Version entity in database has value 6
// Removed all edition 1 books

// Version entity in database has value 7
// Updated list of books with a mixture of updated and wew instances

// Version entity in database has value 8
// At the end of the test sequence, no need to run again
public class Example1 {

    public static final String BOOK_ID1 = "1231";
    public static final String BOOK_ID2 = "1232";

    public static void main(String[] args) {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {

            Template template = container.select(Template.class, new MicrostreamLiteral()).get();
            long versionCount = template.select(Version.class).stream().count();
            if (versionCount == 0) {
                System.out.println("Empty database");
                addVersionEntity(template);
            }
            if (versionCount == 1) {
                Version version = (Version) template.select(Version.class).stream().findAny().orElseThrow();
                int versionNumber = version.getValue();
                System.out.println("Version entity in database has value " + versionNumber);
                switch (versionNumber) {
                    case 1 -> addSomeBooksThroughInsert(template);
                    case 2 -> deleteABook(template);
                    case 3 -> updateABook(template);
                    case 4 -> updateABook2(template);
                    case 5 -> insertSameBook(template);
                    case 6 -> deleteAllEdition1(template);
                    case 7 -> putWithListNotPure(template);
                    case 8 -> checkFinalList(template);
                    default -> throw new RuntimeException("Unknown version value " + versionNumber);
                }

                if (versionNumber < 8) {
                    template.insert(new Version(version, versionNumber + 1));
                } else {
                    System.out.println("At the end of the test sequence, no need to run again");
                }
            }
            if (versionCount > 1) {
                System.out.println("ERROR: multiple Version entities!!!");
            }

        }
    }

    private static void checkFinalList(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 3) {
            throw new RuntimeException("We should have 3 books in this step");
        }
        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID2);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException(String.format("Book with id %s is not in the list", BOOK_ID2));
        }

        BookEntity book = bookEntity.get();
        if (book.edition() != 1 || !book.release().equals(Year.of(2001))) {
            throw new RuntimeException(String.format("Book with id %s has not the correct property values", BOOK_ID2));
        }
    }

    private static void putWithListNotPure(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 0) {
            throw new RuntimeException("We should have no books in this step");
        }

        BookEntity effectiveJava = BookEntity.builder().isbn(BOOK_ID2).title("Effective Java").author("Joshua Bloch")
                .edition(2).release(Year.of(2008)).build();
        template.update(effectiveJava);

        List<BookEntity> books = new ArrayList<>();

        books.add(
                BookEntity.builder().isbn(BOOK_ID1).title("Clean Code").author("Robert Martin")
                        .edition(1).release(Year.of(2020)).build());
        effectiveJava.setEdition(1);
        effectiveJava.setRelease(Year.of(2001));
        books.add(effectiveJava);
        books.add(
                BookEntity.builder().isbn("1233").title("Modern Software Engineering").author("David Farley")
                        .edition(1).release(Year.of(2020)).build());

        template.insert(books);

        System.out.println("Updated list of books with a mixture of updated and wew instances");
    }

    private static void deleteAllEdition1(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 2) {
            throw new RuntimeException("We should have 2 books in this step");
        }

        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID2);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException(String.format("Book with id %s is not in the list", BOOK_ID2));
        }

        BookEntity book = bookEntity.get();
        if (book.edition() != 1 || !book.release().equals(Year.of(2001))) {
            throw new RuntimeException(String.format("Book with id %s has not the correct property values", BOOK_ID2));
        }

        template.delete(BookEntity.class).where("edition").eq(1).execute();
        System.out.println("Removed all edition 1 books");
    }

    private static void insertSameBook(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 2) {
            throw new RuntimeException("We should have 2 books in this step");
        }

        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID2);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException(String.format("Book with id %s is not in the list", BOOK_ID2));
        }

        BookEntity book = bookEntity.get();
        if (book.edition() != 2 || !book.release().equals(Year.of(2008))) {
            throw new RuntimeException(String.format("Book with id %s has not the correct property values", BOOK_ID2));
        }

        // insert an existing entry, should just override
        template.insert(
                BookEntity.builder().isbn(BOOK_ID2).title("Effective Java").author("Joshua Bloch")
                        .edition(1).release(Year.of(2001)).build());

        System.out.println("Insert book with same id");


    }

    private static void updateABook2(Template template) {

        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 2) {
            throw new RuntimeException("We should have 2 books in this step");
        }

        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID2);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException(String.format("Book with id %s is not in the list", BOOK_ID2));
        }

        BookEntity book = bookEntity.get();
        if (book.edition() != 3 || !book.release().equals(Year.of(2017))) {
            throw new RuntimeException(String.format("Book with id %s has not the correct property values", BOOK_ID2));
        }

        // same Id, new instance.
        template.update(
                BookEntity.builder().isbn(BOOK_ID2).title("Effective Java").author("Joshua Bloch")
                        .edition(2).release(Year.of(2008)).build());

        System.out.println("Updated 1 book - different instance");
    }

    private static void updateABook(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 2) {
            throw new RuntimeException("We should have 2 books in this step");
        }

        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID1);
        if (bookEntity.isPresent()) {
            throw new RuntimeException(String.format("Book with id %s is still in the list", BOOK_ID1));
        }

        bookEntity = template.find(BookEntity.class, BOOK_ID2);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException(String.format("Book with id %s is not in the list", BOOK_ID2));
        }

        BookEntity book = bookEntity.get();
        book.setEdition(3);
        book.setRelease(Year.of(2017));

        template.update(book);

        System.out.println("Updated 1 book - same instance");
    }

    private static void deleteABook(Template template) {
        long bookCount = template.select(BookEntity.class).stream().count();
        if (bookCount != 3) {
            throw new RuntimeException("We should have 3 books in this step");
        }

        Optional<BookEntity> bookEntity = template.find(BookEntity.class, BOOK_ID1);
        if (bookEntity.isEmpty()) {
            throw new RuntimeException("Book with id 1231 is not found");
        }

        template.delete(BookEntity.class, BOOK_ID1);
        System.out.println("Removed 1 book");
    }

    private static void addSomeBooksThroughInsert(Template template) {
        List<BookEntity> books = new ArrayList<>();

        books.add(
                BookEntity.builder().isbn(BOOK_ID1).title("Clean Code").author("Robert Martin")
                        .edition(1).release(Year.of(2020)).build());
        books.add(
                BookEntity.builder().isbn(BOOK_ID2).title("Effective Java").author("Joshua Bloch")
                        .edition(1).release(Year.of(2001)).build());
        books.add(
                BookEntity.builder().isbn("1233").title("Modern Software Engineering").author("David Farley")
                        .edition(1).release(Year.of(2020)).build());

        template.insert(books);
        System.out.println("Added 3 books through insert");
    }

    private static void addVersionEntity(Template template) {
        template.insert(new Version(1));
    }
}
