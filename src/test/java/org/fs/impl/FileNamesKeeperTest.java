package org.fs.impl;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Yury Litvinov
 */
public class FileNamesKeeperTest {

    @Test
    public void testGetAddRemove() {
        FileNamesKeeper fileNamesKeeper = new FileNamesKeeper();
        fileNamesKeeper.add("file1");
        fileNamesKeeper.add("file2");

        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        fileNamesKeeper.add("file1");
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);

        fileNamesKeeper.remove("file1");
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isNull();
    }

    @Test
    public void testWriteRead() throws IOException {
        FileNamesKeeper fileNamesKeeper = new FileNamesKeeper();
        fileNamesKeeper.add("file1");
        fileNamesKeeper.add("file2");
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        Assertions.assertThat(fileNamesKeeper.getFileId("file2")).isEqualTo(2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        fileNamesKeeper.write(byteArrayOutputStream);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        fileNamesKeeper = new FileNamesKeeper(byteArrayInputStream);
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        Assertions.assertThat(fileNamesKeeper.getFileId("file2")).isEqualTo(2);
    }
}
