package resource;


public abstract class DBNode {

    private final String name;
    private final DBNode parent;

    public DBNode(String name,DBNode parent){
        this.name = name;
        this.parent = parent;
    }



    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DBNode){
            DBNode other = (DBNode) o;
            return this.getName().equals(other.getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return "DBNode{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

}
