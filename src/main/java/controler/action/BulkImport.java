package controler.action;

import controler.AbstractBaZeControler;
import controler.MyFilter;
import gui.MainFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import resource.DBNodeComposite;
import resource.implementations.Entity;
import tree.TreeItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BulkImport extends AbstractBaZeControler {
    String iconString = "src/main/resources/import.png";
    private String collumns = "";
    private String csvhead= "";

    public BulkImport(){
        putValue(SMALL_ICON,loadIcon(iconString));
        putValue(NAME,"Import");
        putValue(SHORT_DESCRIPTION,"Bulk Import CSV file");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreeItem node = (TreeItem) MainFrame.getInstance().getjTree().getLastSelectedPathComponent();
        if(node == null || !((node.getDbNode()) instanceof Entity))return;
        System.out.println(((DBNodeComposite)node.getDbNode()).getChildren());
        String tableName = ((DBNodeComposite) node.getDbNode()).getName();
        ArrayList<String> querys = selectImport(tableName);
        MainFrame.getInstance().getAppCore().runBulk(csvhead,querys);
    }

    private ArrayList<String> selectImport(String tableName){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new MyFilter());

        if(jFileChooser.showOpenDialog(MainFrame.getInstance())==JFileChooser.APPROVE_OPTION)
            try {
                File csvData = jFileChooser.getSelectedFile();
                InputStreamReader io = new InputStreamReader(new FileInputStream(csvData));
                CSVParser parser = new CSVParser(io, CSVFormat.EXCEL);
                List<CSVRecord> list = parser.getRecords();
                boolean isFirst = true;
                ArrayList<String> querys = new ArrayList<>();
                for(CSVRecord record : list){
                    if(isFirst){
                        collumns = String.join(",",record);
                        csvhead = collumns;
                        collumns = "(" + collumns + ")";
                        isFirst = false;
                    } else {
                        String testString = String.join(", ", record);
                        testString = "(" + testString + ")";
                        String query = "INSERT INTO " +tableName + " "+collumns + " VALUES "+testString;
                        System.out.println(query);
                        querys.add(query);
                    }

                }
                return querys;
            } catch (Exception e){
                e.printStackTrace();
            }
        return null;
    }

}
