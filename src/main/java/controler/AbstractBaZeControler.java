package controler;


import javax.swing.*;
import java.awt.*;

public abstract class AbstractBaZeControler extends AbstractAction {

    public Icon loadIcon(String filename){
        Image icon;
        Icon littleicon = null;
        if(filename != null){
            icon = new ImageIcon(filename).getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
            littleicon = new ImageIcon(icon);
        } else  {
            System.out.println("Image not found"+filename);
        }
    return littleicon;
    }
}
