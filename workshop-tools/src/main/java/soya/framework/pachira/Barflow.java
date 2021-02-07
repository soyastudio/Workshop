package soya.framework.pachira;

public interface Barflow<T extends Baseline> {

    T getBaseLine();

    Barflow<T> baseline(BaselineBuilder<T> builder) throws FlowBuilderException;

    Barflow<T> annotator(AnnotatorBuilder builder) throws FlowBuilderException;

    Barflow<T> renderer(RendererBuilder builder) throws FlowBuilderException;


    interface BaselineBuilder<T> {
        BaselineBuilder<T> digester(Digester<?, ?> digester);

        T create() throws FlowBuilderException;
    }

    interface Digester<S, D> {
        D digest(S origin);
    }

    interface Annotator<T> {
        void annotate(T baseline) throws FlowExecutionException;
    }

    interface Renderer<T> {
        String getName();

        String render(T baseline) throws FlowExecutionException;
    }

    interface AnnotatorBuilder {
        String getName();

        AnnotatorBuilder name(String name);

        Annotator<?> create(Configuration configuration) throws FlowBuilderException;

    }

    interface RendererBuilder {
        String getName();

        RendererBuilder name(String name);

        Renderer<?> create(Configuration configuration) throws FlowBuilderException;

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
