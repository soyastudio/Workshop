package soya.framework.tao;

/**
 * <p>
 *     道生一，一生二，二生三，三生万物。
 *     万物负阴而抱阳，冲气以为和。
 *     人之所恶，唯孤、寡、不谷，而王公以为称。故物或损之而益，或益之而损。人之所教，我亦教之。强梁者不得其死，吾将以为教父。
 * </p>
 *
 * @author Wen Qun
 *
 */
public interface T123W<O, K extends Annotatable> {

    T123W<O, K> baseline(BaselineBuilder<O, K> builder) throws FlowBuilderException;

    T123W<O, K> annotator(AnnotatorBuilder<K> builder) throws FlowBuilderException;

    T123W<O, K> renderer(RendererBuilder builder) throws FlowBuilderException;

    interface BaselineBuilder<O, K extends Annotatable> {
        // BaselineBuilder<O, K> extractor(Extractor<O> extractor);

        // BaselineBuilder<O, K> digester(Digester<O, K> digester);

        Baseline<O, K> create() throws FlowBuilderException;
    }

    interface Extractor<O> {
        O extract() throws FlowBuilderException;
    }

    interface Digester<O, K> {
        K digest(O origin) throws FlowBuilderException;
    }

    interface Annotator<K extends Annotatable> {
        void annotate(K knowlegeBase) throws FlowExecutionException;
    }

    interface Renderer<K extends Annotatable> {
        String render(K knowledgeBase) throws FlowExecutionException;
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
