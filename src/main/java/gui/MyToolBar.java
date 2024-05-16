package gui;

import javax.swing.*;

public class MyToolBar extends JToolBar {

    public MyToolBar(){
        super(HORIZONTAL);
        setFloatable(false);

        add(MainFrame.getInstance().getActionManager().getBulkImport());
        addSeparator();

        add(MainFrame.getInstance().getActionManager().getExport());
        addSeparator();

        add(MainFrame.getInstance().getActionManager().getPretty());
        addSeparator();

        add(MainFrame.getInstance().getActionManager().getRun());
    }
}
