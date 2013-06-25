package org.fs.impl.streams;

import org.fs.common.ThreadSafe;
import org.fs.impl.FileSystemImpl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class ChunkOutputStream extends OutputStream {
    private final IRandomAccessFile randomAccessFile;
    private final ChunksAllocator chunksAllocator;

    private final ByteEncoder encoder = new ByteEncoder();
    private final byte[] chunkData = new byte[FileSystemImpl.CHUNK_SIZE];
    private int position;
    private Integer currentChunkNumber;

    public ChunkOutputStream(IRandomAccessFile randomAccessFile, ChunksAllocator chunksAllocator) {
        this.randomAccessFile = randomAccessFile;
        this.chunksAllocator = chunksAllocator;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        byte value = (byte) b;
        encoder.encode(value);
        writeEncoded(encoder.firstByte());
        if (encoder.requiresSecondByte()) {
            writeEncoded(encoder.secondByte());
        }
    }

    private void writeEncoded(byte value) throws IOException {
        if (currentChunkNumber == null) {
            currentChunkNumber = chunksAllocator.allocateNewChunk();
        }
        chunkData[position++] = value;
        if (position >= FileSystemImpl.CHUNK_SIZE) {
            flush();
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        int currentChunkNumberSaved = currentChunkNumber;
        if (position >= FileSystemImpl.CHUNK_SIZE) {
            position = 0;
            currentChunkNumber = null;
        } else {
            for (int i = position; i < FileSystemImpl.CHUNK_SIZE; i++) {
                chunkData[i] = 0;
            }
        }
        randomAccessFile.write(currentChunkNumberSaved * FileSystemImpl.CHUNK_SIZE, chunkData);
    }

    @Override
    public synchronized void close() throws IOException {
        writeEncoded(ByteEncoder.EOF.getFirst());
        writeEncoded(ByteEncoder.EOF.getSecond());
        flush();
    }

    public interface ChunksAllocator {
        Integer allocateNewChunk();
    }
}
