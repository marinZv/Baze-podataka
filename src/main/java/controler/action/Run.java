package controler.action;

import controler.AbstractBaZeControler;
import gui.MainFrame;

import java.awt.event.ActionEvent;

public class Run extends AbstractBaZeControler {
    String iconString ="src/main/resources/play.png";

    public Run(){
        putValue(SMALL_ICON,loadIcon(iconString));
        putValue(NAME,"Run");
        putValue(SHORT_DESCRIPTION,"Compile and run");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String query = whatSQL(MainFrame.getInstance().getjEditorPane().getText());
            MainFrame.getInstance().getAppCore().run(query);
        }catch (Exception er){
            er.printStackTrace();
        }
    }

    public String whatSQL(String s){
        boolean ignored = false;
        String cleanSQL = "";
        for(int i=0 ; i< s.length();i++){
            if(s.charAt(i) == "<".charAt(0))
                ignored = true;
            if(s.charAt(i) == "\\".charAt(0))
                ignored = true;
            if(i>4)
                if(s.charAt(i-4) == "<".charAt(0) && s.charAt(i-3) == "b".charAt(0) && s.charAt(i-2) == "r".charAt(0) && s.charAt(i-1) == ">".charAt(0))
                    cleanSQL += " ";
            if(!ignored)
                cleanSQL += s.charAt(i);
            if(i>0)
                if(s.charAt(i-1) == "\\".charAt(0))
                    ignored =false;
            if(s.charAt(i) == ">".charAt(0))
                ignored = false;
        }
        //return cleanSQL;
        String[] strings = cleanSQL.split("\\n");
        String t1 = String.join("",strings);
        String[] t2 = t1.split("\\r");
        String t3 ="";
        String[] t4 = String.join("",t2).split(" ");
        for(String string : t4){
            if(!(string.equalsIgnoreCase("")||string.equalsIgnoreCase(" "))) {
                    t3 += string;
                    t3 += " ";

            }
        }
        t3 = t3.replaceAll("&gt;",">");
        t3 = t3.replaceAll("&lt;","<");
        return t3.replaceAll("&quot;","\"");
    }


    }

