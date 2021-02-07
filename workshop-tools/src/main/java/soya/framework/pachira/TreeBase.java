package soya.framework.pachira;

public interface TreeBase<T, K extends KnowledgeTree> extends Baseline<T, K> {
    K knowledgeBase();
}
