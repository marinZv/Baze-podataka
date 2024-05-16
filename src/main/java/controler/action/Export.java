package controler.action;

import controler.AbstractBaZeControler;
import controler.MyFilter;
import gui.MainFrame;
import resource.data.Row;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class Export extends AbstractBaZeControler {
    String iconString = "src/main/resources/export.png";

    public Export(){
        putValue(SMALL_ICON,loadIcon(iconString));
        putValue(NAME,"Export");
        putValue(SHORT_DESCRIPTION,"Export CSV file");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectExport();
    }

    private void selectExport(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new MyFilter());

        PrintWriter writer = null;
        if(jFileChooser.showSaveDialog(MainFrame.getInstance())==JFileChooser.APPROVE_OPTION)
        try {
            File file = new File(jFileChooser.getSelectedFile().getPath()+".csv");
            if(file.createNewFile())
                System.out.println("fajl postoji");
            writer = new PrintWriter(file);

            List<Row> rows = MainFrame.getInstance().getAppCore().getTableModel().getRows();
            if (rows == null)return;
            String collumns = "";
            int j = 0;
            for (String s:rows.get(0).getFields().keySet()){
                if(j == 0){
                    collumns += s;
                    j++;
                } else {
                    collumns = collumns + ","+s;
                }
            }
            writer.println(collumns);
            for(Row r:rows){
                String output;
                int i = 0;
                output = "";
                for(Object o : r.getFields().values()){
                    if(i == 0)
                        if(o instanceof Integer){
                            output = output +o.toString();
                        }else {
                        output = output +"'"+o.toString()+"'";
                        i++;
                    } else
                        if(o instanceof Integer) {
                            output = output + ","+o.toString();
                        } else {
                        output = output + ","+"'"+o.toString()+"'";
                    }
                }
                writer.println(output);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(writer!=null)
                    writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
