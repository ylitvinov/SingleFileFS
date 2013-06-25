package org.fs.impl.streams;

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

    private final ByteDecoder decoder = new ByteDecoder();
    private final byte[] currentChunkData = new byte[FileSystemImpl.CHUNK_SIZE];
    private int currentChunk = 0;
    private int currentPosition = 0;

    public ChunkInputStream(IRandomAccessFile randomAccessFile, List<Integer> chunkNumbers) {
        this.randomAccessFile = randomAccessFile;
        this.chunkNumbers = chunkNumbers;
    }

    @Override
    public synchronized int read() throws IOException {
        if (decoder.isEOF() || isEOF()) {
            return -1;
        }
        decoder.readFirstByte(readEncoded());
        if (decoder.requiresSecondByte()) {
            decoder.readSecondByte(readEncoded());
            if (decoder.isEOF()) {
                return -1;
            }
        }
        return decoder.getValue() & 0xff;
    }

    private boolean isEOF() {
        return currentChunk == chunkNumbers.size() && currentPosition == FileSystemImpl.CHUNK_SIZE;
    }

    private byte readEncoded() throws IOException {
        if (currentPosition == 0 || currentPosition == FileSystemImpl.CHUNK_SIZE) {
            fill();
        }
        return currentChunkData[currentPosition++];
    }

    private void fill() throws IOException {
        if (currentChunk >= chunkNumbers.size()) {
            return;
        }
        int chunkNumber = chunkNumbers.get(currentChunk++);
        currentPosition = 0;
        randomAccessFile.read(chunkNumber * FileSystemImpl.CHUNK_SIZE, currentChunkData);
    }

    @Override
    public boolean markSupported() {
        throw new UnsupportedOperationException();
    }
}
