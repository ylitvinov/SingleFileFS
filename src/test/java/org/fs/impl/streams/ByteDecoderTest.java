package org.fs.impl.streams;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class ByteDecoderTest {
    private ByteDecoder decoder;

    @Before
    public void before() {
        decoder = new ByteDecoder();
    }

    @Test
    public void testZero() {
        decoder.readFirstByte((byte) 0);
        assertThat(decoder.requiresSecondByte()).isTrue();
        decoder.readSecondByte((byte) 0);
        assertThat(decoder.getValue()).isEqualTo((byte) 0);

    }

    @Test
    public void testNormal() {
        decoder.readFirstByte((byte) 1);
        assertThat(decoder.requiresSecondByte()).isFalse();
        assertThat(decoder.getValue()).isEqualTo((byte) 1);
    }
}
