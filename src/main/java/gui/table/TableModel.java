package gui.table;

import lombok.Data;
import lombok.Getter;
import resource.data.Row;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;
@Data
@Getter
public class TableModel extends DefaultTableModel {


    private List<Row> rows;
    int columnCount = 0;
    private void updateModel(){

        if(rows.isEmpty())
        return;
        columnCount = rows.get(0).getFields().keySet().size();

        Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
        Vector dataVector = new Vector(columnCount);

        for (Row row : rows) {
            dataVector.add(DefaultTableModel.convertToVector(row.getFields().values().toArray()));
        }

        setDataVector(dataVector, columnVector);
    }

    public void setRows(List<Row> rows){
        this.rows = rows;
        updateModel();
    }



}
