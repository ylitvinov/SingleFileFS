package org.fs.common;

import java.util.*;

/**
 * @author Yury Litvinov
 */
@NotThreadSafe
public class MultiValueMap<K, V> {

    private final Map<K, List<V>> map = new HashMap<K, List<V>>();

    /**
     * @return true if this set did not already contain the specified element
     */
    public boolean put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }
        return list.add(value);
    }

    public List<V> getSafe(K key) {
        List<V> set = map.get(key);
        if (set == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(set);
    }

    public Set<K> getKeys() {
        return map.keySet();
    }

    public void remove(K key) {
        map.remove(key);
    }
}
