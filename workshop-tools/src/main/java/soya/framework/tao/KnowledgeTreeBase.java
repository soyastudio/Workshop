package soya.framework.tao;

public interface KnowledgeTreeBase<T, K extends KnowledgeTree> extends Baseline<T, K> {

    interface KnowledgeTreeAnnotation extends Barflow.Annotator<KnowledgeTree> {

    }

    interface KnowledgeTreeBaseRenderer extends Barflow.Renderer<KnowledgeTree> {

    }

}
