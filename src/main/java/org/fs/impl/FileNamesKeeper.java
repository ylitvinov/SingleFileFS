package org.fs.impl;

import org.fs.common.ThreadSafe;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps map from file name to file id.
 *
 * @author Yury Litvinov
 */
@ThreadSafe
public class FileNamesKeeper implements Serializable {

    public static final int RESERVED_ID_FOR_FILE_NAMES = -1;
    private Map<String, Integer> names;
    private int maxFileId = 0;

    public FileNamesKeeper() {
        init();
    }

    private void init() {
        names = new HashMap<String, Integer>();
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

    private void writeObject(java.io.ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(names.size());
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            objectOutputStream.writeUTF(entry.getKey());
            objectOutputStream.writeInt(entry.getValue());
        }
    }

    private void readObject(java.io.ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        init();
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
}
