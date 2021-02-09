package soya.framework.tao;

import java.util.Iterator;
import java.util.Set;

public interface Tree<N extends TreeNode> {
    N root();

    N create(TreeNode parent, String name, Object data);

    boolean contains(String path);

    N get(String path);

    Tree<N> rename(TreeNode node, String newName);

    Tree<N> add(TreeNode parent, String name, Object data);

    Tree<N> copyTo(TreeNode node, String newPath);

    Tree<N> move(TreeNode node, String newPath);

    Tree remove(String path);

    //
    Iterator<String> paths();

    Iterator<N> nodes();

    Set<N> find(Selector<N> selector);

    Tree<N> filterIn(Selector<N> selector);

    Tree<N> filterOut(Selector<N> selector);

    interface Selector<N> {
        Set<N> select();
    }
}
