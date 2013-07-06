package org.fs.common.concurrent;

import org.fs.common.ThreadSafe;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

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
 * Note, that you don't need to do any release or unlock operations.
 *
 * @author Yury Litvinov
 */
@ThreadSafe
public class EqualObjectsMutex<T> {

    private final WeakHashMap<T, WeakReference<T>> mutexes = new WeakHashMap<T, WeakReference<T>>();

    public synchronized Object getMutex(T key) {
        WeakReference reference = mutexes.get(key);
        Object monitor = reference == null ? null : reference.get();
        if (monitor == null) {
            monitor = key;
            mutexes.put(key, new WeakReference<T>(key));
        }
        return monitor;
    }
}
