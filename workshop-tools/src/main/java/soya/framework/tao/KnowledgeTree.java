package soya.framework.tao;

public interface KnowledgeTree<K, T> extends Tree<KnowledgeTreeNode<T>>, Annotatable<K> {

    interface KnowledgeTreeBuilder<K, T> {
        KnowledgeTreeBuilder<K, T> knowledgeBase(K knowledgeBase);

        KnowledgeTreeBuilder<K, T> knowledgeDigester(KnowledgeDigester<K, T> digester);

        KnowledgeTree<K, T> create();
    }

    interface KnowledgeDigester<K, T> {
        KnowledgeTree<K, T> digester(K knowledgeBase);
    }

}
