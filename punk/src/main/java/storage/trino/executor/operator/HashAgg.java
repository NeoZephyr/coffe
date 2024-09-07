package storage.trino.executor.operator;

import java.util.List;

public class HashAgg extends PhysicalOperator {

    private boolean asc;
    private String groupByColumn;
    private String aggFuncName;
    private String aggColumn;
    private List columns;

    @Override
    void open() {

    }

    @Override
    void close() {

    }


    // 1.
    @Override
    void next() {
        PhysicalOperator child = children.get(0);
        child.next();
    }
}
