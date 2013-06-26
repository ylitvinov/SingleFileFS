package org.fs.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class FileSystemImplTest {

    private File file;
    private FileSystemImpl fileSystem;

    @Before
    public void before() throws IOException {
        file = File.createTempFile("FileSystemImplTest", "");
        file.delete();
        fileSystem = new FileSystemImpl(file);
    }

    @Test(expected = IOException.class)
    public void testReadNonExisting() throws IOException {
        fileSystem.readFile("a.txt");
    }

    @Test(expected = IOException.class)
    public void testDeleteNonExisting() throws IOException {
        fileSystem.deleteFile("a.txt");
    }

    @Test(expected = IOException.class)
    public void testWriteExistingFile() throws IOException {
        OutputStream outputStream = fileSystem.writeFile("a.txt");
        outputStream.close();

        fileSystem.writeFile("a.txt");
    }

    @Test()
    public void testWriteReadNoFlush() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        InputStream inStream = fileSystem.readFile("a.txt");
        String observed = IOUtils.toString(inStream);
        assertThat(observed).isEqualTo(expected);
    }

    @Test()
    public void testWriteReadWithFlush() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        fileSystem.flushMetadata();
        fileSystem = new FileSystemImpl(file);

        InputStream inStream = fileSystem.readFile("a.txt");
        String observed = IOUtils.toString(inStream);
        assertThat(observed).isEqualTo(expected);
    }

    @Test()
    public void testDeleteExisting() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        fileSystem.flushMetadata();
        fileSystem = new FileSystemImpl(file);

        fileSystem.deleteFile("a.txt");

        boolean gotException = false;
        try {
            assertThat(fileSystem.readFile("a.txt"));
        } catch (IOException e) {
            gotException = true;
        }
        assertThat(gotException).isTrue();
    }

    @Test()
    public void testCreateManyFiles() throws IOException {
        int filesNumber = 100;
        for (int i = 0; i < filesNumber; i++) {
            OutputStream outStream = fileSystem.writeFile("a" + i);
            String expected = "Hello world";
            outStream.write(expected.getBytes());
            outStream.close();
        }

        fileSystem.flushMetadata();
        fileSystem = new FileSystemImpl(file);
    }

    @Test(expected = IllegalStateException.class)
    public void testTooManyFiles() throws IOException {
        int filesNumber = 200;
        for (int i = 0; i < filesNumber; i++) {
            OutputStream outStream = fileSystem.writeFile("a" + i);
            String expected = "Hello world";
            outStream.write(expected.getBytes());
            outStream.close();
        }

        fileSystem.flushMetadata();
        fileSystem = new FileSystemImpl(file);
    }

    @Test(expected = IOException.class)
    public void testReadWhenWriteLockUnreleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());

        fileSystem.readFile("a.txt");
    }

    @Test()
    public void testReadWhenWriteLockReleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        fileSystem.readFile("a.txt");
    }

    @Test(expected = IOException.class)
    public void testDeleteWhenWriteLockUnreleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());

        fileSystem.deleteFile("a.txt");
    }

    @Test()
    public void testDeleteWhenWriteLockReleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        fileSystem.deleteFile("a.txt");
    }

    @Test(expected = IOException.class)
    public void testDeleteWhenReadLockUnreleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        fileSystem.readFile("a.txt");

        fileSystem.deleteFile("a.txt");
    }

    @Test()
    public void testDeleteWhenReadLockReleased() throws IOException {
        OutputStream outStream = fileSystem.writeFile("a.txt");
        String expected = "Hello world";
        outStream.write(expected.getBytes());
        outStream.close();

        InputStream inStream = fileSystem.readFile("a.txt");
        inStream.close();

        fileSystem.deleteFile("a.txt");
    }

}
