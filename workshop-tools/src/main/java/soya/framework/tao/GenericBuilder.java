package soya.framework.tao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GenericBuilder<T, B> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected String name;
    protected Class<? extends T> type;
    protected Barflow.Configuration configuration;

    protected GenericBuilder() {
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public B name(String name) {
        this.name = name;
        return (B) this;
    }

    protected B type(Class<? extends T> type) {
        this.type = type;
        return (B) this;
    }

    public B configure(Barflow.Configuration configuration) {
        this.configuration = configuration;
        return (B) this;
    }

    public T create() {
        return null;
    }

    public T create(Barflow.Configuration configuration) {
        return null;
    }

    public static Barflow.AnnotatorBuilder annotatorBuilder() {
        return new GenericAnnotatorBuilder();
    }

    public static Barflow.RendererBuilder rendererBuilder() {
        return new GenericRendererBuilder();
    }


    public static class GenericAnnotatorBuilder extends GenericBuilder<Barflow.Annotator, Barflow.AnnotatorBuilder> implements Barflow.AnnotatorBuilder {
        protected GenericAnnotatorBuilder() {
        }


        @Override
        public Barflow.AnnotatorBuilder annotatorType(Class type) {
            return super.type(type);
        }
    }

    public static class GenericRendererBuilder extends GenericBuilder<Barflow.Renderer, Barflow.RendererBuilder> implements Barflow.RendererBuilder {
        protected GenericRendererBuilder() {
        }

        @Override
        public Barflow.RendererBuilder rendererType(Class type) {
            return super.type(type);
        }
    }
}
