package org.fs.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class CounterMap<K> {

    private final ConcurrentHashMap<K, AtomicInteger> map = new ConcurrentHashMap<K, AtomicInteger>();

    public void increase(K key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        AtomicInteger counter = map.get(key);
        counter.incrementAndGet();
    }

    public void decrease(K key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        AtomicInteger counter = map.get(key);
        counter.decrementAndGet();
    }

    public int getCount(K key) {
        AtomicInteger counter = map.get(key);
        if (counter != null) {
            return counter.get();
        } else {
            return 0;
        }
    }
}
