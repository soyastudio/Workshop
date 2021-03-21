package soya.framework.tao;

import java.util.List;

public interface TreeNode {

    String getName();

    TreeNode getParent();

    List<? extends TreeNode> getChildren();

    String getPath();

    Object getData();

}
