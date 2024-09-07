package storage.trino.executor.operator;

import java.util.List;

public class Sort extends PhysicalOperator {

    private boolean asc;
    private List sortColumns;

    @Override
    void open() {

    }

    @Override
    void close() {

    }

    // internal sort

    // external sort
    // 1. split to multiple chunks
    // 2. write sorted chunk to temp file
    // 3. merge by pick one from every chunk
    // 4. delete temp file

    @Override
    void next() {
        PhysicalOperator child = children.get(0);
        child.next();
        // sort
    }
}
