package soya.framework.tao;

/**
 * <p>
 *     道生一，一生二，二生三，三生万物。 万物负阴而抱阳，冲气以为和。
 *     人之所恶，唯孤、寡、不谷，而王公以为称。故物或损之而益，或益之而损。人之所教，我亦教之。强梁者不得其死，吾将以为教父。
 * </p>
 * <p>
 *     道生一，在我们这里解读为：通过对道，或者客观事物的感知，学习，实践等而形成认知体系：知识。
 *     一生二，二生三，三生万物，在我们这里解读为这样一个作用过程，就是对认知体系的消化，注释与展示，从而呈现丰富的现像。其中，
 *     消化：是指将认知体系分解为相互关联子系统，直至相对独立的组件；
 *     注释：是指基于已有的知识或规律，对某一知识体系或其组成部分进行注释，对应于阴阳中阴；
 *     展示：是指用什么方式将事物本身及其附属属性呈现出来，对应于阴阳中的阳
 * </p>
 * @author Wen Qun
 *
 */
public interface T123W<T, K extends Annotatable> {

    T123W<T, K> baseline(KnowledgeBuilder<T, K> builder) throws FlowBuilderException;

    T123W<T, K> annotator(AnnotatorBuilder<K> builder) throws FlowBuilderException;

    T123W<T, K> renderer(RendererBuilder builder) throws FlowBuilderException;

    interface KnowledgeBuilder<T, K extends Annotatable> {
        KnowledgeBase<T, K> create() throws FlowBuilderException;
    }

    interface Annotator<K extends Annotatable> {
        void annotate(K knowledge) throws FlowExecutionException;
    }

    interface Renderer<K extends Annotatable> {
        String render(K knowledge) throws FlowExecutionException;
    }

    interface AnnotatorBuilder<K extends Annotatable> {
        String getName();

        Class<?> getType();

        AnnotatorBuilder<K> name(String name);

        AnnotatorBuilder<K> annotatorType(Class<? extends Annotator> type);

        AnnotatorBuilder<K> configure(Configuration configuration);

        Annotator<K> create() throws FlowBuilderException;

        Annotator<K> create(Configuration configuration) throws FlowBuilderException;

    }

    interface RendererBuilder<K extends Annotatable> {
        String getName();

        Class<?> getType();

        RendererBuilder<K> name(String name);

        RendererBuilder<K> rendererType(Class<? extends Renderer> type);

        RendererBuilder<K> configure(Configuration configuration);

        Renderer<K> create() throws FlowBuilderException;

        Renderer<K> create(Configuration configuration) throws FlowBuilderException;

    }

    interface Configuration {
    }

    class FlowException extends RuntimeException {

        public FlowException(Throwable cause) {
            super(cause);
        }

        public FlowException(String message) {
            super(message);
        }

        public FlowException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class FlowBuilderException extends FlowException {
        public FlowBuilderException(Throwable cause) {
            super(cause);
        }

        public FlowBuilderException(String message) {
            super(message);
        }

        public FlowBuilderException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class FlowExecutionException extends FlowException {
        public FlowExecutionException(Throwable cause) {
            super(cause);
        }

        public FlowExecutionException(String message) {
            super(message);
        }

        public FlowExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
