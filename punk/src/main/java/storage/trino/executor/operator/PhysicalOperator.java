package storage.trino.executor.operator;

import java.util.List;

public abstract class PhysicalOperator {
    String name;
    List<PhysicalOperator> children;
    List outputs;

    // init child nodes
    // read outputs from child node
    abstract void open();

    abstract void close();

    abstract void next();

    void addChild(PhysicalOperator operator) {
        children.add(operator);
    }
}
