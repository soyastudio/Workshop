package soya.framework.tao;

import java.util.List;

public interface TreeNode {

    String getName();

    String getPath();

    TreeNode getParent();

    List<TreeNode> getChildren();

    void setData(Object data);

    Object getData();

    <T> T getData(Class<T> type);



}
