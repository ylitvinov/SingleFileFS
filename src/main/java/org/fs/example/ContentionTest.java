package org.fs.example;

import org.fs.FileSystemFactory;
import org.fs.ISingleFileFS;

import java.io.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Yury Litvinov
 */
public class ContentionTest {

    private static final int NUMBER_OF_THREADS = 100;
    private static final int NUMBER_OF_FILES = 100;
    private static final int DURATION = 100000;

    private final ISingleFileFS fileSystem;
    private final CountDownLatch start = new CountDownLatch(NUMBER_OF_THREADS);
    private final CountDownLatch end = new CountDownLatch(NUMBER_OF_THREADS);

    private final AtomicLong errors = new AtomicLong();
    private final AtomicLong successful = new AtomicLong();

    public ContentionTest(ISingleFileFS fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void runTest() throws InterruptedException {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            new Tester().start();
        }
        end.await();
        System.out.println("Successful operations: " + successful.get());
        System.out.println("Errors: " + errors.get());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ISingleFileFS fileSystem = FileSystemFactory.create(createNewFile());
        new ContentionTest(fileSystem).runTest();
    }

    class Tester extends Thread {

        private Random random = new Random();

        @Override
        public void run() {
            waitOthers();
            for (int i = 0; i < DURATION; i++) {
                write(nextRandFile());
                write(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                delete(nextRandFile());
                flush();
            }
            end.countDown();
        }

        private void write(int id) {
            try {
                OutputStream outputStream = fileSystem.writeFile(id + ".txt");
                String message = "Hello mister '" + id + "'!";
                outputStream.write(message.getBytes());
                outputStream.close();
                successful.incrementAndGet();
            } catch (IOException e) {
                errors.incrementAndGet();
                System.out.println(e.getMessage());
            }
        }

        private void read(int id) {
            try {
                InputStream inputStream = fileSystem.readFile(id + ".txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine();
                successful.incrementAndGet();
            } catch (IOException e) {
                errors.incrementAndGet();
                System.out.println(e.getMessage());
            }
        }

        private void delete(int id) {
            try {
                fileSystem.deleteFile(id + ".txt");
                successful.incrementAndGet();
            } catch (IOException e) {
                errors.incrementAndGet();
                System.out.println(e.getMessage());
            }
        }

        private void flush() {
            try {
                fileSystem.flushMetadata();
                successful.incrementAndGet();
            } catch (IOException e) {
                errors.incrementAndGet();
                System.out.println(e.getMessage());
            }
        }

        private void waitOthers() {
            start.countDown();
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        private int nextRandFile() {
            return random.nextInt(NUMBER_OF_FILES);
        }

    }

    private static File createNewFile() {
        File file = new File("test.fs");
        file.delete();
        return file;
    }
}
