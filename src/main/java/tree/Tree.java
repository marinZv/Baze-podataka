package tree;

import resource.implementations.InformationResource;

import javax.swing.tree.DefaultTreeModel;

public interface Tree {

     DefaultTreeModel generateTree(InformationResource informationResource);

}
