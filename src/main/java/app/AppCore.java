package app;

import checkPack.Checker;
import checkPack.ErrorHandler;
import database.Database;
import database.DatabaseImplementation;
import database.MYSQLrepository;
import database.settings.Settings;
import database.settings.SettingsImplementation;
import gui.MainFrame;
import gui.table.TableModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import observer.Notification;
import observer.Subscriber;
import observer.enums.NotificationCode;
import observer.implementatios.PublisherImplementation;
import resource.data.Row;
import resource.implementations.InformationResource;
import tree.Tree;
import tree.implementation.TreeImplementation;
import utils.Constants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Data
@Getter
@Setter
public class AppCore extends PublisherImplementation {

    private Database database;
    private Settings settings;
    private TableModel tableModel;
    private DefaultTreeModel defaultTreeModel;
    private Tree tree;
    private Checker checker;
    //MYSQLrepository mysqLrepository = new MYSQLrepository(this.settings);

    public AppCore(){
        this.settings = initSettings();
        this.database = new DatabaseImplementation(new MYSQLrepository(this.settings));
        this.tableModel = new TableModel();
        this.tree = new TreeImplementation();
        this.checker = new Checker();
    }

    private Settings initSettings(){
        /*
        inicijalizacije tako sto izvlacimo podatke iz SettingsImplementation klase
         */
        SettingsImplementation settingsImplementation = new SettingsImplementation();
        settingsImplementation.addParameter("mysql_ip", Constants.MYSQL_IP);
        settingsImplementation.addParameter("mysql_database", Constants.MYSQL_DATABASE);
        settingsImplementation.addParameter("mysql_username", Constants.MYSQL_USERNAME);
        settingsImplementation.addParameter("mysql_password", Constants.MYSQL_PASSWORD);

        return settingsImplementation;
    }

    public DefaultTreeModel loadResource(){
        /*
        ucitavanje podataka iz baze u Jtree
         */
        InformationResource ir = (InformationResource) this.database.loadResource();
        return tree.generateTree(ir);

    }

    public void readDataFromTable(String fromTable){
        /*
        ucitavanje u redove tabele
         */
        List<Row> rows = null;
        try {
            if((rows =this.database.readDataFromTable(fromTable)) != null)
        tableModel.setRows(rows);
        /*
            List<Row> rows = this.database.readDataFromTable(fromTable);
            if (rows != null)
                tableModel.setRows(rows);

         */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String query){
        /*
        qretys je niz stringova koji moze imati vise elemenata u zavisnosti da li ima podupite
        ukoliko ima podupite svaki podupit se proverava posebno da li su zadovoljeni svi zahtevi
         */
        String[] querys = imalipodupita(query);
        int querint = querys.length;
        System.out.println(querys[0]);
        /*
        checker je stack na kome se proverava redom da li su ispunjena sva pravila za pisanje querry
         */
        checker.check(querys[0]);
        /*
        dokle god ima podupita on ce prolaziti
         */
        while (querint != 1) {
            querys = imalipodupita(querys[1]);
            querint = querys.length;
            String next = querys[0];
            System.out.println(next);
            checker.check(next);
        }
        String message = "<html>";
        /*
        ukoliko je checker pao negde tj imao greske pisao je u array Errors
        formatirano je kao html i ispisujemo sve greske u Joptionpane
         */
        if(!checker.getErrors().isEmpty()) {
            ArrayList<String> strings = checker.errorMessage(checker.getErrors());
            int i = 0;
            for(String s:strings){
                if(i == 0){
                    message += s;
                    i++;
                } else {
                    message += "<br>" + s;
                }
            }
            message = message+ "</html>";
            notifySubscribers(new Notification(NotificationCode.ERROR_HAPPENED,message));
            checker.getErrors().clear();
        } else {
            readDataFromTable(query);
        }
    }
/*
runBulk nam proverava kada importujemo csv fajl da li je fajl u dobrom formatu
i da li se sve iz njega moze ubaciti u bazu
 */
    public void runBulk(String head,ArrayList<String> querys){
        checker.checkCSV(head,querys);
        String message = "<html>";
        if(!checker.getErrors().isEmpty())
            for(String q : querys){
                readDataFromTable(q);
            } else {
            ArrayList<String> strings = checker.errorMessage(checker.getErrors());
            int i = 0;
            for(String s:strings){
                if(i == 0){
                    message += s;
                    i++;
                } else {
                    message += "<br>" + s;
                }
            }
            message = message+ "</html>";
            notifySubscribers(new Notification(NotificationCode.ERROR_HAPPENED,message));
            checker.getErrors().clear();
        }
    }

    private String[] imalipodupita(String query){
        boolean hasWhere = false;
        int separateQueryint = 0;
        boolean hasSelect = false;
        /*
        splitujemo upit pod " " proveravamo da li ima Where ukoliko smo nasli na where
        i da 3 mesta nakon toga se nalazi nesto onda delimo to na podupite
         */
        String[] separateQuery = query.split(" ");
        for(int i = 0; i< separateQuery.length; i++) {
            if (separateQuery[i].equalsIgnoreCase("WHERE") && !hasWhere) {
                hasWhere = true;
                separateQueryint = i + 3;
            }
        }
        String newString = "";
        String oldString = "";

        if(separateQueryint != 0) {

            for (int k = 0; k < separateQueryint - 1; k++) {
                /*
                if (separateQuery[k].startsWith("(") && separateQuery[k].length() != 1)
                    separateQuery[k] = separateQuery[k].substring(1);
                if (separateQuery[k].endsWith(")") && separateQuery[k].length() != 1)
                    separateQuery[k] = separateQuery[k].substring(0, separateQuery[k].length() - 1);
                if (separateQuery[k].startsWith("(") && separateQuery[k].length() == 1)
                    continue;
                if (separateQuery[k].endsWith(")") && separateQuery[k].length() == 1)
                    continue;
                */
                if (k != 0)
                    oldString += " ";
                oldString += separateQuery[k];
            }

            for (int j = separateQueryint; j < separateQuery.length; j++) {
                if (separateQuery[j].startsWith("(") && separateQuery[j].length() == 1)
                    continue;
                if (separateQuery[j].endsWith(")") && separateQuery[j].length() == 1)
                    continue;
                if (j != separateQueryint)
                    newString += " ";
                newString += separateQuery[j];
                if(!hasSelect)
                    if(!(separateQuery[j].equalsIgnoreCase("SELECT")||separateQuery[j].equalsIgnoreCase("(SELECT"))) {
                        String[] retStr = new String[1];
                        retStr[0] = String.join(" ",separateQuery);
                        return retStr;
                    } else {
                        hasSelect = true;
                    }
            }
            newString = newString.substring(1,newString.length()-1);
            String[] retString = new String[2];
            retString[0] = oldString;
            retString[1] = newString;
            return retString;
        } else {
            String[] retStr = new String[1];
            retStr[0] = String.join(" ",separateQuery);
            return retStr;
        }
    }


}
