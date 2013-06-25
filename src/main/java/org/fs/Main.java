package org.fs;

import java.io.*;

/**
 * @author Yury Litvinov
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File file = getNewFile();
        write(file);
        read(file);
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
    }

    private static File getNewFile() {
        File file = new File("test.fs");
        file.delete();
        return file;
    }
}
