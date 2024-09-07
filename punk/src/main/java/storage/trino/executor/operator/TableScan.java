package storage.trino.executor.operator;

import java.util.List;

public class TableScan extends PhysicalOperator {

    String tableName;
    String condition;
    List columns;

    @Override
    void open() {

    }

    @Override
    void close() {

    }

    @Override
    void next() {
        // for each
        // check condition for each item
        // item: [
        //  TableColumn(t1, id) -> 1
        //  TableColumn(t1, name) -> "jack"
        // ]
    }

    @Override
    void addChild(PhysicalOperator operator) {

    }
}
