package soya.framework.pachira;

import java.util.Iterator;
import java.util.Set;

public interface Tree {
    TreeNode root();

    TreeNode create(TreeNode parent, String name, Object data);

    TreeNode get(String path);

    Tree rename(TreeNode node, String newName);

    Tree add(TreeNode parent, String name, Object data);

    Tree copyTo(TreeNode node, String newPath);

    Tree move(TreeNode node, String newPath);

    Tree remove(String path);

    //
    Iterator<String> paths();

    Iterator<TreeNode> nodes();

    Set<TreeNode> find(Selector selector);

    Tree filterIn(Selector selector);

    Tree filterOut(Selector selector);

    interface Selector {
        Set<TreeNode> select();
    }
}
