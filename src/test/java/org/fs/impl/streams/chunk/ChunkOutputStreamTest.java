package org.fs.impl.streams.chunk;

import org.fest.assertions.Assertions;
import org.fs.impl.FileSystemImpl;
import org.fs.impl.streams.ChunkAllocatorMock;
import org.fs.impl.streams.RandomAccessFileMock;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Yury Litvinov
 */
public class ChunkOutputStreamTest {

    @Test
    public void testSingleChunk() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE);
        ChunkOutputStream outputStream = new ChunkOutputStream(randomAccessFileMock, new ChunkAllocatorMock());
        outputStream.write(1);
        outputStream.flush();
        outputStream.write(2);
        outputStream.close();

        Assertions.assertThat(randomAccessFileMock.buffer[0]).isEqualTo((byte) 1);
        Assertions.assertThat(randomAccessFileMock.buffer[1]).isEqualTo((byte) 2);
        Assertions.assertThat(randomAccessFileMock.buffer[2]).isEqualTo((byte) ChunkOutputStream.EOF);
    }

    @Test
    public void testManyChunks() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE * 2);
        ChunkOutputStream outputStream = new ChunkOutputStream(randomAccessFileMock, new ChunkAllocatorMock());
        for (int i = 0; i < FileSystemImpl.CHUNK_SIZE / 2; i++) {
            outputStream.write(1);
        }
        outputStream.flush();
        for (int i = 0; i < FileSystemImpl.CHUNK_SIZE / 2; i++) {
            outputStream.write(2);
        }
        for (int i = 0; i < FileSystemImpl.CHUNK_SIZE / 2; i++) {
            outputStream.write(3);
        }
        outputStream.close();

        Assertions.assertThat(randomAccessFileMock.buffer[0]).isEqualTo((byte) 1);
        Assertions.assertThat(randomAccessFileMock.buffer[FileSystemImpl.CHUNK_SIZE / 2]).isEqualTo((byte) 2);
        Assertions.assertThat(randomAccessFileMock.buffer[FileSystemImpl.CHUNK_SIZE - 1]).isEqualTo((byte) 2);
        Assertions.assertThat(randomAccessFileMock.buffer[FileSystemImpl.CHUNK_SIZE]).isEqualTo((byte) 3);
        Assertions.assertThat(randomAccessFileMock.buffer[FileSystemImpl.CHUNK_SIZE + FileSystemImpl.CHUNK_SIZE / 2 - 1]).isEqualTo((byte) 3);
        Assertions.assertThat(randomAccessFileMock.buffer[FileSystemImpl.CHUNK_SIZE + FileSystemImpl.CHUNK_SIZE / 2]).isEqualTo((byte) ChunkOutputStream.EOF);
    }

}
