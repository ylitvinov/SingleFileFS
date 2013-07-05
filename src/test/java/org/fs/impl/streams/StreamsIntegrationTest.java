package org.fs.impl.streams;

import org.fs.impl.FileSystemImpl;
import org.fs.impl.streams.chunk.ChunkInputStream;
import org.fs.impl.streams.chunk.ChunkOutputStream;
import org.fs.impl.streams.screening.ScreeningInputStream;
import org.fs.impl.streams.screening.ScreeningOutputStream;
import org.junit.Test;

import java.io.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class StreamsIntegrationTest {

    @Test
    public void testSingleChunk() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE);
        ChunkAllocatorMock chunksAllocator = new ChunkAllocatorMock();

        ScreeningOutputStream screeningOutputStream = new ScreeningOutputStream(new ChunkOutputStream(randomAccessFileMock, chunksAllocator), 0);
        DataOutputStream outputStream = new DataOutputStream(screeningOutputStream);
        outputStream.writeInt(0);
        outputStream.writeInt(1);
        outputStream.writeInt(Integer.MAX_VALUE);
        outputStream.writeInt(-1);
        outputStream.writeInt(Integer.MIN_VALUE);
        outputStream.close();

        ScreeningInputStream screeningInputStream = new ScreeningInputStream(new ChunkInputStream(randomAccessFileMock, chunksAllocator.getAllocatedChunks()), 0);
        DataInputStream inputStream = new DataInputStream(screeningInputStream);
        assertThat(inputStream.readInt()).isEqualTo(0);
        assertThat(inputStream.readInt()).isEqualTo(1);
        assertThat(inputStream.readInt()).isEqualTo(Integer.MAX_VALUE);
        assertThat(inputStream.readInt()).isEqualTo(-1);
        assertThat(inputStream.readInt()).isEqualTo(Integer.MIN_VALUE);
        assertThat(inputStream.read()).isEqualTo(-1);
    }

    @Test
    public void testManyChunks() throws IOException {
        RandomAccessFileMock randomAccessFileMock = new RandomAccessFileMock(FileSystemImpl.CHUNK_SIZE * 2);
        ChunkAllocatorMock chunksAllocator = new ChunkAllocatorMock();

        OutputStream outputStream = new ScreeningOutputStream(new ChunkOutputStream(randomAccessFileMock, chunksAllocator), 0);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        int startIdx = -5;
        int endIdx = FileSystemImpl.CHUNK_SIZE / 3;
        for (int i = startIdx; i < endIdx; i++) {
            dataOutputStream.writeInt(i);
        }
        dataOutputStream.close();

        InputStream inputStream = new ScreeningInputStream(new ChunkInputStream(randomAccessFileMock, chunksAllocator.getAllocatedChunks()), 0);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i = startIdx; i < endIdx; i++) {
            assertThat(dataInputStream.readInt()).isEqualTo(i);
        }
        assertThat(inputStream.read()).isEqualTo(-1);
    }
}