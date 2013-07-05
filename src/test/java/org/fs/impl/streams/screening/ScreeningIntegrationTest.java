package org.fs.impl.streams.screening;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class ScreeningIntegrationTest {

    @Test
    public void test() throws Exception {
        int[] data = {0, 0, 1, 1, 2, 2, 0, 250, 252, 253, 1, 0, 1};
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ScreeningOutputStream outStream = new ScreeningOutputStream(arrayOutputStream, 0);

        for (int val : data) {
            outStream.write(val);
        }
        outStream.close();

        byte[] bytes = arrayOutputStream.toByteArray();
        ScreeningInputStream inStream = new ScreeningInputStream(new ByteArrayInputStream(bytes), 0);

        for (int i = 0; i < data.length; i++) {
            assertThat(inStream.read()).isEqualTo(data[i]);
        }
    }
}
