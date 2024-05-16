package database;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import resource.DBNode;
import resource.data.Row;

import java.util.List;
@Data
@Getter
@Setter
public class DatabaseImplementation implements Database {

    private Repository repository;

    public DatabaseImplementation(Repository repository){
        this.repository = repository;
    }

    @Override
    public DBNode loadResource() {
        return repository.getSchema();
    }

    @Override
    public List<Row> readDataFromTable(String tableName) {
        return repository.get(tableName);
    }

    public String getImportedKey(String tableName){
        return repository.getImportedKey(tableName);
    }

}
