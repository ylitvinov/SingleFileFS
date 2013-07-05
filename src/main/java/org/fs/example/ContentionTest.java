package org.fs.example;

import org.fs.FileSystemFactory;
import org.fs.ISingleFileFS;

import java.io.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author Yury Litvinov
 */
public class ContentionTest {

    private static final int NUMBER_OF_THREADS = 100;
    private static final int NUMBER_OF_FILES = 10;
    private static final int DURATION = 20;

    private final ISingleFileFS fileSystem;
    private final CountDownLatch countDownLatch = new CountDownLatch(NUMBER_OF_THREADS);

    public ContentionTest(ISingleFileFS fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void runTest() {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            new Tester().start();
        }
    }

    public static void main(String[] args) throws IOException {
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
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                read(nextRandFile());
                delete(nextRandFile());
            }
        }

        private void write(int id) {
            try {
                OutputStream outputStream = fileSystem.writeFile(id + ".txt");
                String message = "Hello mister '" + id + "'!";
                outputStream.write(message.getBytes());
                outputStream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void read(int id) {
            try {
                InputStream inputStream = fileSystem.readFile(id + ".txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine();
                fileSystem.flushMetadata();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void delete(int id) {
            try {
                fileSystem.deleteFile(id + ".txt");
                fileSystem.flushMetadata();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void waitOthers() {
            countDownLatch.countDown();
            try {
                countDownLatch.await();
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
