package org.fs.impl;

import org.fs.common.MultiValueMap;
import org.fs.common.ThreadSafe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class MetadataHandler implements Serializable {

    public static final int METADATA_CHUNK_NUMBER = 0;
    private BitSet chunksAllocation;
    private MultiValueMap<Integer, Integer> fileIdToChunks;

    public MetadataHandler() {
        init();
    }

    private void init() {
        chunksAllocation = new BitSet();
        chunksAllocation.set(METADATA_CHUNK_NUMBER);
        fileIdToChunks = new MultiValueMap<Integer, Integer>();
    }

    public synchronized int allocateNewChunkForFile(int fileId) {
        int newChunk = chunksAllocation.nextClearBit(0);
        chunksAllocation.set(newChunk);
        fileIdToChunks.put(fileId, newChunk);
        return newChunk;
    }

    public synchronized void releaseChunksForFile(int fileId) {
        for (Integer chunkId : getChunksForFile(fileId)) {
            chunksAllocation.clear(chunkId);
        }
        fileIdToChunks.remove(fileId);
    }

    public synchronized List<Integer> getChunksForFile(int fileId) {
        return Collections.unmodifiableList(fileIdToChunks.getSafe(fileId));
    }

    private void writeObject(ObjectOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(fileIdToChunks.getKeys().size());
        for (Integer fileId : fileIdToChunks.getKeys()) {
            dataOutputStream.writeInt(fileId);
            List<Integer> values = fileIdToChunks.getSafe(fileId);
            dataOutputStream.writeInt(values.size());
            for (Integer value : values) {
                dataOutputStream.writeInt(value);
            }
        }
    }

    private void readObject(ObjectInputStream dataInputStream) throws IOException, ClassNotFoundException {
        init();
        int numberOfEntries = dataInputStream.readInt();
        for (int i = 0; i < numberOfEntries; i++) {
            int fileId = dataInputStream.readInt();
            int numberOfChunks = dataInputStream.readInt();
            for (int j = 0; j < numberOfChunks; j++) {
                int chunkNumber = dataInputStream.readInt();
                fileIdToChunks.put(fileId, chunkNumber);
                chunksAllocation.set(chunkNumber);
            }
        }
    }
}
