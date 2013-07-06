package org.fs.common.concurrent;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yury Litvinov
 */
public class EqualObjectsMutexTest {

    @Test
    public void testGetMutexForEqualsObjects() throws Exception {
        EqualObjectsMutex<String> mutexes = new EqualObjectsMutex<String>();

        String s1 = getString("1");
        String s2 = getString("1");

        assertThat(s1 == s2).isFalse(); // lets make sure we have different objects
        assertThat(s1.equals(s2)).isTrue(); // but equal

        assertThat(mutexes.getMutex(s1) == mutexes.getMutex(s2)).isTrue();
    }

    @Test
    public void testGetMutexForNonEqualsObjects() throws Exception {
        EqualObjectsMutex<String> mutexes = new EqualObjectsMutex<String>();

        String s1 = getString("1");
        String s2 = getString("2");

        assertThat(s1 == s2).isFalse();
        assertThat(s1.equals(s2)).isFalse();

        assertThat(mutexes.getMutex(s1) == mutexes.getMutex(s2)).isFalse();
    }

    @Test
    public void testGetMutexForSameObjects() throws Exception {
        EqualObjectsMutex<String> mutexes = new EqualObjectsMutex<String>();

        String s1 = "a";
        String s2 = "a";

        assertThat(s1 == s2).isTrue();
        assertThat(s1.equals(s2)).isTrue();

        assertThat(mutexes.getMutex(s1) == mutexes.getMutex(s2)).isTrue();
    }

    @Test
    public void testGetMutexForNonSameObjects() throws Exception {
        EqualObjectsMutex<String> mutexes = new EqualObjectsMutex<String>();

        String s1 = "a";
        String s2 = "b";

        assertThat(s1 == s2).isFalse();
        assertThat(s1.equals(s2)).isFalse();

        assertThat(mutexes.getMutex(s1) == mutexes.getMutex(s2)).isFalse();
    }


    public String getString(String s) {
        return "a" + s;
    }
}
