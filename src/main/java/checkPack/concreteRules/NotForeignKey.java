package checkPack.concreteRules;

import checkPack.ErrorHandler;
import gui.MainFrame;
import resource.DBNode;
import resource.DBNodeComposite;
import resource.implementations.AttributeConstraint;
import tree.implementation.TreeImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static resource.enums.ConstraintType.FOREIGN_KEY;
import static resource.enums.ConstraintType.PRIMARY_KEY;

public class NotForeignKey implements Rule{

    HashMap<String,String> tableAlias;
    HashMap<String,String> columnString;
    ArrayList<String> tables;
    ErrorHandler errorHandler;

    public NotForeignKey(){
    }
    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadForeignKey");
        tableAlias = new HashMap<>();
        columnString = new HashMap<>();
        tables = new ArrayList<>();
        String[] separateQuery = string.split(" ");
        int i  = 0;
        if(!separateQuery[i].equalsIgnoreCase("SELECT"))
            return null;
        if(separateQuery[i].equalsIgnoreCase("*"))
            return null;
        boolean hasFrom = false;
        boolean hasJoin = false;
        boolean gotTables = false;
        boolean foundFirst = false;
        String tempTable = "";
        for (i = 1; i<separateQuery.length;i++){
            if(separateQuery[i].equalsIgnoreCase("FROM")){
                hasFrom = true;
                continue;
            }
            if(separateQuery[i].equalsIgnoreCase("JOIN")) {
                hasJoin = true;
                continue;
            }
            if(hasFrom && !hasJoin && !foundFirst){
                if(tempTable.equalsIgnoreCase("")){
                    tempTable = separateQuery[i].toUpperCase();
                    tables.add(tempTable);
                } else {
                    tableAlias.put(tempTable,separateQuery[i].toUpperCase());
                    tempTable = "";
                    foundFirst = true;
                }
            }
            if(hasJoin && !gotTables){
                if(tempTable.equalsIgnoreCase("")){
                    tempTable = separateQuery[i].toUpperCase();
                    tables.add(tempTable);
                } else {
                    tableAlias.put(tempTable,separateQuery[i].toUpperCase());
                    tempTable = "";
                    gotTables = true;
                }
            }
            if(separateQuery[i].contains("(")) {
                if (separateQuery[i].contains(")")) {
                    String[] columns = separateQuery[i].split("=");
                    for (String column : columns) {
                        String[] tableColumn = new String[2];
                        if (column.startsWith("("))
                            tableColumn = column.substring(1).split("\\.");
                        if (column.endsWith(")"))
                            tableColumn = column.substring(0, column.length() - 1).split("\\.");
                        if (tableColumn.length > 1)
                            columnString.put(tableColumn[0].toUpperCase(), tableColumn[1].toUpperCase());
                    }

                } else {
                    //String[] tableColumn = new String[2];
                    if (separateQuery[i].startsWith("(")) {
                        String[] dotString = separateQuery[i].substring(1).split("\\.");
                        columnString.put(dotString[0].toUpperCase(), dotString[1].toUpperCase());
                    }
                }
            }else {
                if (separateQuery[i].endsWith(")")) {
                    String[] dotString = separateQuery[i].substring(0, separateQuery[i].length() - 1).split("\\.");
                    columnString.put(dotString[0].toUpperCase(), dotString[1].toUpperCase());
                    }
                }
            }
        if(tableAlias.isEmpty())
            return null;
        if(columnString.isEmpty())
            return null;
        if(tables.isEmpty())
            return null;
        String msg;
        if((msg = cheackFKS(tableAlias,columnString,tables)) == null)
            return null;
        for(String s : msg.split(";")) {
            errorHandler.getErrorDescriptions().add(s);
            errorHandler.getErrorSugestions().add(s);
        }
        return errorHandler;
    }


    private String cheackFKS(HashMap<String,String> tables,HashMap<String,String> columns, ArrayList<String> tableses){
        String columnproblem = "";
        String tableproblem = "";
        boolean foundFK = false;
        boolean foundPK = false;
        for(String s: tableses){
            DBNodeComposite root = ((DBNodeComposite)((TreeImplementation)MainFrame.getInstance().getAppCore().getTree()).getRoot().getDbNode());
            DBNodeComposite child = (DBNodeComposite) root.getChildByName(s);
            String columnName = columns.get(tables.get(s.toUpperCase()));
            DBNode columnNode= child.getChildByName(columnName);
            if(columnNode!=null){
                List<DBNode> constraints = ((DBNodeComposite)columnNode).getChildren();
                for (DBNode constr :constraints){
                   if(((AttributeConstraint)constr).getConstraintType()==FOREIGN_KEY)
                       foundFK = true;
                   if(((AttributeConstraint)constr).getConstraintType()==PRIMARY_KEY)
                       foundPK = true;
                }
                if(!foundFK && !foundPK){
                    columnproblem = columnproblem + columnName.toLowerCase() +", ";
                    tableproblem = tableproblem + s +", ";
                }
            }
        }
        if(foundFK && foundPK)
            return null;
        return columnproblem + ";" + tableproblem;
    }
}
