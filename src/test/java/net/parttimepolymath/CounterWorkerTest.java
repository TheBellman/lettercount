package net.parttimepolymath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

public class CounterWorkerTest {

    @Test
    public void test() throws Exception {
        CounterWorker instance = new CounterWorker();
        instance.populate(
                "You can fool some of the people all of the time, and all of the people some of the time, but you can not fool all of the people all of the time.");

        Map<Character, MutableInt> result = instance.call();
        assertNotNull(result);
        assertEquals(17, result.size());

        assertEquals(7, result.get('a').intValue());
        assertEquals(1, result.get('b').intValue());
        assertEquals(2, result.get('c').intValue());
        assertEquals(1, result.get('d').intValue());
        assertEquals(17, result.get('e').intValue());
        assertEquals(8, result.get('f').intValue());
        assertEquals(6, result.get('h').intValue());
        assertEquals(3, result.get('i').intValue());
        assertEquals(13, result.get('l').intValue());
        assertEquals(5, result.get('m').intValue());
        assertEquals(4, result.get('n').intValue());
        assertEquals(18, result.get('o').intValue());
        assertEquals(6, result.get('p').intValue());
        assertEquals(2, result.get('s').intValue());
        assertEquals(11, result.get('t').intValue());
        assertEquals(3, result.get('u').intValue());
        assertEquals(2, result.get('y').intValue());
    }
}
