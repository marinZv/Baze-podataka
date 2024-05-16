package resource.implementations;

import resource.DBNode;
import resource.enums.ConstraintType;

public class AttributeConstraint extends DBNode {
 private final ConstraintType constraintType;

 public AttributeConstraint(String name,DBNode parent , ConstraintType constraintType){
     super(name, parent);
     this.constraintType = constraintType;
 }

    public ConstraintType getConstraintType() {
        return constraintType;
    }
}
