package checkPack.concreteRules;


import checkPack.ErrorHandler;

import java.util.*;

public class BadOrder implements Rule{

    private final Map<String,Integer> weightMap;
    private Set<String> keys;
    private ErrorHandler errorHandler;

    public BadOrder(){
        this.weightMap = new HashMap<>();
        this.keys = new HashSet<>();
        fillMap();
    }


    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("BadOrder");
        List<String> words = new ArrayList<>();
        List<Integer> order = new ArrayList<>();
        for (String s : string.split(" ")) {
            if (keys.contains(s.toUpperCase())) {
                order.add(weightMap.get(s.toUpperCase()));
                words.add(s.toUpperCase());
            }
        }
        String msg;
        if ((msg = isValid(order, words)) == null)
            return null;
        errorHandler.getErrorDescriptions().add(msg);
        errorHandler.getErrorSugestions().add((msg.split(","))[1]);
        return errorHandler;
    }


    private void fillMap(){
        weightMap.put("SELECT",1);
        weightMap.put("INSERT",1);
        weightMap.put("UPDATE",1);
        weightMap.put("DELETE",1);
        weightMap.put("DROP",1);
        weightMap.put("EXEC",1);
        weightMap.put("FROM",2);
        weightMap.put("INTO",2);
        weightMap.put("SET",2);
        weightMap.put("WITH",2);
        weightMap.put("JOIN",3);
        weightMap.put("WHERE",4);
        weightMap.put("GROUP",5);
        weightMap.put("HAVING",6);
        weightMap.put("ORDER",7);
        keys = weightMap.keySet();
    }

    private String isValid(List<Integer> orderList , List<String> wordList){
        int curr;
        int prev = 0;
        for (int i : orderList){
            curr = i;
            if(curr<prev)
                return wordList.get(i-1)+","+wordList.get(i);
            prev = curr;
        }
        return null;
    }
}
