package jubi.config;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigBuilder {

    private String doc = "";
    private final String key;
    private Consumer<ConfigEntry<?>> callback;

    public ConfigBuilder(String key) {
        this.key = key;
    }

    public ConfigBuilder doc(String doc) {
        this.doc = doc;
        return this;
    }

    public ConfigBuilder callback(Consumer<ConfigEntry<?>> callback) {
        this.callback = callback;
        return this;
    }

    public TypedConfigBuilder<Integer> intConf() {
        return new TypedConfigBuilder<>(Integer::valueOf, value -> Integer.toString(value));
    }

    public TypedConfigBuilder<Long> longConf() {
        return new TypedConfigBuilder<>(Long::valueOf, value -> Long.toString(value));
    }

    public TypedConfigBuilder<Double> doubleConf() {
        return new TypedConfigBuilder<>(Double::valueOf, value -> Double.toString(value));
    }

    public TypedConfigBuilder<Boolean> booleanConf() {
        return new TypedConfigBuilder<>(Boolean::valueOf, value -> Boolean.toString(value));
    }

    public TypedConfigBuilder<String> stringConf() {
        return new TypedConfigBuilder<>(value -> value);
    }

    class TypedConfigBuilder<T> {
        private final Function<String, T> textToValueFunc;
        private final Function<T, String> valueToTextFunc;

        public TypedConfigBuilder(Function<String, T> textToValueFunc) {
            this(textToValueFunc, Object::toString);
        }

        public TypedConfigBuilder(Function<String, T> textToValueFunc, Function<T, String> valueToTextFunc) {
            this.textToValueFunc = textToValueFunc;
            this.valueToTextFunc = valueToTextFunc;
        }

        public ConfigEntry<T> create() {
            ConfigEntry<T> entry = new ConfigEntry<>(
                    ConfigBuilder.this.key,
                    ConfigBuilder.this.doc,
                    textToValueFunc,
                    valueToTextFunc
            );
            ConfigBuilder.this.callback.accept(entry);
            return entry;
        }

        public ConfigEntry<T> createWithDefault(T value) {
            ConfigEntry<T> entry = new ConfigEntry<>(
                    ConfigBuilder.this.key,
                    ConfigBuilder.this.doc,
                    value,
                    textToValueFunc,
                    valueToTextFunc
            );
            ConfigBuilder.this.callback.accept(entry);
            return entry;
        }
    }
}
