package foundation.lab.collection;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MapTest {

    public static void main(String[] args) {
        Map<String, List> shuffle = new LinkedHashMap<>();
        shuffle.computeIfAbsent("a", k -> new ArrayList());
        shuffle.get("a").add("a");
        shuffle.get("a").add("b");
        shuffle.get("a").add("c");

        shuffle.computeIfAbsent("x", k -> new ArrayList());
        shuffle.get("x").add("x");
        shuffle.get("x").add("y");
        shuffle.get("x").add("z");

        shuffle.computeIfAbsent("b", k -> new ArrayList());
        shuffle.get("b").add("x");
        shuffle.get("b").add("y");

        shuffle.computeIfAbsent("c", k -> new ArrayList());
        shuffle.get("c").add("x");
        shuffle.get("c").add("y");

        shuffle.forEach((k, v) -> {
            log.info("k = {}, v = {}", k, v);
        });
    }
}
