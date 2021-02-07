package soya.framework.pachira;

public interface Baseline<T, K> extends Annotatable<T> {
    K knowledgeBase();
}
