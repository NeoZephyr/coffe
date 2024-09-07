package storage.trino.executor.operator;

import java.util.List;

public class Scan {

    // table_name, location
    // index_name, location
    // index_name, start, end

    List tableGetAll(String tableName) {
        // get table info by tableName from catalog
        // collect tableGetOne
        return null;
    }

    void tableGetOne(String tableName, Object location) {}

    // index: indexName, columns, tableName
    // index data
    // idx (c1v1, c2v1, c3v1) -> location1
    // idx (c1v2, c2v2, c3v2) -> location2
    void indexGetRange(String indexName, Object start, Object end) {
        // get index info by indexName
        // 回表 get location -> tableGetOne
    }

    void coverIndexGetRange(String indexName, Object start, Object end) {
        // 不需要回表
    }

    // 等值查询
    void indexGetEq(String indexName, Object value) {}

    void indexGetOne(String indexName, Object location) {}
}
