package soya.framework.pachira;

public interface Barflow<T> {

    Barflow<T> baseline(BaselineBuilder<T> builder) throws FlowBuilderException;

    Barflow<T> annotator(AnnotatorBuilder builder) throws FlowBuilderException;

    Barflow<T> renderer(RendererBuilder builder) throws FlowBuilderException;

    static interface BaselineBuilder<T> {
        T create() throws FlowBuilderException;
    }

    static interface AnnotatorBuilder {
        String getName();

        AnnotatorBuilder name(String name);

        Annotator<?> create(Configuration configuration) throws FlowBuilderException;

    }

    static interface RendererBuilder {
        String getName();

        RendererBuilder name(String name);

        Renderer<?> create(Configuration configuration) throws FlowBuilderException;

    }

    static interface Configuration {
    }

    static interface Annotator<T> {
        void annotate(T base) throws FlowExecutionException;
    }

    static interface Renderer<T> {
        String render(T base) throws FlowExecutionException;
    }

    static class FlowException extends RuntimeException {

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

    static class FlowBuilderException extends FlowException {
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

    static class FlowExecutionException extends FlowException {
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
