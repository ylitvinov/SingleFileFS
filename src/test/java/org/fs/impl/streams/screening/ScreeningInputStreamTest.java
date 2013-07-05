package org.fs.impl.streams.screening;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class ScreeningInputStreamTest {

    @Test
    public void testStream() throws Exception {
        byte[] buffer = {1, 2, 2, 3, 1, -127};
        ScreeningInputStream stream = new ScreeningInputStream(new ByteArrayInputStream(buffer), 0);

        assertThat(stream.read()).isEqualTo(0);
        assertThat(stream.read()).isEqualTo(0);
        assertThat(stream.read()).isEqualTo(2);
        assertThat(stream.read()).isEqualTo(3);
        assertThat(stream.read()).isEqualTo(1);
        assertThat(stream.read()).isEqualTo(1);
        assertThat(stream.read()).isEqualTo(-1);
    }
}
