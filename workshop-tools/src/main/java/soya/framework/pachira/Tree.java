package soya.framework.pachira;

import java.util.Iterator;
import java.util.Set;

public interface Tree<N extends TreeNode> {
    N root();

    N create(TreeNode parent, String name, Object data);

    N get(String path);

    Tree<N> rename(TreeNode node, String newName);

    Tree<N> add(TreeNode parent, String name, Object data);

    Tree<N> copyTo(TreeNode node, String newPath);

    Tree<N> move(TreeNode node, String newPath);

    Tree remove(String path);

    //
    Iterator<String> paths();

    Iterator<N> nodes();

    Set<N> find(Selector selector);

    Tree filterIn(Selector selector);

    Tree filterOut(Selector selector);

    interface Selector {
        Set<TreeNode> select();
    }
}
