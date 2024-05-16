package checkPack.concreteRules;

import checkPack.ErrorHandler;
import gui.MainFrame;
import resource.DBNode;
import resource.DBNodeComposite;
import resource.implementations.Entity;
import tree.TreeItem;

import java.util.ArrayList;

public class BadCSV implements Rule{

    private ErrorHandler errorHandler;
    private TreeItem node;

    public BadCSV(){
    }


    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadCSV");
        String[] collumns = string.split(",");
        ArrayList<String> validCollumns = getValidCollumns();
        String msg = "";
        if(validCollumns != null)
        for(String s:collumns){
            if(!(validCollumns.contains(s.toUpperCase())))
                msg = msg + s +", ";
        }
        if(msg.equalsIgnoreCase(""))
            return null;
        errorHandler.getErrorDescriptions().add(msg);
        errorHandler.getErrorDescriptions().add(node.getName());
        errorHandler.getErrorSugestions().add(msg);
        return errorHandler;

    }

    private ArrayList<String> getValidCollumns(){
        ArrayList<String> strings = new ArrayList<>();
        node = (TreeItem) MainFrame.getInstance().getjTree().getLastSelectedPathComponent();
        if(node == null || !((node.getDbNode()) instanceof Entity))return null;
        for(DBNode dbNode: ((DBNodeComposite)node.getDbNode()).getChildren()){
            strings.add(dbNode.getName().toUpperCase());
        }
        return strings;
    }

}
