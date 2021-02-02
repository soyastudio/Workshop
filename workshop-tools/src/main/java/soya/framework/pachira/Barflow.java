package soya.framework.pachira;

public interface Barflow<T> {

    Barflow<T> baseline(BaselineBuilder<T> builder);

    Barflow<T> annotator(AnnotatorBuilder builder);

    Barflow<T> renderer(RendererBuilder builder);




    static interface BaselineBuilder<T> {
        T create();

    }

    static interface AnnotatorBuilder {
        String getName();

        AnnotatorBuilder name(String name);

        Annotator<?> create(Configuration configuration);

    }

    static interface RendererBuilder {
        String getName();

        RendererBuilder name(String name);

        Renderer<?> create(Configuration configuration);

    }

    static interface Configuration {}

    static interface Annotator<T> {
        void annotate(T base);
    }

    static interface Renderer<T> {

    }

}
