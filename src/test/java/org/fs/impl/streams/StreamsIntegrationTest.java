package org.fs.impl.streams;

import org.fs.impl.FileSystemImpl;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class StreamsIntegrationTest {

    @Test
    public void testSingleChunk() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE);
        ChunkAllocatorMock chunksAllocator = new ChunkAllocatorMock();

        ChunkOutputStream outputStream = new ChunkOutputStream(randomAccessFileMock, chunksAllocator);
        outputStream.write(0);
        outputStream.flush();
        outputStream.write(1);
        outputStream.close();

        ChunkInputStream inputStream = new ChunkInputStream(randomAccessFileMock, chunksAllocator.getAllocatedChunks());
        assertThat(inputStream.read()).isEqualTo(0);
        assertThat(inputStream.read()).isEqualTo(1);
        assertThat(inputStream.read()).isEqualTo(-1);
    }

    @Test
    public void testManyChunks() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE * 2);
        ChunkAllocatorMock chunksAllocator = new ChunkAllocatorMock();

        ChunkOutputStream outputStream = new ChunkOutputStream(randomAccessFileMock, chunksAllocator);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        int max = FileSystemImpl.CHUNK_SIZE / 5;
        for (int i = 0; i < max; i++) {
            dataOutputStream.writeInt(i);
        }
        dataOutputStream.close();

        ChunkInputStream inputStream = new ChunkInputStream(randomAccessFileMock, chunksAllocator.getAllocatedChunks());
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i = 0; i < max; i++) {
            assertThat(dataInputStream.readInt()).isEqualTo(i);
        }
        assertThat(inputStream.read()).isEqualTo(-1);
    }
}