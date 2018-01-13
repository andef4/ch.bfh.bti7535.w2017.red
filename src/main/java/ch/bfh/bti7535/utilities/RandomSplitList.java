package ch.bfh.bti7535.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomSplitList {
    /**
     * Split a list into two lists, distributed by the "split" factor
     * E.g. split == 0.8 => first list contains 80% of the items, second list 20% of all items.
     * @param list The list to split
     * @param split The split factor
     * @param <E> The type of the list
     * @return The two new lists as a Tuple
     */
    public static <E> Tuple<List<E>, List<E>> randomSplitList(List<E> list, double split) {
        List<E> list1 = new ArrayList<>();
        List<E> list2 = new ArrayList<>();

        int splitIndex = (int) (list.size() * split);

        Collections.shuffle(list, new Random(1));
        for (int i = 0; i < splitIndex; i++) {
            list1.add(list.get(i));
        }
        for (int i = splitIndex; i < list.size(); i++) {
            list2.add(list.get(i));
        }

        return new Tuple<>(list1, list2);
    }
}
