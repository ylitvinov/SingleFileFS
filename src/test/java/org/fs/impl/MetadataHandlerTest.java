package org.fs.impl;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.io.*;

/**
 * @author Yury Litvinov
 */
public class MetadataHandlerTest {

    @Test
    public void testAllocateReleaseGet() {
        MetadataHandler metadataHandler = new MetadataHandler();

        Assertions.assertThat(metadataHandler.getChunksForFile(1)).isEmpty();

        metadataHandler.allocateNewChunkForFile(1);
        metadataHandler.allocateNewChunkForFile(2);
        metadataHandler.allocateNewChunkForFile(1);
        Assertions.assertThat(metadataHandler.getChunksForFile(1)).containsExactly(1, 3);

        metadataHandler.releaseChunksForFile(1);
        Assertions.assertThat(metadataHandler.getChunksForFile(1)).isEmpty();
        Assertions.assertThat(metadataHandler.getChunksForFile(2)).containsExactly(2);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        MetadataHandler metadataHandler = new MetadataHandler();
        metadataHandler.allocateNewChunkForFile(1);
        metadataHandler.allocateNewChunkForFile(2);
        metadataHandler.allocateNewChunkForFile(2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(metadataHandler);
        objectOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        metadataHandler = (MetadataHandler) objectInputStream.readObject();

        Assertions.assertThat(metadataHandler.getChunksForFile(1)).containsExactly(1);
        Assertions.assertThat(metadataHandler.getChunksForFile(2)).containsExactly(2, 3);
    }
}
