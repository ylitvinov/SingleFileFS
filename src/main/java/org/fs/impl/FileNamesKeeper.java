package org.fs.impl;

import org.fs.common.ThreadSafe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps map from file name to file id.
 *
 * @author Yury Litvinov
 */
@ThreadSafe
public class FileNamesKeeper implements Serializable {

    public static final int RESERVED_ID_FOR_FILE_NAMES = -1;
    private ConcurrentHashMap<String, Integer> names;
    private AtomicInteger maxFileId;

    public FileNamesKeeper() {
        init();
    }

    private void init() {
        maxFileId = new AtomicInteger(0);
        names = new ConcurrentHashMap<String, Integer>();
    }

    public boolean hasFile(String fileName) {
        return names.containsKey(fileName);
    }

    public int getFileId(String fileName) {
        Integer fileId = names.get(fileName);
        if (fileId == null) {
            throw new IllegalArgumentException();
        }
        return names.get(fileName);
    }

    public void remove(String fileName) {
        names.remove(fileName);
    }

    public int add(String fileName) {
        // This is safe, since #add() should never be called with the same fileName simultaneously
        if (!names.containsKey(fileName)) {
            names.put(fileName, maxFileId.incrementAndGet());
        }
        return names.get(fileName);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(maxFileId.get());
        objectOutputStream.writeInt(names.size());
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            objectOutputStream.writeUTF(entry.getKey());
            objectOutputStream.writeInt(entry.getValue());
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        init();
        maxFileId.set(objectInputStream.readInt());
        int count = objectInputStream.readInt();
        for (int i = 0; i < count; i++) {
            String name = objectInputStream.readUTF();
            int fileId = objectInputStream.readInt();
            names.put(name, fileId);
        }
    }
}
