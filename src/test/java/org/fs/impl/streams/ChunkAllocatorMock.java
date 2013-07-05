package org.fs.impl.streams;

import org.fs.impl.streams.chunk.ChunkOutputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yury Litvinov
 */
public class ChunkAllocatorMock implements ChunkOutputStream.ChunksAllocator {
    private int idx = 0;

    @Override
    public int allocateNewChunk() {
        return idx++;
    }

    public List<Integer> getAllocatedChunks() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < idx; i++) {
            list.add(i);
        }
        return list;
    }
}
