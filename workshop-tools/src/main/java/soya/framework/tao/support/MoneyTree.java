package soya.framework.tao.support;

import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.Tree;
import soya.framework.tao.TreeNode;

import java.util.*;

public class MoneyTree<K, T> extends Feature<K> implements KnowledgeTree<K, T> {

    private KnowledgeTreeNode root;
    private Map<String, KnowledgeTreeNode<T>> treeNodeMap;

    protected MoneyTree(K knowledge, DefaultTreeNode<T> root) {
        super(knowledge);
        this.root = root;
        this.treeNodeMap = new LinkedHashMap<>();
        treeNodeMap.put(root.path, root);

    }

    @Override
    public KnowledgeTreeNode<T> root() {
        return root;
    }

    @Override
    public KnowledgeTreeNode<T> create(TreeNode parent, String name, Object data) {
        DefaultTreeNode node = new DefaultTreeNode(parent, name, data);
        treeNodeMap.put(node.path, node);
        return node;
    }

    @Override
    public boolean contains(String path) {
        return treeNodeMap.containsKey(path);
    }

    public KnowledgeTreeNode<T> get(String path) {
        return treeNodeMap.get(path);
    }

    public Iterator<String> paths() {
        return treeNodeMap.keySet().iterator();
    }

    public Iterator<KnowledgeTreeNode<T>> nodes() {
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

    @Override
    public Set<KnowledgeTreeNode<T>> find(Selector selector) {
        return selector.select();
    }

    @Override
    public Tree filterIn(Selector selector) {
        return null;
    }

    @Override
    public Tree filterOut(Selector selector) {
        return null;
    }

    public synchronized MoneyTree move(TreeNode node, String newPath) {
        copyTo(node, newPath);
        remove(node.getPath());
        return this;
    }

    public static <K, T> MoneyTree<K, T> newInstance(K k, String name, T t) {
        return new MoneyTree<>(k, new DefaultTreeNode<T>(null, name, t));
    }

    public static <K, T> KnowledgeTreeBuilder<K, T> builder() {
        return new MoneyTreeBuilder<K, T>();
    }

    public static TreeNodeBuilder treeNodeBuilder() {
        return new TreeNodeBuilder();
    }

    static class MoneyTreeBuilder<K, T> implements KnowledgeTreeBuilder<K, T>{
        private K knowledgeBase;
        private KnowledgeDigester<K, T> knowledgeDigester;

        private MoneyTreeBuilder() {
        }

        @Override
        public KnowledgeTreeBuilder<K, T> knowledgeBase(K knowledgeBase) {
            this.knowledgeBase = knowledgeBase;
            return this;
        }

        @Override
        public KnowledgeTreeBuilder<K, T> knowledgeDigester(KnowledgeDigester<K, T> digester) {
            this.knowledgeDigester = digester;
            return this;
        }

        @Override
        public KnowledgeTree<K, T> create() {
            return new MoneyTree(knowledgeBase, (DefaultTreeNode) knowledgeDigester.digester(knowledgeBase));
        }
    }

    public static class TreeNodeBuilder {
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

            DefaultTreeNode node = new DefaultTreeNode(parentNode, name, data);
            parentNode.getChildren().add(node);

            tree.treeNodeMap.put(path, node);

            return node;
        }

    }

    static class DefaultTreeNode<T> extends Feature<T> implements KnowledgeTreeNode<T> {

        private TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();

        private String name;
        private String path;

        private Object data;
        private Map<String, Object> annotations = new LinkedHashMap<>();

        protected DefaultTreeNode(TreeNode parent, String name, T data) {
            super(data);
            this.parent = parent;
            this.name = name;

            if (parent != null) {
                parent.getChildren().add(this);
                this.path = parent.getPath() + "/" + name;
            } else {
                this.path = name;
            }
        }

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

    }

}
