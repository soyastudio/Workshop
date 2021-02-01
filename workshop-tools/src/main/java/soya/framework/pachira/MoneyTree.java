package soya.framework.pachira;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MoneyTree implements Tree, Annotatable {

    private TreeNode root;
    private Map<String, TreeNode> treeNodeMap;

    private MoneyTree() {
        this.root = new DefaultTreeNode();
        this.treeNodeMap = new LinkedHashMap<>();
    }

    public TreeNode get(String path) {
        return treeNodeMap.get(path);
    }

    public Iterator<String> paths() {
        return treeNodeMap.keySet().iterator();
    }

    public Iterator<TreeNode> nodes() {
        return treeNodeMap.values().iterator();
    }

    public synchronized MoneyTree add(TreeNode parent, String name, Object data) {
        if (parent == null) {
            new TreeNodeBuilder().name(name).parent(root.getPath()).data(data).create(this);
        } else {
            new TreeNodeBuilder().parent(parent.getPath()).name(name).data(data).create(this);
        }

        return this;
    }

    public synchronized MoneyTree add(TreeNodeBuilder builder) {
        builder.create(this);
        return this;
    }

    public synchronized MoneyTree rename(TreeNode node, String newName) {
        TreeNode newNode = new TreeNodeBuilder().name(newName).parent(node.getParent().getPath()).data(node.getData()).create(this);
        node.getChildren().forEach(e -> {
            new Thread(() -> {
                copyTo(e, newNode.getPath());
            }).start();
        });

        remove(node.getPath());

        return this;
    }

    public synchronized MoneyTree copyTo(TreeNode node, String newPath) {
        TreeNode newParent = treeNodeMap.get(newPath);
        if (newParent == null) {
            throw new IllegalArgumentException("Path '" + newPath + "' does not exist.");
        }

        TreeNode newNode = new TreeNodeBuilder().parent(newPath).name(node.getName()).data(node.getData()).create(this);
        // FIXME: do we need copy the annotations?
        node.getChildren().forEach(e -> {
            new Thread(() -> {
                copyTo(e, newNode.getPath());
            }).start();
        });

        return this;
    }

    public synchronized MoneyTree remove(String path) {
        TreeNode node = treeNodeMap.get(path);
        if (node != null) {
            TreeNode parent = node.getParent();
            parent.getChildren().remove(node);

            List<String> paths = new ArrayList<>(treeNodeMap.keySet());
            Collections.sort(paths);
            String prefix = path + "/";
            paths.forEach(e -> {
                if (path.equals(e) || e.startsWith(prefix)) {
                    treeNodeMap.remove(e);
                }
            });
        }

        return this;
    }

    public synchronized MoneyTree move(TreeNode node, String newPath) {
        copyTo(node, newPath);
        remove(node.getPath());
        return this;
    }

    public Cursor cursor() {
        return new Cursor(this);
    }

    @Override
    public void annotate(String namespace, Object annotation) {
        root.annotate(namespace, annotation);
    }

    @Override
    public Object getAnnotation(String namespace) {
        return root.getAnnotation(namespace);
    }

    @Override
    public <T> T getAnnotation(String namespace, Class<T> annotationType) {
        return root.getAnnotation(namespace, annotationType);
    }

    public static MoneyTree newInstance(String name) {
        MoneyTree tree = new MoneyTree();
        DefaultTreeNode rootNode = (DefaultTreeNode) tree.root;

        rootNode.name = name;
        rootNode.path = name;

        tree.treeNodeMap.put(rootNode.path, rootNode);

        return tree;
    }

    public static TreeNodeBuilder treeNodeBuilder() {
        return new TreeNodeBuilder();
    }

    static class TreeNodeBuilder {
        private String name;
        private String parent;
        private Object data;

        private TreeNodeBuilder() {
        }

        public TreeNodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TreeNodeBuilder parent(String parent) {
            this.parent = parent;
            return this;
        }

        public TreeNodeBuilder data(Object data) {
            this.data = data;
            return this;
        }

        private TreeNode create(MoneyTree tree) {
            TreeNode parentNode = tree.get(parent);
            if (tree.get(parent) == null) {
                throw new IllegalArgumentException("Path '" + parent + "' does not exist.");
            }

            String path = parent + "/" + name;
            if (tree.get(path) != null) {
                throw new IllegalArgumentException("Path '" + path + "' already exist.");
            }

            DefaultTreeNode node = new DefaultTreeNode();
            node.name = name;
            node.path = path;
            node.data = data;

            node.parent = parentNode;
            parentNode.getChildren().add(node);

            tree.treeNodeMap.put(path, node);

            return node;
        }

    }

    static class DefaultTreeNode implements TreeNode {

        private TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();

        private String name;
        private String path;

        private Object data;
        private Map<String, Object> annotations = new LinkedHashMap<>();

        @Override
        public String getName() {
            return name;
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public List<TreeNode> getChildren() {
            return children;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public Object getData() {
            return data;
        }

        @Override
        public <T> T getData(Class<T> type) {
            return (T) data;
        }


        @Override
        public void annotate(String namespace, Object annotation) {
            this.annotations.put(namespace, annotation);
        }

        @Override
        public Object getAnnotation(String namespace) {
            return annotations.get(namespace);
        }

        @Override
        public <T> T getAnnotation(String namespace, Class<T> annotationType) {
            return (T) annotations.get(namespace);
        }

    }

    static class Cursor {
        private MoneyTree tree;
        private List<String> paths;

        private TreeNode current;


        private Cursor(MoneyTree tree) {
            this.tree = tree;
            this.paths = new ArrayList<>(tree.treeNodeMap.keySet());
        }
    }

}
