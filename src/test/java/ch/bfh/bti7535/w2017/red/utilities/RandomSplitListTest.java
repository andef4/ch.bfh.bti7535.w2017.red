package ch.bfh.bti7535.w2017.red.utilities;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class RandomSplitListTest {

    @Test
    public void testSize() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        Tuple<List<Integer>, List<Integer>> ret = RandomSplitList.randomSplitList(list, 0.8);
        assertEquals(80, ret.first().size());
        assertEquals(20, ret.second().size());
    }

    @Test
    public void testUniqueItems() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        Tuple<List<Integer>, List<Integer>> ret = RandomSplitList.randomSplitList(list, 0.8);
        ArrayList<Integer> first = new ArrayList<>(ret.first());
        first.retainAll(ret.second());
        assertEquals(0, first.size());

        ArrayList<Integer> second = new ArrayList<>(ret.second());
        second.retainAll(ret.first());
        assertEquals(0, second.size());
    }

    @Test
    public void testNoMissing() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        Tuple<List<Integer>, List<Integer>> ret = RandomSplitList.randomSplitList(list, 0.8);

        list.removeAll(ret.first());
        list.removeAll(ret.second());

        assertEquals(0, list.size());
    }
}
