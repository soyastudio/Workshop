package soya.framework.pachira;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

public class PachiraBarFlow implements Barflow<MoneyTree> {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private MoneyTree base;
    private LinkedHashMap<String, AnnotatorBuilder> annotatorBuilders = new LinkedHashMap<>();
    private LinkedHashMap<String, RendererBuilder> rendererBuilders = new LinkedHashMap<>();

    @Override
    public PachiraBarFlow baseline(BaselineBuilder<MoneyTree> builder) {
        this.base = builder.create();
        return this;
    }

    @Override
    public PachiraBarFlow annotator(AnnotatorBuilder builder) {
        annotatorBuilders.put(builder.getName(), builder);
        return this;
    }

    @Override
    public PachiraBarFlow renderer(RendererBuilder builder) {
        rendererBuilders.put(builder.getName(), builder);
        return this;
    }

    public PachiraBarFlow annotator(String name, Class<? extends Annotator> annotatorType, String json) {
        annotatorBuilders.put(name, new GenericAnnotatorBuilder().type(annotatorType).fromJson(json));
        return this;
    }

    public PachiraBarFlow renderer(String name, Class<? extends Renderer> rendererType, String json) {
        rendererBuilders.put(name, new GenericRendererBuilder().type(rendererType).fromJson(json));
        return this;
    }

    //
    public static PachiraBarFlow fromYaml(String yaml) {
        return null;
    }

    public static PachiraBarFlow fromJson(String yaml) {
        return null;
    }

    public static PachiraBarFlow fromXml(String yaml) {
        return null;
    }

    public class TreeBaseBuilder implements BaselineBuilder<MoneyTree> {

        @Override
        public MoneyTree create() {
            return null;
        }
    }

    public class GenericAnnotatorBuilder extends GenericBuilder<Annotator> implements AnnotatorBuilder {

        @Override
        public GenericAnnotatorBuilder name(String name) {
            return (GenericAnnotatorBuilder) _name(name);
        }

        public GenericAnnotatorBuilder type(Class<? extends Annotator> annotatorType) {
            return (GenericAnnotatorBuilder) _type(annotatorType);
        }

        public GenericAnnotatorBuilder fromJson(String json) {
            return (GenericAnnotatorBuilder) _fromJson(json);
        }
    }

    public class GenericRendererBuilder extends GenericBuilder<Renderer> implements RendererBuilder {

        @Override
        public RendererBuilder name(String name) {
            return (RendererBuilder) _name(name);
        }

        public GenericRendererBuilder type(Class<? extends Renderer> rendererType) {
            return (GenericRendererBuilder) _type(rendererType);
        }

        public GenericRendererBuilder fromJson(String json) {
            return (GenericRendererBuilder) _fromJson(json);
        }


    }

    public abstract class GenericBuilder<T> {
        private String name;
        private Class<? extends T> type;
        private T prototype;

        public String getName() {
            return name;
        }

        protected GenericBuilder<T> _name(String name) {
            this.name = name;
            return this;
        }

        protected GenericBuilder<T> _type(Class<? extends T> type) {
            this.type = type;
            return this;
        }

        public GenericBuilder<T> _fromJson(String json) {
            this.prototype = gson.fromJson(json, type);
            return this;
        }

        public T create(Configuration configuration) {
            return null;
        }
    }
}
