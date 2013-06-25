package org.fs.impl.streams;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class ByteEncoderTest {
    private ByteEncoder encoder;

    @Before
    public void before() {
        encoder = new ByteEncoder();
    }

    @Test
    public void testZero() {
        encoder.encode((byte) 0);

        assertThat(encoder.firstByte()).isEqualTo((byte) 0);
        assertThat(encoder.requiresSecondByte()).isTrue();
        assertThat(encoder.secondByte()).isEqualTo((byte) 0);
    }

    @Test
    public void testNormal() {
        encoder.encode((byte) 1);

        assertThat(encoder.firstByte()).isEqualTo((byte) 1);
        assertThat(encoder.requiresSecondByte()).isFalse();
    }
}
