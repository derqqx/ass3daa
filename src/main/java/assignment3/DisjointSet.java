package assignment3;

import java.util.*;

public class DisjointSet {
    private final Map<String, String> parent;
    private final Map<String, Integer> rank;
    public long operationsCount;

    public DisjointSet(Collection<String> elements) {
        parent = new HashMap<>();
        rank = new HashMap<>();
        operationsCount = 0;
        for (String element : elements) {
            parent.put(element, element);
            rank.put(element, 0);
        }
    }

    public String find(String i) {
        operationsCount++;
        if (!parent.get(i).equals(i)) {
            parent.put(i, find(parent.get(i)));
        }
        return parent.get(i);
    }

    public boolean union(String i, String j) {
        String rootI = find(i);
        String rootJ = find(j);
        operationsCount++;

        if (rootI.equals(rootJ)) {
            return false;
        }

        if (rank.get(rootI) < rank.get(rootJ)) {
            parent.put(rootI, rootJ);
        } else if (rank.get(rootI) > rank.get(rootJ)) {
            parent.put(rootJ, rootI);
        } else {
            parent.put(rootJ, rootI);
            rank.put(rootI, rank.get(rootI) + 1);
        }
        operationsCount += 2;
        return true;
    }
}