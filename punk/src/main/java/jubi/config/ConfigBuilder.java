package jubi.config;

import java.util.Arrays;
import java.util.List;
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
        return new TypedConfigBuilder<>(ConfigUtils::convertToInt, value -> Integer.toString(value));
    }

    public TypedConfigBuilder<Long> longConf() {
        return new TypedConfigBuilder<>(ConfigUtils::convertToLong, value -> Long.toString(value));
    }

    public TypedConfigBuilder<Double> doubleConf() {
        return new TypedConfigBuilder<>(ConfigUtils::convertToDouble, value -> Double.toString(value));
    }

    public TypedConfigBuilder<Boolean> booleanConf() {
        return new TypedConfigBuilder<>(ConfigUtils::convertToBoolean, value -> Boolean.toString(value));
    }

    public TypedConfigBuilder<String> stringConf() {
        return new TypedConfigBuilder<>(value -> value);
    }

    public class TypedConfigBuilder<T> {
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

    class EnumTypedConfigBuilder<E extends Enum<E>> {
        private Class<Enum<E>> clazz;
        private Function<String, List<E>> textToValueFunc;
        private Function<List<E>, String> valueToTextFunc;
    }

    class ListTypedConfigBuilder<E> {
        private Function<String, List<E>> textToValueFunc;
        private final Function<List<E>, String> valueToTextFunc;

        public ListTypedConfigBuilder(Function<String, List<E>> textToValueFunc, Function<List<E>, String> valueToTextFunc) {
            this.textToValueFunc = textToValueFunc;
            this.valueToTextFunc = valueToTextFunc;
        }

        public ListTypedConfigBuilder<E> checkValue(Function<E, Boolean> validator, String errMsg) {
            textToValueFunc = (v) -> {
                List<E> values = textToValueFunc.apply(v);

                if (values.stream().anyMatch(validator::apply)) {
                    throw new IllegalArgumentException(errMsg);
                }

                return values;
            };
            return this;
        }

        public ConfigEntry<List<E>> create() {
            ConfigEntry<List<E>> entry = new ConfigEntry<>(
                    ConfigBuilder.this.key,
                    ConfigBuilder.this.doc,
                    textToValueFunc,
                    valueToTextFunc
            );
            ConfigBuilder.this.callback.accept(entry);
            return entry;
        }

        public ConfigEntry<List<E>> createWithDefault(E... values) {
            ConfigEntry<List<E>> entry = new ConfigEntry<>(
                    ConfigBuilder.this.key,
                    ConfigBuilder.this.doc,
                    Arrays.asList(values),
                    textToValueFunc,
                    valueToTextFunc
            );
            ConfigBuilder.this.callback.accept(entry);
            return entry;
        }
    }
}
