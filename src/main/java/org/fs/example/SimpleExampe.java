package org.fs.example;

import org.fs.FileSystemFactory;
import org.fs.ISingleFileFS;

import java.io.*;

/**
 * @author Yury Litvinov
 */
public class SimpleExampe {
    public static void main(String[] args) throws IOException {
        File file = createNewFile();
        write(file);
        read(file);
        read(file);
        delete(file);
    }

    private static void write(File file) throws IOException {
        ISingleFileFS fileSystem = FileSystemFactory.create(file);

        OutputStream outputStream = fileSystem.writeFile("test");
        outputStream.write("Hello".getBytes());
        outputStream.close();

        fileSystem.flushMetadata();
    }

    private static void read(File file) throws IOException {
        ISingleFileFS fileSystem = FileSystemFactory.create(file);
        InputStream inputStream = fileSystem.readFile("test");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        System.out.println(reader.readLine());
        fileSystem.flushMetadata();
    }

    private static void delete(File file) throws IOException {
        ISingleFileFS fileSystem = FileSystemFactory.create(file);
        fileSystem.deleteFile("test");
        fileSystem.flushMetadata();
    }

    private static File createNewFile() {
        File file = new File("test.fs");
        file.delete();
        return file;
    }
}
