package database;

import resource.DBNode;
import resource.data.Row;

import java.util.ArrayList;
import java.util.List;

public interface Repository {

    DBNode getSchema();

    List<Row> get(String from);

    void updateOnInsert(ArrayList<String> querys);

    String getImportedKey(String tableName);
}
