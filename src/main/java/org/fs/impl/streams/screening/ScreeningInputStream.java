package org.fs.impl.streams.screening;

import java.io.IOException;
import java.io.InputStream;

import static org.fs.impl.streams.screening.ScreeningOutputStream.getSystemByte;

/**
 * @author Yury Litvinov
 */
public class ScreeningInputStream extends InputStream {

    private final InputStream inputStream;
    private final int screenedByte;
    private final int systemByte;

    private int systemBytesCount;
    private int screenedBytesCount;

    public ScreeningInputStream(InputStream inputStream, int screenedByte) {
        this.inputStream = inputStream;
        this.screenedByte = screenedByte;
        this.systemByte = getSystemByte(screenedByte); // "next" byte
    }

    @Override
    public int read() throws IOException {
        if (screenedBytesCount > 0) {
            screenedBytesCount--;
            return screenedByte;
        }
        if (systemBytesCount > 0) {
            systemBytesCount--;
            return systemByte;
        }
        int b = inputStream.read();
        if (b == systemByte) {
            int count = inputStream.read();
            if (count > ScreeningOutputStream.HALF_BYTE) {
                systemBytesCount = count - ScreeningOutputStream.HALF_BYTE - 1;
                return systemByte;
            } else {
                screenedBytesCount = count - 1;
                return screenedByte;
            }
        }
        return b;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
