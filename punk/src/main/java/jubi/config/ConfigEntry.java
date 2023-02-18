package jubi.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ConfigEntry<T> {
    private String key;
    private String doc;
    private T value;

    private Function<String, T> textToValueFunc;
    private Function<T, String> valueToTextFunc;

    private static final String UNDEFINED = "<undefined>";
    private static final ConcurrentHashMap<String, ConfigEntry<?>> KNOWN_CONFIGS = new ConcurrentHashMap<>();

    public ConfigEntry(String key, String doc, Function<String, T> textToValueFunc, Function<T, String> valueToTextFunc) {
        this(key, doc, null, textToValueFunc, valueToTextFunc);
    }

    public ConfigEntry(String key, String doc, T value, Function<String, T> textToValueFunc, Function<T, String> valueToTextFunc) {
        this.key = key;
        this.doc = doc;
        this.value = value;
        this.textToValueFunc = textToValueFunc;
        this.valueToTextFunc = valueToTextFunc;

        registerEntry();
    }

    public String key() {
        return key;
    }

    public T defaultValue() {
        return value;
    }

    public String defaultValueStr() {
        return convertToText(value);
    }

    public String convertToText(T value) {
        if (value == null) {
            return UNDEFINED;
        }

        return valueToTextFunc.apply(value);
    }

    public T convertToValue(String text) {
        return textToValueFunc.apply(text);
    }

    @Override
    public String toString() {
        return String.format("ConfigEntry(key=%s, defaultValue=%s, doc=%s)", key, defaultValueStr(), doc);
    }

    private void registerEntry() {
        ConfigEntry<?> exist = KNOWN_CONFIGS.putIfAbsent(key, this);
        assert (exist == null) : String.format("Config entry %s already registered!", key);
    }
}
