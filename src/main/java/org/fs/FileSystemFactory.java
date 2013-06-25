package org.fs;

import org.fs.impl.FileSystemImpl;

import java.io.File;
import java.io.IOException;

/**
 * @author Yury Litvinov
 */
public class FileSystemFactory {

    public static ISingleFileFS create(File file) throws IOException {
        return new FileSystemImpl(file);
    }

    public static ISingleFileFS create(String name) throws IOException {
        return new FileSystemImpl(new File(name));
    }

}
