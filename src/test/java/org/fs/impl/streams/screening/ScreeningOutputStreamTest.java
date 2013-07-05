package org.fs.impl.streams.screening;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class ScreeningOutputStreamTest {
    @Test
    public void testStream() throws Exception {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ScreeningOutputStream stream = new ScreeningOutputStream(arrayOutputStream, 0);
        stream.write(0);
        stream.write(0);
        stream.write(2);
        stream.write(3);
        stream.write(1);
        stream.write(1);
        stream.close();

        byte[] bytes = arrayOutputStream.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 1);
        assertThat(bytes[1]).isEqualTo((byte) 2);
        assertThat(bytes[2]).isEqualTo((byte) 2);
        assertThat(bytes[3]).isEqualTo((byte) 3);
        assertThat(bytes[4]).isEqualTo((byte) 1);
        assertThat(bytes[5]).isEqualTo((byte) -127);
    }
}
