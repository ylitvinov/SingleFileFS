package org.fs.impl;

import org.fs.common.ThreadSafe;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps map from file name to file id.
 *
 * @author Yury Litvinov
 */
@ThreadSafe
public class FileNamesKeeper {

    public static final int RESERVED_ID_FOR_FILE_NAMES = -1;
    private final Map<String, Integer> names = new HashMap<String, Integer>();
    private int maxFileId = 0;

    public FileNamesKeeper() {
    }

    public FileNamesKeeper(InputStream inputStream) {
        try {
            read(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file names. File system is corrupted", e);
        }
    }

    private void read(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        int count = objectInputStream.readInt();
        for (int i = 0; i < count; i++) {
            String name = objectInputStream.readUTF();
            int fileId = objectInputStream.readInt();
            names.put(name, fileId);
            if (fileId > maxFileId) {
                maxFileId = fileId;
            }
        }
    }

    public synchronized void write(OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeInt(names.size());
        for (Map.Entry<String, Integer> stringIntegerEntry : names.entrySet()) {
            objectOutputStream.writeUTF(stringIntegerEntry.getKey());
            objectOutputStream.writeInt(stringIntegerEntry.getValue());
        }
        objectOutputStream.close();
    }

    public synchronized Integer getFileId(String fileName) {
        return names.get(fileName);
    }

    public synchronized void remove(String fileName) {
        names.remove(fileName);
    }

    public synchronized Integer add(String fileName) {
        if (names.containsKey(fileName)) {
            return names.get(fileName);
        }
        int value = ++maxFileId;
        names.put(fileName, value);
        return value;
    }
}
