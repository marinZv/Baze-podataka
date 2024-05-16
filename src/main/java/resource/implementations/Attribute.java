package resource.implementations;

import resource.DBNode;
import resource.DBNodeComposite;
import resource.enums.AttributeType;


public class Attribute extends DBNodeComposite {

    private final AttributeType attributeType;
    private final int lenght;
    private Attribute inRelationWith;

    public Attribute(String name, DBNode parent,AttributeType attributeType,int lenght){
        super(name, parent);
        this.attributeType = attributeType;
        this.lenght = lenght;
    }

    @Override
    public void addChild(DBNode child) {
        if(child != null && child instanceof AttributeConstraint){
            AttributeConstraint attributeConstraint = (AttributeConstraint) child;
            this.getChildren().add(attributeConstraint);
        }
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }
}
