package org.fs.common;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class CounterMapTest {

    @Test
    public void testNormalUseCase() {
        CounterMap<String> counter = new CounterMap<String>();

        assertThat(counter.getCount("a")).isEqualTo(0);

        counter.increase("a");
        counter.increase("a");
        assertThat(counter.getCount("a")).isEqualTo(2);

        counter.increase("b");
        assertThat(counter.getCount("a")).isEqualTo(2);

        counter.decrease("a");
        counter.decrease("a");
        assertThat(counter.getCount("a")).isEqualTo(0);
    }
}
