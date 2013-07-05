package org.fs.impl.streams.chunk;

import org.fs.common.ThreadSafe;
import org.fs.impl.FileSystemImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class ChunkInputStream extends InputStream {

    private final IRandomAccessFile randomAccessFile;
    private final List<Integer> chunkNumbers;

    private byte[] chunkData = new byte[FileSystemImpl.CHUNK_SIZE];
    private int currentChunk;
    private int currentPosition;

    public ChunkInputStream(IRandomAccessFile randomAccessFile, List<Integer> chunkNumbers) {
        this.randomAccessFile = randomAccessFile;
        this.chunkNumbers = chunkNumbers;
    }

    @Override
    public synchronized int read() throws IOException {
        if (isEOF()) {
            return -1;
        }
        int value = readInt();
        if (value == ChunkOutputStream.EOF) {
            return -1;
        }
        return value;
    }

    private int readInt() throws IOException {
        if (currentPosition == 0 || currentPosition == FileSystemImpl.CHUNK_SIZE) {
            fill();
        }
        byte b = chunkData[currentPosition++];
        return b & 0xff;
    }

    private boolean isEOF() {
        return chunkData == null
                || currentChunk == chunkNumbers.size() && currentPosition == FileSystemImpl.CHUNK_SIZE;
    }

    private void fill() throws IOException {
        if (currentChunk >= chunkNumbers.size()) {
            return;
        }
        int chunkNumber = chunkNumbers.get(currentChunk++);
        currentPosition = 0;
        randomAccessFile.read(chunkNumber * FileSystemImpl.CHUNK_SIZE, chunkData);
    }

    @Override
    public boolean markSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        chunkData = null;
    }
}
