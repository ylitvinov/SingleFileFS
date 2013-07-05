package org.fs.impl.streams.screening;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Yury Litvinov
 */
public class ScreeningOutputStream extends OutputStream {

    public static final int HALF_BYTE = 127;

    private final OutputStream outputStream;
    private final int screeningByte;
    private final int systemByte;

    private int systemBytesCount;
    private int screenedBytesCount;

    public ScreeningOutputStream(OutputStream outputStream, int screeningByte) {
        this.outputStream = outputStream;

        this.screeningByte = screeningByte;
        this.systemByte = getSystemByte(screeningByte); // "next" byte
    }

    @Override
    public synchronized void write(int b) throws IOException {
        flushIfRequired(b);
        if (b == systemByte) {
            systemBytesCount++;
        } else if (b == screeningByte) {
            screenedBytesCount++;
        } else {
            outputStream.write(b);
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        forceFlush();
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        forceFlush();
        outputStream.close();
    }

    private void flushIfRequired(int nextByte) throws IOException {
        if (systemBytesCount > 0 && nextByte != systemByte || systemBytesCount == HALF_BYTE
                || screenedBytesCount > 0 && nextByte != screeningByte || screenedBytesCount == HALF_BYTE) {
            forceFlush();
        }
    }

    private void forceFlush() throws IOException {
        if (screenedBytesCount > 0) {
            outputStream.write(systemByte);
            outputStream.write(screenedBytesCount);
            screenedBytesCount = 0;
        }
        if (systemBytesCount > 0) {
            outputStream.write(systemByte);
            outputStream.write(systemBytesCount + HALF_BYTE);
            systemBytesCount = 0;
        }
    }

    public static int getSystemByte(int screenedByte) {
        return (screenedByte + 1) % 255;
    }
}
