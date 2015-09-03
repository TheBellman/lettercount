package net.parttimepolymath;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import net.jcip.annotations.NotThreadSafe;

/**
 * worker class for each thread which examines the contents of it's own buffer. This is most definitely not
 * thread safe, as the internal buffer is exposed, which means that conflicting external threads could
 * independently modify the contents of the buffer.
 * 
 * @author robert
 */
@NotThreadSafe
public final class CounterWorker implements Callable<Map<Character, MutableInt>> {
    /**
     * segment size to read.
     */
    public static final int BUFSIZ = 4096;
    /**
     * the characters to count.
     */
    private final char[] buffer = new char[CounterWorker.BUFSIZ];

    /**
     * default constructor.
     */
    public CounterWorker() {
    }

    /**
     * accessor, get a reference to the buffer for this instance.
     * 
     * @return the buffer.
     */
    public char[] getBuffer() {
        return buffer;
    }

    /**
     * helper method which fills the internal buffer with the contents of a supplied string. this is mainly intended to support testing.
     * 
     * @param body the string to populate from, assumed non-null. The internal buffer is completely flushed, and only the first BUFSIZ
     *            characters are copied in.
     */
    public void populate(final String body) {
        Arrays.fill(buffer, (char) 0);
        char[] input = body.toCharArray();
        for (int i = 0; i < BUFSIZ && i < input.length; i++) {
            buffer[i] = input[i];
        }
    }

    /**
     * count the ascii characters in the provided input. Note that this is not particularly efficient, we're doing quite a bit of work in
     * here that should be optimized.
     */
    @Override
    public Map<Character, MutableInt> call() throws Exception {
        Map<Character, MutableInt> freq = new TreeMap<>();
        for (char c : buffer) {
            if (CharUtils.isAsciiAlpha(c)) {
                char key = Character.toLowerCase(c);
                MutableInt count = freq.get(key);
                if (count == null) {
                    freq.put(key, new MutableInt(1));
                } else {
                    count.increment();
                }
            }
        }
        return freq;
    }

}
