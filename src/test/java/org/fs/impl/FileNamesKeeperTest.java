package org.fs.impl;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.io.*;

/**
 * @author Yury Litvinov
 */
public class FileNamesKeeperTest {

    @Test
    public void testGetAddRemoveHas() {
        FileNamesKeeper fileNamesKeeper = new FileNamesKeeper();
        fileNamesKeeper.add("file1");
        fileNamesKeeper.add("file2");

        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        fileNamesKeeper.add("file1");
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);

        fileNamesKeeper.remove("file1");
        Assertions.assertThat(fileNamesKeeper.hasFile("file1")).isFalse();
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        FileNamesKeeper fileNamesKeeper = new FileNamesKeeper();
        fileNamesKeeper.add("file1");
        fileNamesKeeper.add("file2");
        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        Assertions.assertThat(fileNamesKeeper.getFileId("file2")).isEqualTo(2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(fileNamesKeeper);
        objectOutputStream.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        fileNamesKeeper = (FileNamesKeeper) objectInputStream.readObject();

        Assertions.assertThat(fileNamesKeeper.getFileId("file1")).isEqualTo(1);
        Assertions.assertThat(fileNamesKeeper.getFileId("file2")).isEqualTo(2);
    }
}
