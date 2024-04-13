package foundation;

import java.io.Serializable;

public abstract class Event<T> implements Serializable {

    abstract T getValue();
}
