package lake.expression;

public abstract class Literal<T> {
    private T value;

    public T value() {
        return value;
    }
}
