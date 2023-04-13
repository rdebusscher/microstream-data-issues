package be.rubus.microstream.data;

import be.rubus.microstream.data.model.BookEntity;
import be.rubus.microstream.data.model.Person;
import jakarta.nosql.Template;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

public class KeyIssue {

    public static void main(String[] args) {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {

            Template template = container.select(Template.class, new MicrostreamLiteral()).get();

            String sharedId = "1231";  // @Id value is used as key and thus not unique throughout different entities.
            // But only 1 entity supported within 0.0.2-SNAPSHOT
            BookEntity bookEntity = BookEntity.builder().isbn(sharedId).title("Clean Code").author("Robert Martin")
                    .edition(1).release(Year.of(2020)).build();

            Person person = Person.of(sharedId, "John Doe", LocalDate.now());

            template.insert(bookEntity);
            template.insert(person);

            Optional<BookEntity> optionalBook = template.find(BookEntity.class, sharedId);
            System.out.println(optionalBook.isPresent());
            System.out.println(optionalBook.get());
        }

    }
}
