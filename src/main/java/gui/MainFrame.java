package gui;

import app.AppCore;
import controler.ActionManager;
import observer.Notification;
import observer.Subscriber;
import observer.enums.NotificationCode;
import tree.implementation.SelectionListener;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class MainFrame extends JFrame implements Subscriber {

    private static MainFrame instance;
    private AppCore appCore;
    private JTable jTable;
    private JScrollPane scrollPaneTop;
    private JScrollPane scrollPaneBot;
    private JScrollPane scrollPaneTree;
    private JSplitPane splitPaneTopBot;
    private JSplitPane splitPaneLeftRight;
    private JEditorPane jEditorPane;
    private MyToolBar toolBar;
    private JTree jTree;
    private ActionManager actionManager;

    public static MainFrame getInstance() {
        if(instance == null){
            instance = new MainFrame();
            instance.init();
        }
        return instance;
    }

    private void init(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            //NO NEED TO HANDLE
        }

        actionManager = new ActionManager();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        setSize(screenWidth*2/3,screenHeight*2/3);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Projekat");

        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(500,300));
        jTable.setFillsViewportHeight(true);

        jTree = new JTree();
        scrollPaneTree= new JScrollPane(jTree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        scrollPaneTop = new JScrollPane(jEditorPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneBot = new JScrollPane(jTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        splitPaneTopBot = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPaneTop,scrollPaneBot);
        scrollPaneTop.setMinimumSize(new Dimension(200,200));
        scrollPaneBot.setMinimumSize(new Dimension(200,200));

        splitPaneTopBot.setDividerLocation(250);
        splitPaneTopBot.setOneTouchExpandable(true);

        splitPaneLeftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollPaneTree,splitPaneTopBot);
        scrollPaneTree.setMinimumSize(new Dimension(200,200));

        add(splitPaneLeftRight,BorderLayout.CENTER);
        toolBar = new MyToolBar();
        add(toolBar,BorderLayout.NORTH);
        this.setVisible(true);


    }

    private void initTree(){
        DefaultTreeModel defaultTreeModel = appCore.loadResource();
        jTree.setModel(defaultTreeModel);
        jTree.addTreeSelectionListener(new SelectionListener());
    }

    @Override
    public void update(Notification notification) {
        if(notification.getNotificationCode() == NotificationCode.ERROR_HAPPENED)
            JOptionPane.showMessageDialog(null,notification.getData().toString() , "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.appCore.addSubscriber(this);
        this.jTable.setModel(appCore.getTableModel());
        initTree();
    }

    public JTree getjTree() {
        return jTree;
    }

    public AppCore getAppCore() {
        return appCore;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public JEditorPane getjEditorPane() {
        return jEditorPane;
    }
}
