package checkPack.concreteRules;

import checkPack.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class BadWhere implements Rule{

    List<String> agregationList;
    private ErrorHandler errorHandler;

    public BadWhere(){
        agregationList = new ArrayList<>();
        agregationList.add("COUNT(");
        agregationList.add("MAX(");
        agregationList.add("MIN(");
        agregationList.add("SUM(");
        agregationList.add("AVG(");
    }

    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadWhere");
        String[] separateQuery = string.split(" ");

        boolean hasWhere = false;
        for(String s : separateQuery){

            if(s.equalsIgnoreCase("where")){
                hasWhere = true;
            }
            if(s.length()>5)
                if(hasWhere && (agregationList.contains(s.substring(0,4).toUpperCase())||agregationList.contains(s.substring(0,6).toUpperCase()))){
                    errorHandler.getErrorDescriptions().add(s);
                    errorHandler.getErrorSugestions().add(s);
                    return errorHandler;
                }
        }


        return null;
    }
}
