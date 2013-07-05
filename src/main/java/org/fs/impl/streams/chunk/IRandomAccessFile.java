package org.fs.impl.streams.chunk;

import java.io.IOException;

/**
 * @author Yury Litvinov
 */
public interface IRandomAccessFile {

    void write(int offset, byte[] data) throws IOException;

    void read(int offset, byte[] data) throws IOException;
}
