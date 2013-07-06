package org.fs.common;

import java.util.HashMap;

/**
 * @author Yury Litvinov
 */
@ThreadSafe
public class CounterMap<K> {

    private final HashMap<K, Integer> map = new HashMap<K, Integer>();

    public synchronized void increase(K key) {
        Integer counter = map.get(key);
        if (counter != null) {
            map.put(key, counter + 1);
        } else {
            map.put(key, 1);
        }
    }

    public synchronized void decrease(K key) {
        Integer counter = map.get(key);
        if (counter != null) {
            if (counter == 1) {
                map.remove(key);
            } else {
                map.put(key, counter - 1);
            }
        }
    }

    public Integer getCount(K key) {
        Integer counter = map.get(key);
        if (counter != null) {
            return counter;
        } else {
            return 0;
        }
    }
}
