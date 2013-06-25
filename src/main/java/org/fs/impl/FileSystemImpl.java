package org.fs.impl;

import org.fs.ISingleFileFS;
import org.fs.common.CounterMap;
import org.fs.common.ThreadSafe;
import org.fs.impl.streams.ChunkInputStream;
import org.fs.impl.streams.ChunkOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class FileSystemImpl implements ISingleFileFS {

    public static final int CHUNK_SIZE = 1 << 12; //4k size of the each chunk

    private final RandomAccessFileWrapper randomAccessFile;
    private final MetadataHandler chunksMetadataHandler;
    private final FileNamesKeeper fileNamesKeeper;

    private final CounterMap<Integer> filesReads = new CounterMap<Integer>();
    private final CounterMap<Integer> filesWrites = new CounterMap<Integer>();
    private final Object metadataLock = new Object();

    public FileSystemImpl(File file) throws IOException {
        this.randomAccessFile = new RandomAccessFileWrapper(file);
        if (randomAccessFile.isNewFile()) {
            chunksMetadataHandler = new MetadataHandler();
            fileNamesKeeper = new FileNamesKeeper();
        } else {
            // reading chunks metadata
            ChunkInputStream metadataStream = createMetadataInputStream();
            chunksMetadataHandler = new MetadataHandler(metadataStream);

            // reading file names
            ChunkInputStream filesNamesInputStream = createInputStream(FileNamesKeeper.RESERVED_ID_FOR_FILE_NAMES);
            fileNamesKeeper = new FileNamesKeeper(filesNamesInputStream);
        }
    }

    @Override
    public synchronized InputStream readFile(String fileName) throws IOException {
        Integer fileId = fileNamesKeeper.getFileId(fileName);
        if (fileId == null) {
            throw new IOException("File with name '" + fileName + "' is not found");
        }
        filesReads.increase(fileId);
        return createInputStream(fileId);
    }

    @Override
    public synchronized OutputStream writeFile(String fileName) throws IOException {
        if (fileNamesKeeper.getFileId(fileName) != null) {
            throw new IOException("File '" + fileName + "' already exists");
        }
        final Integer fileId = fileNamesKeeper.add(fileName);
        filesWrites.increase(fileId);
        return createOutputStream(fileId);
    }

    @Override
    public synchronized void deleteFile(String fileName) throws IOException {
        Integer fileId = fileNamesKeeper.getFileId(fileName);
        if (fileId == null) {
            throw new IOException("File with name '" + fileName + "' does not exist in file system");
        }
        if (filesReads.getCount(fileId) > 0) {
            throw new IOException("File is opened for reading");
        }
        if (filesWrites.getCount(fileId) > 0) {
            throw new IOException("File is opened for writing");
        }

        // make sure that we don't flush at the moment
        synchronized (metadataLock) {
            fileNamesKeeper.remove(fileName);
            chunksMetadataHandler.releaseChunks(fileId);
        }
    }

    @Override
    public void flushMetadata() throws IOException {
        // we don't want any changes being made to metadata during flushing so we make write access to
        // chunksMetadataHandler and fileNamesKeeper being always synchronized
        synchronized (metadataLock) {
            // removing chunks for FileNames structure
            chunksMetadataHandler.releaseChunks(FileNamesKeeper.RESERVED_ID_FOR_FILE_NAMES);
            // write FileNames structure
            fileNamesKeeper.write(createOutputStream(FileNamesKeeper.RESERVED_ID_FOR_FILE_NAMES));
            // write metadata to the first chunk
            chunksMetadataHandler.write(createMetadataOutputStream());
        }
    }

    private ChunkOutputStream createOutputStream(final Integer fileId) {
        ChunkOutputStream.ChunksAllocator chunksAllocator = new ChunkOutputStream.ChunksAllocator() {
            @Override
            public Integer allocateNewChunk() {
                synchronized (metadataLock) {
                    int chunkNumber = chunksMetadataHandler.allocateNewChunkForFile(fileId);
                    randomAccessFile.resizeFileToFitChunk(chunkNumber);
                    return chunkNumber;
                }
            }
        };
        return new ChunkOutputStream(randomAccessFile, chunksAllocator) {
            @Override
            public synchronized void close() throws IOException {
                synchronized (FileSystemImpl.this) {
                    filesWrites.decrease(fileId);
                }
                super.close();
            }
        };
    }

    private ChunkInputStream createInputStream(final Integer fileId) {
        List<Integer> chunkNumbers = chunksMetadataHandler.getFileNameChunks(fileId);
        return new ChunkInputStream(randomAccessFile, chunkNumbers) {
            boolean closed = false;

            @Override
            public synchronized int read() throws IOException {
                int value = super.read();
                if (value == -1) {
                    close();
                }
                return value;
            }

            @Override
            public void close() throws IOException {
                synchronized (FileSystemImpl.this) {
                    if (!closed) {
                        filesReads.decrease(fileId);
                    }
                    closed = true;
                }
                super.close();
            }
        };
    }

    private ChunkInputStream createMetadataInputStream() {
        return new ChunkInputStream(randomAccessFile, Arrays.asList(MetadataHandler.METADATA_CHUNK_NUMBER));
    }

    private ChunkOutputStream createMetadataOutputStream() {
        return new ChunkOutputStream(randomAccessFile, new ChunkOutputStream.ChunksAllocator() {
            boolean invoked;

            @Override
            public Integer allocateNewChunk() {
                if (invoked) {
                    throw new IllegalStateException("Metadata does not fit into single chunk. This should never happen since we should prevent too many files creation from API");
                }
                invoked = true;
                return MetadataHandler.METADATA_CHUNK_NUMBER;
            }
        });
    }

}
