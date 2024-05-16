package checkPack.concreteRules;

import checkPack.ErrorHandler;

import java.util.ArrayList;

public class NotUsed implements Rule{

    private ErrorHandler errorHandler;

    public NotUsed(){
    }


    @Override
    public ErrorHandler check(String string) {
        errorHandler = new ErrorHandler("NotUsed");
        ArrayList<String> promenljivaList = new ArrayList<>();
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
        boolean began = false;
        boolean ended = false;
        int j = 0;
        for (int i = 0; i < separateQuery.length; i++) {

            if (separateQuery[i].endsWith("(")) {
                hasbBackets = true;
                continue;
            }
            if (separateQuery[i].startsWith("(")) {
                separateQuery[i] = separateQuery[i].substring(1);
                hasbBackets = true;
            }

            if (separateQuery[i].startsWith(",")) {
                separateQuery[i] = separateQuery[i].substring(1);
            }

            if (separateQuery[i].endsWith(")") && separateQuery[i].length() == 1) {
                hasbBackets = false;
                continue;
            }
            if (separateQuery[i].endsWith(")") && separateQuery[i].length() > 1) {
                separateQuery[i] = separateQuery[i].substring(0, separateQuery[i].length() - 1);
                hasbBackets = false;
            }

            if (separateQuery[i].endsWith(",") && separateQuery[i].length() > 1)
                separateQuery[i] = separateQuery[i].substring(0, separateQuery[i].length() - 1);

            if (separateQuery[i].endsWith(";") && separateQuery[i].length() > 1)
                separateQuery[i] = separateQuery[i].substring(0, separateQuery[i].length() - 1);

            if (separateQuery[i].equalsIgnoreCase("BEGIN")) {
                began = true;
                continue;
            }
            if (separateQuery[i].equalsIgnoreCase("END")) {
                ended = true;
                continue;
            }

            if (hasbBackets && !began && !separateQuery[i].equals(",")) {
                if (separateQuery[i].equalsIgnoreCase("IS") || separateQuery[i].equalsIgnoreCase("AS"))
                    continue;
                if (type.equalsIgnoreCase("PROCEDURE")) {
                    if (j % 3 == 0) {
                        promenljivaList.add(separateQuery[i]);
                        j++;
                    } else {
                        j++;
                    }
                } else {
                    if (j % 2 == 0) {
                        promenljivaList.add(separateQuery[i]);
                        j++;
                    } else {
                        j++;
                    }
                }
            }

            if (began && !ended)
                promenljivaList.remove(separateQuery[i]);

        }
        String msg = null;
        if (!promenljivaList.isEmpty()) {
            for(String promenljiva : promenljivaList){
                msg = promenljiva +", ";
            }
            errorHandler.getErrorDescriptions().add(msg);
            errorHandler.getErrorSugestions().add(msg);
            return errorHandler;
        }else {
            return null;
        }
    }
}
