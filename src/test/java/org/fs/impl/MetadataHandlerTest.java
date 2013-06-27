package org.fs.impl;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public void testReadWrite() throws IOException {
        MetadataHandler metadataHandler = new MetadataHandler();
        metadataHandler.allocateNewChunkForFile(1);
        metadataHandler.allocateNewChunkForFile(2);
        metadataHandler.allocateNewChunkForFile(2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        metadataHandler.write(byteArrayOutputStream);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        metadataHandler = new MetadataHandler(byteArrayInputStream);
        Assertions.assertThat(metadataHandler.getChunksForFile(1)).containsExactly(1);
        Assertions.assertThat(metadataHandler.getChunksForFile(2)).containsExactly(2, 3);
    }
}
