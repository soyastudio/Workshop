package soya.framework.tao;

public interface KnowledgeBase<T, K extends Annotatable> {
    T tao();

    K knowledge();
}
