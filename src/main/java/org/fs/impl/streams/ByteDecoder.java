package org.fs.impl.streams;

import org.fs.common.NotThreadSafe;

/**
 * @author Yury Litvinov
 */
@NotThreadSafe
public class ByteDecoder {
    private byte firstByte;
    private byte secondByte;

    public void readFirstByte(byte b) {
        firstByte = b;
    }

    public boolean requiresSecondByte() {
        return firstByte == ByteEncoder.EOF.getFirst();
    }

    public void readSecondByte(byte b) {
        secondByte = b;
    }

    public byte getValue() {
        if (requiresSecondByte()) {
            return secondByte;
        } else {
            return firstByte;
        }
    }

    public boolean isEOF() {
        return ByteEncoder.EOF.getFirst() == firstByte && ByteEncoder.EOF.getSecond() == secondByte;
    }
}
