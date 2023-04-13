package be.rubus.microstream.data;

import be.rubus.microstream.data.model.BookBuilder;
import be.rubus.microstream.data.model.BookEntity;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.nosql.Template;
import net.datafaker.Faker;
import net.datafaker.providers.base.Book;

import java.time.Year;
import java.util.List;
import java.util.Random;

public class ConcurrencyIssue {

    private static final Random rnd = new Random();

    public static void main(String[] args) throws InterruptedException {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {

            Template template = container.select(Template.class, new MicrostreamLiteral()).get();

            WriterThread thread = new WriterThread(template);
            thread.start();

            while (thread.isAlive()) {
                long size = template.select(BookEntity.class)
                        .stream().count();
                System.out.printf("Size is %s%n", size);
                if (size > 0) {
                    String id = String.valueOf(rnd.nextInt((int) size));
                    List<Object> matching = template.select(BookEntity.class)
                            .where("isbn").eq(id)
                            .stream().toList();
                    System.out.printf("Retrieved book with id %s%n", id);
                    System.out.println(matching);
                }
                Thread.sleep(10L);
            }

            thread.join();

        }


    }

    private static class WriterThread extends Thread {
        private final Faker faker = new Faker();

        private final Template template;

        private WriterThread(Template template) {
            this.template = template;
        }

        @Override
        public void run() {
            for (int i = 1; i < 100_000; i++) {
                template.insert(createBook(i));
                System.out.printf("Saved book nr %s%n", i);
                Thread.yield();
            }
        }

        private BookEntity createBook(int idx) {
            Book book = faker.book();
            return new BookBuilder().isbn(String.valueOf(idx))
                    .title(book.title())
                    .author(book.author())
                    .release(Year.of(rnd.nextInt(50) + 1970))
                    .build();

        }
    }
}
