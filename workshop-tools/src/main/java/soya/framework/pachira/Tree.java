package soya.framework.pachira;

import java.util.Iterator;
import java.util.Set;

public interface Tree {

    Iterator<String> paths();

    Iterator<TreeNode> nodes();

    TreeNode get(String path);

    Tree add(TreeNode parent, String name, Object data);

    Tree rename(TreeNode node, String newName);

    Tree copyTo(TreeNode node, String newPath);

    Tree move(TreeNode node, String newPath);

    Tree remove(String path);

    Set<TreeNode> find(Selector selector);

    Tree filterIn(Selector selector);

    Tree filterOut(Selector selector);

    interface Selector {
        Set<TreeNode> select();
    }
}
