package org.fs.common;

import org.fs.common.concurrent.EqualObjectsMutex;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class CounterMap<K> {

    private final ConcurrentHashMap<K, Integer> map = new ConcurrentHashMap<K, Integer>();
    private final EqualObjectsMutex<K> mutexes = new EqualObjectsMutex<K>();

    public void increase(K key) {
        synchronized (mutexes.getMutex(key)) {
            Integer counter = map.get(key);
            if (counter != null) {
                map.put(key, counter + 1);
            } else {
                map.put(key, 1);
            }
        }
    }

    public void decrease(K key) {
        synchronized (mutexes.getMutex(key)) {
            Integer counter = map.get(key);
            if (counter != null) {
                if (counter == 1) {
                    map.remove(key);
                } else {
                    map.put(key, counter - 1);
                }
            }
        }
    }

    public Integer getCount(K key) {
        synchronized (mutexes.getMutex(key)) {
            Integer counter = map.get(key);
            if (counter != null) {
                return counter;
            } else {
                return 0;
            }
        }
    }
}
