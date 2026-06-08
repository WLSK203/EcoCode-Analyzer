// HashMap Lookup - O(1) average complexity
import java.util.HashMap;
import java.util.Map;
public class HashMapLookup {
    private Map<String, Integer> cache = new HashMap<>();
    public void put(String key, int value) {
        cache.put(key, value);
    }
    public int get(String key) {
        return cache.getOrDefault(key, -1);
    }
    public boolean contains(String key) {
        return cache.containsKey(key);
    }
    public static void main(String[] args) {
        HashMapLookup lookup = new HashMapLookup();
        lookup.put("apple", 1);
        lookup.put("banana", 2);
        System.out.println(lookup.get("apple"));
        System.out.println(lookup.contains("cherry"));
    }
}
