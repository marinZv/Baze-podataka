package checkPack.concreteRules;


import checkPack.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class BadAlias implements Rule {


    private final List<String> keyWords;
    private ErrorHandler errorHandler;

    public BadAlias(){

        keyWords = new ArrayList<>();
        initAliasList();

    }


    private void initAliasList(){
        keyWords.add("SELECT");
        keyWords.add("BETWEEN");
        keyWords.add("CASE");
        keyWords.add("CREATE");
        keyWords.add("DELETE");
        keyWords.add("DISTINCT");
        keyWords.add("DROP");
        keyWords.add("DEFAULT");
        keyWords.add("EXEC");
        keyWords.add("EXISTS");
        keyWords.add("FOREIGN");
        keyWords.add("FROM");
        keyWords.add("FULL");
        keyWords.add("JOIN");
        keyWords.add("GROUP");
        keyWords.add("IN");
        keyWords.add("INNER");
        keyWords.add("INSERT");
        keyWords.add("INTO");
        keyWords.add("IS");
        keyWords.add("OR");
        keyWords.add("ORDER");
        keyWords.add("PRIMARY");
        keyWords.add("PROCEDURE");
        keyWords.add("RIGHT");
        keyWords.add("LEFT");
        keyWords.add("SET");
        keyWords.add("TABLE");
        keyWords.add("WHERE");

    }

    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadAlias");
        String alias = "";

        String[] separateQuery = string.split(" ");

        int countWords = 0;
        boolean hasAs = false;
        boolean hasQuote = false;
        for(String s : separateQuery){

            if(s.equalsIgnoreCase("as")){
                hasAs = true;
                continue;
            }
            if(s.equalsIgnoreCase("FUNCTION"))
                return null;

            if(hasAs){

                if(!s.equalsIgnoreCase("")){
                    if(s.equalsIgnoreCase(",") || keyWords.contains(s.toUpperCase())){
                        hasAs = false;
                        countWords = 0;
                    }else{
                        if(s.substring(0,1).equalsIgnoreCase("\"")){
                            hasQuote = true;
                        }
                        if(!hasQuote){
                            countWords++;
                            alias = alias + s + " ";
                        }
                        if(s.endsWith("\"")){
                            hasQuote = false;
                        }
                    }
                }


            }

            if(countWords > 1) {
                errorHandler.getErrorDescriptions().add(alias);
                errorHandler.getErrorSugestions().add("\""+alias+"\"");
                return errorHandler;
            }
        }
        return null;
    }
}
