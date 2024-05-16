package checkPack.concreteRules;

import checkPack.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class AgregationNotGroup implements Rule{

    List<String> agregationList;
    List<String> columnList;
    private ErrorHandler errorHandler;

    public AgregationNotGroup(){
        agregationList = new ArrayList<>();
        agregationList.add("COUNT(");
        agregationList.add("MAX(");
        agregationList.add("MIN(");
        agregationList.add("SUM(");
        agregationList.add("AVG(");
    }


    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("AgregationNotGroup");
        columnList = new ArrayList<>();
        String[] separateQuery = string.split(" ");
        boolean isAgregated = false;
        boolean isFromed = false;
        boolean isGroup = false;
        if(!(separateQuery[0].equalsIgnoreCase("SELECT")))
            return null;
        for (int i = 1 ; i< separateQuery.length;i++){
            if(separateQuery[i].length() > 6){
                if(agregationList.contains(separateQuery[i].substring(0,4).toUpperCase())||agregationList.contains(separateQuery[i].substring(0,6).toUpperCase())){
                    isAgregated = true;
                    continue;
                }
            }
            if(separateQuery[i].equalsIgnoreCase("FROM"))
                isFromed = true;
            if(separateQuery[i].equalsIgnoreCase("GROUP"))
                isGroup = true;
            if(!isFromed)
                if(separateQuery[i].endsWith(",")){
                    if(!separateQuery[i].equalsIgnoreCase(","))
                    columnList.add(separateQuery[i].substring(0,separateQuery[i].length()-1).toUpperCase());
                }else {
                        columnList.add(separateQuery[i].toUpperCase());
                        }
            if(isFromed && isGroup){
                    columnList.remove(separateQuery[i].toUpperCase());
                    columnList.remove(separateQuery[i].substring(0,separateQuery[i].length()-1).toUpperCase());
            }

        }
        if(!isAgregated)
            return null;
        String msg = "";
        if(!isGroup || !columnList.isEmpty()){
            for (String col : columnList) {
                msg = msg + col +", ";
                errorHandler.getErrorDescriptions().add(msg);
                errorHandler.getErrorSugestions().add(msg);
                return errorHandler;
            }
        }else {
            return null;
        }
        return null;
    }
}
