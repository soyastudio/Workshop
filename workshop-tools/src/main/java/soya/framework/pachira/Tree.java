package soya.framework.pachira;

import java.util.Iterator;

public interface Tree {

    TreeNode get(String path);

    Iterator<String> paths();

    Iterator<TreeNode> nodes();

    MoneyTree add(TreeNode parent, String name, Object data);

    MoneyTree rename(TreeNode node, String newName);

    MoneyTree copyTo(TreeNode node, String newPath);

    MoneyTree remove(String path);

    MoneyTree move(TreeNode node, String newPath);
}
