package org.fs.common.concurrent;

import org.fs.common.ThreadSafe;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is intended to use when you need to synchronize access to multiple objects which are not same
 * (in terms of '==' operation) but equal (in terms of #equals() operation).
 * <p/>
 * For example you need a per-file synchronize access to files in your file system but clients don't have
 * "same" objects they can synchronize on, but rather have only equal objects.
 * <p/>
 * If you use mutexes returned from this class, you can use usual synchronized{} block.
 * <p/>
 * Usage example:
 * <blockquote><pre>
 * EqualObjectsMutex<String> mutexes = new EqualObjectsMutex<String>();
 * ...
 * synchronized (mutexes.get("abc")){
 *    ...
 * }
 * </pre></blockquote>
 * Note, that you don't need to do any release or unlock operations, mutexes which have been requested previously
 * and not used anymore will be deleted during one of the GC runs.
 *
 * @author Yury Litvinov
 */
@ThreadSafe
public class EqualObjectsMutex<T> {

    private final WeakHashMap<T, WeakReference<T>> mutexes = new WeakHashMap<T, WeakReference<T>>();

    // Since WeakHashMap is not thread-safe, we have to restrict write access to it.
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Object getMutex(T key) {
        Object monitor = getMonitor(key);
        if (monitor == null) {
            return setMonitor(key);
        }
        return monitor;
    }

    private Object getMonitor(T key) {
        lock.readLock().lock();
        try {
            WeakReference reference = mutexes.get(key);
            return reference == null ? null : reference.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    private Object setMonitor(T key) {
        lock.writeLock().lock();
        try {
            Object monitor = getMonitor(key);
            if (monitor == null) {
                monitor = key;
                mutexes.put(key, new WeakReference<T>(key));
            }
            return monitor;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
