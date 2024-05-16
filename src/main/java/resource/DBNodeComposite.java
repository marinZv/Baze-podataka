package resource;

import java.util.ArrayList;
import java.util.List;

public abstract class DBNodeComposite extends DBNode{

    private List<DBNode> children;

    public DBNodeComposite(String name, DBNode parent){
        super(name,parent);
        this.children = new ArrayList<>();
    }

    public abstract void addChild(DBNode child);

    public DBNode getChildByName(String name){
        for (DBNode child: this.getChildren()){
            if (name.equalsIgnoreCase(child.getName())){
                return child;
            }
        }
        return null;
    }

    public List<DBNode> getChildren() {
        return children;
    }

}
