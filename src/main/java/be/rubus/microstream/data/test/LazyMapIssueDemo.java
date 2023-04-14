package be.rubus.microstream.data.test;

import be.rubus.microstream.data.model.BookBuilder;
import be.rubus.microstream.data.model.BookEntity;
import net.datafaker.Faker;
import net.datafaker.providers.base.Book;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.types.StorageManager;

import java.time.Year;
import java.util.Random;

public class LazyMapIssueDemo {

    private static final Random rnd = new Random();

    public static void main(String[] args) throws InterruptedException {

        DataStorage root = new DataStorage();

        // Initialize a storage manager ("the database")
        try (StorageManager storageManager = createStorageManager(root)) {

            // Start
            storageManager.start();
            root.setPersister(storageManager);

            WriterThread thread = new WriterThread(root);

            thread.start();
            Thread.sleep(10L);

            while (thread.isAlive()) {
                long size = root.size();
                System.out.printf("Size is %s%n", size);
                if (size > 0) {
                    String id = String.valueOf(rnd.nextInt((int) size));

                    // No problem as not using iterators.
                    //Optional<Object> matching = root.get(id);

                    // The following statement triggers the problem because
                    // .collect(Collectors.toList()); runs outside the synchronized `streamValues()` method

                    /*
                    List<BookEntity> matching = root.values().map(o -> (BookEntity) o)
                            .filter(b -> b.isbn().equals(id))
                            .collect(Collectors.toList());

                     */


                    // This is fine as everything runs inside synchronized methods.
                    BookEntity matching = root.find(id);

                    System.out.printf("Retrieved book with id %s%n", id);
                    System.out.println(matching);
                }
                Thread.sleep(10L);
            }
            thread.join();

        }


    }

    private static StorageManager createStorageManager(DataStorage root) {

        // requires  microstream-storage-embedded-configuration dependency
        return EmbeddedStorageConfiguration.Builder()
                .setStorageDirectory("target/data2")
                .setChannelCount(4)

                .createEmbeddedStorageFoundation()
                // Further customise the Foundation if needed.
                //.onConnectionFoundation()
                .setRoot(root)

                .createEmbeddedStorageManager();
    }

    private static class WriterThread extends Thread {
        private final Faker faker = new Faker();
        private final DataStorage root;

        private WriterThread(DataStorage root) {
            this.root = root;
        }

        @Override
        public void run() {
            for (int i = 1; i < 100_000; i++) {
                BookEntity book = createBook(i);
                root.put(book.isbn(), book);
                //System.out.printf("Saved book nr %s%n", i);
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
