package org.fs.impl;

import org.fs.common.MultiValueMap;
import org.fs.common.ThreadSafe;

import java.io.*;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class MetadataHandler {

    public static final int METADATA_CHUNK_NUMBER = 0;
    private final BitSet chunksAllocation = new BitSet();

    {
        chunksAllocation.set(METADATA_CHUNK_NUMBER);
    }

    private final MultiValueMap<Integer, Integer> fileIdToChunks = new MultiValueMap<Integer, Integer>();

    public MetadataHandler() {
    }

    public MetadataHandler(InputStream inputStream) {
        try {
            read(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to metadata. File system was corrupted", e);
        }
    }

    private void read(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
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

    public synchronized void write(OutputStream outputStream) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(fileIdToChunks.getKeys().size());
        for (Integer fileId : fileIdToChunks.getKeys()) {
            dataOutputStream.writeInt(fileId);
            List<Integer> values = fileIdToChunks.getSafe(fileId);
            dataOutputStream.writeInt(values.size());
            for (Integer value : values) {
                dataOutputStream.writeInt(value);
            }
        }
        dataOutputStream.close();
    }

    public synchronized int allocateNewChunkForFile(Integer fileId) {
        int newChunk = chunksAllocation.nextClearBit(0);
        chunksAllocation.set(newChunk);
        fileIdToChunks.put(fileId, newChunk);
        return newChunk;
    }

    public synchronized void releaseChunks(Integer fileId) {
        for (Integer chunkId : getFileNameChunks(fileId)) {
            chunksAllocation.clear(chunkId);
        }
        fileIdToChunks.remove(fileId);
    }

    public synchronized List<Integer> getFileNameChunks(Integer fileId) {
        return Collections.unmodifiableList(fileIdToChunks.getSafe(fileId));
    }
}
