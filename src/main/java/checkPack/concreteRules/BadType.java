package checkPack.concreteRules;

import checkPack.ErrorHandler;
import gui.MainFrame;
import resource.DBNode;
import resource.DBNodeComposite;
import resource.enums.AttributeType;
import resource.implementations.Attribute;
import tree.implementation.TreeImplementation;

import java.util.ArrayList;
import java.util.HashMap;

public class BadType implements Rule{

    ArrayList<String> selectList;
    ArrayList<String> intoList;
    HashMap<String,String> types;
    ArrayList<String> intolist2;
    private ErrorHandler errorHandler;

    public BadType(){

    }
    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadType");
        int j = 0;
        String temp = null;
        String table = null;
        selectList = new ArrayList<>();
        intoList = new ArrayList<>();
        intolist2 = new ArrayList<>();
        types = new HashMap<>();
        String[] separateQuery = string.split(" ");
        if(separateQuery.length<5)
            return null;
        String type = null;
        if (!separateQuery[0].equalsIgnoreCase("CREATE"))
            return null;
        if ((separateQuery[1].equalsIgnoreCase("OR") &&
                separateQuery[2].equalsIgnoreCase("REPLACE") &&
                separateQuery[3].equalsIgnoreCase("PROCEDURE")) ||
                separateQuery[1].equalsIgnoreCase("PROCEDURE"))
            type = "PROCEDURE";
        if ((separateQuery[1].equalsIgnoreCase("OR") &&
                separateQuery[2].equalsIgnoreCase("REPLACE") &&
                separateQuery[3].equalsIgnoreCase("FUNCTION")) ||
                separateQuery[1].equalsIgnoreCase("FUNCTION"))
            type = "FUNCTION";
        if (type == null)
            return null;
        boolean hasbBackets = false;
        boolean hasBegin = false;
        boolean hasSelect = false;
        boolean hasInto = false;
        boolean hasFrom = false;
        boolean gotTable = false;
        for(String s : separateQuery){

            if (s.endsWith("(")) {
                hasbBackets = true;
                continue;
            }
            if (s.startsWith("(")) {
                s = s.substring(1);
                hasbBackets = true;
            }

            if (s.startsWith(",")) {
                s = s.substring(1);
            }

            if (s.endsWith(")") && s.length() == 1) {
                hasbBackets = false;
                continue;
            }
            if (s.endsWith(")") && s.length() > 1) {
                s = s.substring(0, s.length() - 1);
                hasbBackets = false;
            }

            if (s.endsWith(",") && s.length() > 1)
                s = s.substring(0, s.length() - 1);

            if (s.endsWith(";") && s.length() > 1)
                s = s.substring(0, s.length() - 1);

            if(s.equalsIgnoreCase(","))
                continue;

            if(s.equalsIgnoreCase("BEGIN")) {
                hasBegin = true;
                continue;
            }

            if(hasBegin){
                if(s.equalsIgnoreCase("SELECT")){
                    hasSelect = true;
                    continue;
                }


                if(hasSelect && !hasFrom){

                    if(s.equalsIgnoreCase("INTO")){
                        hasInto = true;
                        continue;
                    }
                    if(!hasInto){
                            selectList.add(s);
                    }
                    if(hasInto){
                        if(s.equalsIgnoreCase("FROM")){
                            hasFrom = true;
                            continue;
                        }
                        intolist2.add(s);
                    }

                }
                if(hasFrom && !gotTable){
                    table = s;
                    gotTable = true;
                }
            }



            if (hasbBackets && !hasBegin && !s.equals(",")) {
                if (s.equalsIgnoreCase("IS") || s.equalsIgnoreCase("AS"))
                    continue;
                if (type.equalsIgnoreCase("PROCEDURE")) {
                    if (j % 3 == 0) {
                        intoList.add(s);
                        temp = s;
                        j++;
                    } else if (j % 3 == 2) {
                        types.put(temp, s);
                        j++;
                    } else {
                        j++;
                    }
                } else {
                    if (j % 2 == 0) {
                        intoList.add(s);
                        temp = s;
                        j++;
                    } else if (j % 2 == 1) {
                        types.put(temp, s);
                        j++;
                    }
                }
            }

        }

        if(!(hasBegin && hasSelect && hasInto && hasFrom))
            return null;
        if(intolist2.size() != selectList.size())
            return null;
        String msg;
        if((msg = checkTypes(selectList,intoList,types,table,intolist2)) == null)
            return null;
        errorHandler.getErrorDescriptions().add(msg);
        errorHandler.getErrorSugestions().add(msg.split("not")[0]);
        errorHandler.getErrorSugestions().add(msg.split("not")[1]);
        return errorHandler;
    }


    private String checkTypes(ArrayList<String> selectListf,ArrayList<String> intoListf,HashMap<String,String> typesf,String tablef,ArrayList<String> intolist2f){
        String tablename = null;
        String str = "";
        HashMap<String ,AttributeType> hmf = getTypes(typesf,intoListf);
        if(hmf == null)
            return null;
        for(int i = 0; i< selectListf.size();i++){
            if(hmf.containsKey(intolist2f.get(i))){
                AttributeType checkType = hmf.get(intolist2f.get(i));
                if(tablef.contains(".")) {
                    String[] pathing = tablef.split("\\.");
                    if(pathing.length == 2)
                        tablename = pathing[1];
                    DBNodeComposite root = ((DBNodeComposite)((TreeImplementation)MainFrame.getInstance().getAppCore().getTree()).getRoot().getDbNode());
                    DBNodeComposite table = (DBNodeComposite) root.getChildByName(tablename);
                    if(table == null)
                        return null;
                    DBNode columnNode = table.getChildByName(selectListf.get(i));
                    if(columnNode!=null) {
                        AttributeType at = ((Attribute) columnNode).getAttributeType();
                        if(checkType != at)
                            str = str +intolist2f.get(i) +" not "+at.toString();
                    }
                }

            }
        }
        if(str.equalsIgnoreCase(""))
            return null;
        return str;
    }

    private HashMap<String,AttributeType> getTypes(HashMap<String ,String> typesfx, ArrayList<String> intolistfx){
        HashMap<String ,AttributeType> hm = new HashMap<>();
        String path;
        for (String s:intolistfx) {
            if(typesfx.containsKey(s)){
                path = typesfx.get(s);
                if(path.contains(".")){
                    String[] completePath = path.split("\\.");
                    if(completePath.length == 3){
                        String tabela = completePath[1];
                        String kolona = completePath[2].replaceAll("%type","");


                        DBNodeComposite root = ((DBNodeComposite)((TreeImplementation)MainFrame.getInstance().getAppCore().getTree()).getRoot().getDbNode());
                        DBNodeComposite table = (DBNodeComposite) root.getChildByName(tabela);
                        if(table == null)
                            return null;
                        DBNode columnNode = table.getChildByName(kolona);
                        if(columnNode!=null){
                            AttributeType at = ((Attribute)columnNode).getAttributeType();
                            hm.put(s,at);
                        } else return null;
                    }
                } else {
                    if(path.startsWith("char"))
                        hm.put(s,AttributeType.CHAR);
                    if(path.startsWith("varchar"))
                        hm.put(s,AttributeType.VARCHAR);
                    if(path.startsWith("text"))
                        hm.put(s,AttributeType.TEXT);
                    if(path.equalsIgnoreCase("date"))
                        hm.put(s,AttributeType.DATE);
                    if(path.startsWith("datetime"))
                        hm.put(s,AttributeType.DATETIME);
                    if(path.startsWith("float"))
                        hm.put(s,AttributeType.FLOAT);
                    if(path.startsWith("real"))
                        hm.put(s,AttributeType.REAL);
                    if(path.startsWith("bit"))
                        hm.put(s,AttributeType.BIT);
                    if(path.startsWith("bigint"))
                        hm.put(s,AttributeType.BIGINT);
                    if(path.startsWith("numeric"))
                        hm.put(s,AttributeType.NUMERIC);
                    if(path.equalsIgnoreCase("decimal"))
                        hm.put(s,AttributeType.DECIMAL);
                    if(path.startsWith("decimal_unsigned"))
                        hm.put(s,AttributeType.DECIMAL_UNSIGNED);
                    if(path.equalsIgnoreCase("int"))
                        hm.put(s,AttributeType.INT);
                    if(path.startsWith("int_unsigned"))
                        hm.put(s,AttributeType.INT_UNSIGNED);
                    if(path.startsWith("image"))
                        hm.put(s,AttributeType.IMAGE);
                    if(path.startsWith("smallint"))
                        hm.put(s,AttributeType.SMALLINT);
                    if(path.startsWith("nvarchar"))
                        hm.put(s,AttributeType.NVARCHAR);

                }
            } else return null;

        }
        return hm;
    }
}
