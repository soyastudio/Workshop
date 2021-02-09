package soya.framework.tao;

public interface Baseline<O, K extends Annotatable> {
    O origin();

    K knowledgeBase();
}
