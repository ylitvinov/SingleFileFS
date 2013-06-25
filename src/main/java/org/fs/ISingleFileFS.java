package org.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yury Litvinov
 */
public interface ISingleFileFS {

    OutputStream writeFile(String fileName) throws IOException;

    InputStream readFile(String fileName) throws IOException;

    void deleteFile(String fileName) throws IOException;

    void flushMetadata() throws IOException;
}
