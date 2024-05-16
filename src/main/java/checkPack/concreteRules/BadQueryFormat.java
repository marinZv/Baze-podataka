package checkPack.concreteRules;

import checkPack.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class BadQueryFormat implements Rule{

    private final HashMap<String,String> ZARULES;
    private  ErrorHandler errorHandler;

    public BadQueryFormat(){
        ZARULES = new HashMap<>();
        fillTheRules();
    }

    private void fillTheRules(){
        ZARULES.put("SELECT","FROM");
        ZARULES.put("INSERT","INTO");
        ZARULES.put("UPDATE","SET");
        ZARULES.put("DELETE","FROM");
        ZARULES.put("GROUP","BY");
    }

    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadQueryFormat");
        ArrayList<String> foundValues = new ArrayList<>();
        String[] separateQuery = string.split(" ");
        for(String s: separateQuery){
            if(ZARULES.containsKey(s.toUpperCase())){
                foundValues.add(ZARULES.get(s.toUpperCase()));
            }
            foundValues.remove(s.toUpperCase());
        }
        if(foundValues.isEmpty())
            return null;
        String msg = "";
        for(String value : foundValues){
            msg = msg+value+", ";
        }
        errorHandler.getErrorDescriptions().add(msg);
        errorHandler.getErrorSugestions().add(msg);
        return errorHandler;
    }
}
