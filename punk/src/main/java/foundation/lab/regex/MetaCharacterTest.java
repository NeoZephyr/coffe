package foundation.lab.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaCharacterTest {
    public static void main(String[] args) {
        String input = "Abc  1234  def 18201984521  ~`!@#$%^&*()_+-= {}|[]\\;':\",./<>?";

        test(".", input);
        test("\\d", input);
        test("\\D", input);

        test("\\w", input);
        test("\\W", input);

        test("\\s", input);
        test("\\S", input);

        test("\\d{3}", input);
        test("\\d*", input);
        test("\\d+", input);
        test("\\d?", input);

        test("Abc|def", input);

        test("1[3-9]\\d{9}", input);
    }

    private static void test(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        int matchCount = 0;

        while (matcher.find()) {
            matchCount++;

        }

        System.out.printf("regex: %s, matchCount: %d\n", regex, matchCount);
    }
}
