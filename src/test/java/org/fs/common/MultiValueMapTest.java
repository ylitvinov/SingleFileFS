package org.fs.common;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class MultiValueMapTest {

    @Test
    public void testNormalUsaCase() {
        MultiValueMap<String, Integer> valueMap = new MultiValueMap<String, Integer>();

        assertThat(valueMap.getSafe("a")).isEmpty();

        valueMap.put("a", 1);
        assertThat(valueMap.getSafe("a")).containsExactly(1);

        valueMap.put("a", 1);
        assertThat(valueMap.getSafe("a")).containsExactly(1, 1);

        valueMap.put("b", 1);
        assertThat(valueMap.getSafe("a")).containsExactly(1, 1);

        valueMap.remove("a");
        assertThat(valueMap.getSafe("a")).isEmpty();
    }
}
