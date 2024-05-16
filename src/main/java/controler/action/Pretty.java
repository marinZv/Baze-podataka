package controler.action;

import controler.AbstractBaZeControler;
import gui.MainFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Pretty extends AbstractBaZeControler {
    String iconString = "src/main/resources/pretty.png";
    ArrayList<String> keywords;
    ArrayList<String> newRow;

    public Pretty(){
        keywords = new ArrayList<>();
        newRow = new ArrayList<>();
        init();
        initRow();
        putValue(SMALL_ICON,loadIcon(iconString));
        putValue(NAME,"Pretty");
        putValue(SHORT_DESCRIPTION,"Format query");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String str = MainFrame.getInstance().getjEditorPane().getText();
        boolean first = true;
        boolean needsbr = false;
        System.out.println(str);
        str.replaceAll("<p style=\"margin-top: 0\">","");
        str.replaceAll("<body>","<body><p style=\"margin-top: 0\">");
        str.replaceAll("</p>","");
        str.replaceAll("</body>","</p></body>");
        //MainFrame.getInstance().getjEditorPane().setContentType("text/html");
        String[] texts = str.split(" ");
        String newString;

        for(int i=0; i<texts.length;i++){

            String word = texts[i].replaceAll("\\r","");
            word = word.replaceAll("\\n","");
            if(word.equalsIgnoreCase("<p"))
                texts[i] = "";
            if(word.equalsIgnoreCase("style=\"margin-top:"))
                texts[i] = "";
            if(word.equalsIgnoreCase("0\">"))
                texts[i] = "";
            if(word.equalsIgnoreCase("</p>"))
                texts[i] = "";
            if(word.equalsIgnoreCase("<body>"))
                texts[i] = texts[i]+"<p style=\"margin-top: 0\">";
            if(word.startsWith("</body>"))
                texts[i] = "</p>" + texts[i];

            if(word.endsWith("(")) {
                needsbr = true;
                continue;
            }

            if(keywords.contains(word.toUpperCase())){
                texts[i] = "<span style =\"color:blue\">"+texts[i].toUpperCase()+"</span>";
                if(first){
                    first = false;
                    continue;
                }
            }
            if(newRow.contains(word.toUpperCase())&& i!=0){
                texts[i] = "<br> " + texts[i];
            }
            if(needsbr) {
                texts[i] = "<br> " + texts[i];
                needsbr = false;
            }


        }
        newString = String.join(" ",texts);
        MainFrame.getInstance().getjEditorPane().setText(newString);
        System.out.println(newString);
    }


    private void init(){
        keywords.add("ADD");
        keywords.add("CONSTRAINT");
        keywords.add("ALL");
        keywords.add("ALTER");
        keywords.add("COLUMN");
        keywords.add("TABLE");
        keywords.add("AND");
        keywords.add("ANY");
        keywords.add("AS");
        keywords.add("ASC");
        keywords.add("BACKUP");
        keywords.add("DATABASE");
        keywords.add("BETWEEN");
        keywords.add("CASE");
        keywords.add("CHECK");
        keywords.add("CREATE");
        keywords.add("INDEX");
        keywords.add("OR");
        keywords.add("REPLACE");
        keywords.add("VIEW");
        keywords.add("PROCEDURE");
        keywords.add("UNIQUE");
        keywords.add("DEFAULT");
        keywords.add("DESC");
        keywords.add("DELETE");
        keywords.add("DISTINCT");
        keywords.add("DROP");
        keywords.add("EXEC");
        keywords.add("EXISTS");
        keywords.add("FOREIGN");
        keywords.add("KEY");
        keywords.add("FROM");
        keywords.add("FULL");
        keywords.add("OUTER");
        keywords.add("JOIN");
        keywords.add("GROUP");
        keywords.add("HAVING");
        keywords.add("IN");
        keywords.add("INNER");
        keywords.add("INSERT");
        keywords.add("INTO");
        keywords.add("IS");
        keywords.add("NULL");
        keywords.add("NOT");
        keywords.add("LEFT");
        keywords.add("LIKE");
        keywords.add("LIMIT");
        keywords.add("OR");
        keywords.add("ORDER");
        keywords.add("PRIMARY");
        keywords.add("RIGHT");
        keywords.add("ROWNUM");
        keywords.add("TOP");
        keywords.add("SET");
        keywords.add("TRUNCATE");
        keywords.add("UNION");
        keywords.add("ALL");
        keywords.add("UPDATE");
        keywords.add("VALUES");
        keywords.add("WHERE");
        keywords.add("SELECT");
        keywords.add("AS");
        keywords.add("BEGIN");
        keywords.add("END");
        keywords.add("OUT");
        keywords.add("FUNCTION");
    }


    private void initRow(){
        newRow.add("ADD");
        newRow.add("ALTER");
        newRow.add("AS");
        newRow.add("BACKUP");
        newRow.add("CASE");
        newRow.add("CHECK");
        newRow.add("CREATE");
        newRow.add("DEFAULT");
        newRow.add("DELETE");
        newRow.add("DROP");
        newRow.add("EXEC");
        newRow.add("FOREIGN");
        newRow.add("FULL");
        newRow.add("GROUP");
        newRow.add("HAVING");
        newRow.add("INNER");
        newRow.add("INSERT");
        newRow.add("IS");
        newRow.add("NOT");
        newRow.add("LIMIT");
        newRow.add("ORDER");
        newRow.add("PRIMARY");
        newRow.add("RIGHT");
        newRow.add("SET");
        newRow.add("TRUNCATE");
        newRow.add("UNION");
        newRow.add("UPDATE");
        newRow.add("VALUES");
        newRow.add("WHERE");
        newRow.add("SELECT");
        newRow.add("AS");
        newRow.add("BEGIN");
        newRow.add("END");
    }
}
