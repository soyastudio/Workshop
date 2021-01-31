package soya.framework.pachira;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MoneyTree implements Annotatable {

    private TreeNode root;
    private Map<String, TreeNode> treeNodeMap;

    private MoneyTree() {
        this.root = new DefaultTreeNode();
        this.treeNodeMap = new ConcurrentHashMap<>();
    }

    public TreeNode get(String path) {
        return treeNodeMap.get(path);
    }

    public synchronized MoneyTree add(TreeNode parent, String name, Object data) {
        if(parent == null) {
            new TreeNodeBuilder().name(name).data(data).create(this);
        } else {
            new TreeNodeBuilder().parent(parent.getPath()).name(name).data(data).create(this);
        }
        return this;
    }

    public synchronized MoneyTree add(TreeNodeBuilder builder) {
        builder.create(this);
        return this;
    }

    public synchronized MoneyTree remove(String path) {
        TreeNode node = treeNodeMap.get(path);
        if(node != null) {
            TreeNode parent = node.getParent();
            parent.getChildren().remove(node);

            List<String> paths = new ArrayList<>(treeNodeMap.keySet());
            Collections.sort(paths);
            String prefix = path + "/";
            paths.forEach(e -> {
                if(path.equals(e) || e.startsWith(prefix)) {
                    treeNodeMap.remove(e);
                }
            });
        }

        return this;
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
        private TreeNode root;
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

}
