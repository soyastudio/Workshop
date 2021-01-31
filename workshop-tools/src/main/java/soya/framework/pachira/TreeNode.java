package soya.framework.pachira;

import java.util.List;

public interface TreeNode extends Annotatable {
    String getName();

    TreeNode getParent();

    List<TreeNode> getChildren();

    String getPath();

    void setData(Object data);

    Object getData();

    <T> T getData(Class<T> type);

}
