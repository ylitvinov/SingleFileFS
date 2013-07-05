package org.fs.impl.streams;

import org.fs.impl.streams.chunk.IRandomAccessFile;

import java.io.IOException;

/**
 * @author Yury Litvinov
 */
public class RandomAccessFileMock implements IRandomAccessFile {
    public final byte[] buffer;

    public RandomAccessFileMock(byte[] buffer) {
        this.buffer = buffer;
    }

    public RandomAccessFileMock(int size) {
        this.buffer = new byte[size];
    }

    @Override
    public void write(int offset, byte[] data) throws IOException {
        System.arraycopy(data, 0, buffer, offset, data.length);
    }

    @Override
    public void read(int offset, byte[] data) throws IOException {
        System.arraycopy(buffer, offset, data, 0, data.length);
    }
}
