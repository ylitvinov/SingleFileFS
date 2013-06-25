package org.fs.impl.streams;

import org.fs.common.NotThreadSafe;
import org.fs.common.Pair;

/**
 * @author Yury Litvinov
 */
@NotThreadSafe
public class ByteEncoder {
    public final static Pair<Byte, Byte> EOF = new Pair<Byte, Byte>((byte) 0, (byte) 1);

    private byte firstByte;

    public void encode(byte value) {
        firstByte = value;
    }

    public byte firstByte() {
        return firstByte;
    }

    public boolean requiresSecondByte() {
        return firstByte == 0;
    }

    public byte secondByte() {
        return 0;
    }

}
