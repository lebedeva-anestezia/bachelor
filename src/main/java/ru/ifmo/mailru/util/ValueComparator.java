package ru.ifmo.mailru.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Anastasia Lebedeva
 */
public class ValueComparator <K, V extends Comparable> implements Comparator<K> {
    private Map<K, V> map;

    public ValueComparator(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public int compare(K o1, K o2) {
        if (map.get(o1).compareTo(map.get(o2)) >= 0) {
            return -1;
        }
        return 1;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        ValueComparator<String, Integer> comparator = new ValueComparator<>(map);
        TreeMap<String, Integer> treeMap = new TreeMap<>(comparator);
        map.put("ololo", 1);
        map.put("pyapya", 1);
        map.put("pyapy", 3);
        treeMap.putAll(map);
        for (String s : treeMap.keySet()) {
            System.out.println(s + " : " + map.get(s));
        }
    }
}
