package org.fs.impl.streams.chunk;

import org.fest.assertions.Assertions;
import org.fs.impl.FileSystemImpl;
import org.fs.impl.streams.RandomAccessFileMock;
import org.fs.impl.streams.chunk.ChunkInputStream;
import org.fs.impl.streams.chunk.ChunkOutputStream;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Yury Litvinov
 */
public class ChunkInputStreamTest {

    @Test
    public void testSingleChunk() throws IOException {
        byte[] bytes = new byte[FileSystemImpl.CHUNK_SIZE];
        bytes[0] = 1;
        bytes[1] = 2;
        bytes[2] = ChunkOutputStream.EOF;
        ChunkInputStream inputStream = new ChunkInputStream(new RandomAccessFileMock(bytes), Arrays.asList(0));
        Assertions.assertThat(inputStream.read()).isEqualTo(1);
        Assertions.assertThat(inputStream.read()).isEqualTo(2);
        Assertions.assertThat(inputStream.read()).isEqualTo(-1);
    }

    @Test
    public void testManyChunks() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE * 2);
        ChunkInputStream outputStream = new ChunkInputStream(randomAccessFileMock, Arrays.asList(0, 1));
        int i = 0;
        for (int j = 0; j < FileSystemImpl.CHUNK_SIZE / 2; j++) {
            randomAccessFileMock.buffer[i++] = 1;
        }
        for (int j = 0; j < FileSystemImpl.CHUNK_SIZE / 2; j++) {
            randomAccessFileMock.buffer[i++] = 2;
        }
        for (int j = 0; j < FileSystemImpl.CHUNK_SIZE / 2; j++) {
            randomAccessFileMock.buffer[i++] = 3;
        }
        randomAccessFileMock.buffer[i] = ChunkOutputStream.EOF;

        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 1);
        outputStream.skip(FileSystemImpl.CHUNK_SIZE / 2 - 2);
        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 1);

        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 2);
        outputStream.skip(FileSystemImpl.CHUNK_SIZE / 2 - 2);
        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 2);

        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 3);
        outputStream.skip(FileSystemImpl.CHUNK_SIZE / 2 - 2);
        Assertions.assertThat(outputStream.read()).isEqualTo((byte) 3);

        Assertions.assertThat(outputStream.read()).isEqualTo(-1);
        Assertions.assertThat(outputStream.read()).isEqualTo(-1);
    }

}
