package checkPack.concreteRules;

import checkPack.ErrorHandler;
import gui.MainFrame;
import resource.DBNode;
import resource.DBNodeComposite;
import tree.implementation.TreeImplementation;

import java.util.ArrayList;
import java.util.List;

public class NotFound implements Rule{
    private ArrayList<String> columnsList;
    private ArrayList<String> tableList;
    private ArrayList<String> whereList;
    private DBNodeComposite root;
    private ErrorHandler errorHandler;
    private ArrayList<String> agregationList;

    public NotFound(){
        agregationList = new ArrayList<>();
        agregationList.add("COUNT(");
        agregationList.add("MAX(");
        agregationList.add("MIN(");
        agregationList.add("SUM(");
        agregationList.add("AVG(");
    }

    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("NotFound");
        root = ((DBNodeComposite)((TreeImplementation)MainFrame.getInstance().getAppCore().getTree()).getRoot().getDbNode());
        columnsList = new ArrayList<>();
        tableList = new ArrayList<>();
        whereList = new ArrayList<>();
        String[] separateQuery = string.split(" ");
        for(int i = 0 ; i<separateQuery.length;i++) {
            if (separateQuery[i].equalsIgnoreCase("SELECT"))
                return checkSelect(separateQuery, i+1);
            if (separateQuery[i].equalsIgnoreCase("INSERT"))
                return checkInsert(separateQuery, i+1);
            if (separateQuery[i].equalsIgnoreCase("UPDATE"))
                return checkUpdate(separateQuery, i+1);
            if (separateQuery[i].equalsIgnoreCase("DELETE"))
                return checkDelete(separateQuery, i+1);
            if (separateQuery[i].equalsIgnoreCase("DROP"))
                return checkDrop(separateQuery, i+1 );
        }
        return null;
    }


    private ErrorHandler checkSelect(String[] separateQuery,int in){
        int i = in;
        boolean foundFrom = false;
        if(separateQuery[i].charAt(0) == ("*").charAt(0)){
            i++;
            return checkFrom(separateQuery,i);
        } else {
            if(separateQuery[i].length()>6) {
                if (agregationList.contains(separateQuery[i].substring(0, 4).toUpperCase()))
                    separateQuery[i] = separateQuery[i].substring(4, separateQuery[i].length()-1);
                if(agregationList.contains(separateQuery[i].substring(0,6).toUpperCase()))
                    separateQuery[i] = separateQuery[i].substring(6, separateQuery[i].length()-1);
            }
            if(separateQuery[i].endsWith(","))
                while (separateQuery[i].endsWith(",")){
                    columnsList.add(separateQuery[i].substring(0,separateQuery[i].length()-1).toUpperCase());
                    i++;
                }
            columnsList.add(separateQuery[i].toUpperCase());
                i++;
                for(int j = i; j<separateQuery.length;j++) {
                    if (separateQuery[j].equalsIgnoreCase("FROM")) {
                        foundFrom = true;
                    }
                    if (foundFrom)
                        return checkFrom(separateQuery, j);
                }
        }

        return null;
    }

    private ErrorHandler checkInsert(String[] separateQuery,int in){
        int i  = in;
        if(separateQuery[i].equalsIgnoreCase("INTO")){
            if(i<separateQuery.length-1)
                i++;
            tableList.add(separateQuery[i].toUpperCase());
        } else return null;
        {
            boolean ended = false;
        while (!ended) {
            if (i < separateQuery.length - 1)
                i++;
            String[] split = separateQuery[i].split(",");
            for (String s : split) {
                if (s.substring(0, 1).equalsIgnoreCase("(")) {
                    if (!(s.equalsIgnoreCase("("))) {
                        columnsList.add(s.substring(1).toUpperCase());
                    }
                } else {
                    if (!(s.equalsIgnoreCase(")")) && s.endsWith(")")) {
                        columnsList.add(s.substring(0,s.length()-1).toUpperCase());
                        ended = true;
                    } else  if(s.equalsIgnoreCase(")")){
                        ended = true;
                    } else {
                        columnsList.add(s.toUpperCase());
                    }
                    if (separateQuery[i].equalsIgnoreCase("VALUES")) {
                        ended = true;
                    }
                }
            }

        }
            String msg;
            if((msg = checkTable(tableList)) == null){
                if((msg =checlColumns(columnsList,tableList)) != null){
                    errorHandler.getErrorDescriptions().add(msg);
                    errorHandler.getErrorSugestions().add(msg);
                    return errorHandler;
                }
            } else {
                errorHandler.getErrorDescriptions().add(msg);
                errorHandler.getErrorSugestions().add(msg);
                return errorHandler;
            }
        }

        return null;
    }

    private ErrorHandler checkUpdate(String[] separateQuery , int in){
        int i = in;
        tableList.add(separateQuery[i].toUpperCase());
        if (i < separateQuery.length - 1)
            i++;
        if(!(separateQuery[i].equalsIgnoreCase("SET")))
            return null;
        boolean finished = false;
        boolean odd = true;
        while(!finished){
            if (i < separateQuery.length - 1)
                i++;
            String[] split = separateQuery[i].split(",");
            String[] newSplit = String.join("=",split).split("=");
            for (String s : newSplit) {
                if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("WHERE"))
                    if (odd) {
                        columnsList.add(s.toUpperCase());
                        odd = false;
                    } else {
                    odd = true;
                    }
                if (s.equalsIgnoreCase("WHERE"))
                    finished = true;
            }
        }
        finished = false;
        odd = true;
        while (!finished){
            if (i < separateQuery.length - 1)
                i++;
            if(i == separateQuery.length -1)
                finished = true;
            String[] split = separateQuery[i].split(",");
            String[] newSplit = String.join("=",split).split("=");
            for (String s : newSplit) {
                if (odd) {
                    whereList.add(s.toUpperCase());
                    odd = false;
                } else {
                    odd = true;
                }
            }
            String msg;
            String msg2 = null;
            if((msg =checkTable(tableList)) == null){
                if((msg =checlColumns(columnsList,tableList)) == null && (msg2 = checlColumns(whereList,tableList)) == null) {
                    return null;
                }else {
                    if(msg == null) {
                        errorHandler.getErrorDescriptions().add(msg2);
                        errorHandler.getErrorSugestions().add(msg2);
                    }
                    if(msg2 == null){
                        errorHandler.getErrorDescriptions().add(msg);
                        errorHandler.getErrorSugestions().add(msg);
                    }
                    if(msg != null && msg2 != null) {
                        String[] messages = msg.split(";");
                        msg2 = messages[0] + msg2;
                        errorHandler.getErrorDescriptions().add(msg2);
                        errorHandler.getErrorSugestions().add(msg2);
                    }
                    return errorHandler;

                }
            } else {
                errorHandler.getErrorDescriptions().add(msg);
                errorHandler.getErrorSugestions().add(msg);
                return errorHandler;
            }
        }
        return null;
    }

    private ErrorHandler checkDelete(String[] separateQuery, int in){
        int i = in;
        if(!(separateQuery[i].equalsIgnoreCase("FROM")))
            return null;
        if (i < separateQuery.length - 1)
            i++;
        tableList.add(separateQuery[i].toUpperCase());
        boolean finished = false;
        boolean odd = true;
        while (!finished){
            if (i < separateQuery.length - 1)
                i++;
            if(i == separateQuery.length -1)
                finished = true;
            String[] split = separateQuery[i].split(",");
            String[] newSplit = String.join("=",split).split("=");
            for (String s : newSplit) {
                if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("WHERE"))
                    if (odd) {
                        whereList.add(s.toUpperCase());
                        odd = false;
                    } else {
                        odd = true;
                    }
            }
        }
        String msg;
        if((msg =checkTable(tableList)) == null){
            if((msg =checlColumns(whereList,tableList)) == null) {
                return null;
            } else {
                errorHandler.getErrorDescriptions().add(msg);
                errorHandler.getErrorSugestions().add(msg);
                return errorHandler;
            }
        } else {
            errorHandler.getErrorDescriptions().add(msg);
            errorHandler.getErrorSugestions().add(msg);
            return errorHandler;
        }
    }

    private ErrorHandler checkDrop(String[] separateQuery, int in){
        if (separateQuery[in].equalsIgnoreCase("TABLE"))
            tableList.add(separateQuery[in+1].toUpperCase());
        String msg = null;
        if((msg =checkTable(tableList)) == null){
            return null;
        } else {
            errorHandler.getErrorDescriptions().add(msg);
            errorHandler.getErrorSugestions().add(msg);
            return errorHandler;
        }
    }

    private ErrorHandler checkFrom(String[] separateQuery,int i){
        if(separateQuery[i].equalsIgnoreCase("FROM")||
                separateQuery[i].equalsIgnoreCase("SET")){
            if(i<separateQuery.length-1)
            i++;
        } else {
            return null;
        }
            if(separateQuery[i].endsWith(","))
                while (separateQuery[i].endsWith(",")){
                    if(separateQuery[i].contains("."))
                        tableList.add((separateQuery[i].substring(0,separateQuery[i].length()-1).split("\\."))[1].toUpperCase());
                    if(i<separateQuery.length-1)
                    i++;
                }
            if(separateQuery[i].contains(".")) {
                tableList.add((separateQuery[i].split("\\."))[1].toUpperCase());
            } else tableList.add(separateQuery[i].toUpperCase());
                if(i<separateQuery.length-1)
                    i++;

            if(separateQuery[i].equalsIgnoreCase("JOIN")||
                    separateQuery[i].equalsIgnoreCase("LEFT")||
                    separateQuery[i].equalsIgnoreCase("RIGHT")||
                    separateQuery[i].equalsIgnoreCase("FULL")||
                    separateQuery[i].equalsIgnoreCase("INNER")||
                    separateQuery[i].equalsIgnoreCase("CROSS")) {
                if (separateQuery[i].equalsIgnoreCase("JOIN")) {
                    if (i < separateQuery.length)
                        i++;
                    if(separateQuery[i].contains("."))
                        tableList.add((separateQuery[i].split("\\."))[1].toUpperCase());
                } else {
                    if (i < separateQuery.length)
                        i++;
                            if (i < separateQuery.length - 1)
                                if (separateQuery[i].equalsIgnoreCase("JOIN") ||
                                        separateQuery[i].equalsIgnoreCase("OUTER"))
                                    if (separateQuery[i].equalsIgnoreCase("JOIN")) {
                                i++;
                            if(separateQuery[i].contains("."))
                                tableList.add((separateQuery[i].split("\\."))[1].toUpperCase());
                        } else {
                            if (i < separateQuery.length - 1)
                                i++;
                            if (separateQuery[i].equalsIgnoreCase("JOIN")) {
                                if (i < separateQuery.length - 1)
                                    i++;
                                if(separateQuery[i].contains("."))
                                    tableList.add((separateQuery[i].split("\\."))[1].toUpperCase());
                            }
                        }
                }
            }
                String st;
                if((st = checkTable(tableList)) == null){
                    if((st = checlColumns(columnsList,tableList)) == null) {
                        return null;
                    }else {
                        for(String s:st.split(";")) {
                            errorHandler.getErrorDescriptions().add(s);
                            errorHandler.getErrorSugestions().add(s);
                        }
                        return errorHandler;
                    }
                } else {
                    for(String s:st.split(";")) {
                        errorHandler.getErrorDescriptions().add(s);
                        errorHandler.getErrorSugestions().add(s);
                    }
                    return errorHandler;
                }

    }

    private String checlColumns(ArrayList<String> columnsList,ArrayList<String> tableList){
        List<String> validColumnList = new ArrayList<>();
        for (String table:tableList){
            DBNodeComposite child = (DBNodeComposite) root.getChildByName(table);
            for(DBNode dbNode:child.getChildren()){
                validColumnList.add(dbNode.getName().toUpperCase());
            }
        }
        if(validColumnList.containsAll(columnsList))
            return null;
        String ret = "";
        for(String column: columnsList){
            if(!validColumnList.contains(column))
                ret = ret + column +", ";
        }
        ret = ret + ";";
        for(String table : tableList){
            ret = ret +table+", ";
        }
        return ret;
    }

    private String checkTable(ArrayList<String> tableList){
        List<DBNode> rootChildren = root.getChildren();
        List<String> validTables = new ArrayList<>();
        for (DBNode dbNode : rootChildren){
            validTables.add(dbNode.getName().toUpperCase());
        }
        if(validTables.containsAll(tableList))
                return null;
        String ret = "";
        for(String table: tableList){
            if(!validTables.contains(table))
                ret = ret + table + ", ";
        }
        ret = ret +";" + root.getName();
        return ret;
    }
}
