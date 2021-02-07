package soya.framework.pachira;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

public abstract class BarFlowSupport<T extends Baseline> implements Barflow<T> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected T baseline;

    private BaselineBuilder<T> baselineBuilder;
    private LinkedHashMap<String, AnnotatorBuilder> annotatorBuilders = new LinkedHashMap<>();
    private LinkedHashMap<String, RendererBuilder> rendererBuilders = new LinkedHashMap<>();

    @Override
    public T getBaseLine() {
        return baseline;
    }

    @Override
    public BarFlowSupport<T> baseline(BaselineBuilder<T> builder) throws FlowBuilderException {
        this.baselineBuilder = builder;
        this.baseline = builder.create();
        return this;
    }

    @Override
    public BarFlowSupport<T> annotator(AnnotatorBuilder builder) throws FlowBuilderException {
        annotatorBuilders.put(builder.getName(), builder);
        return this;
    }

    @Override
    public BarFlowSupport<T> renderer(RendererBuilder builder) throws FlowBuilderException {
        rendererBuilders.put(builder.getName(), builder);
        return this;
    }

    public BarFlowSupport<T> annotator(String name, Class<? extends Annotator> annotatorType, String json) {
        annotatorBuilders.put(name, new XsdTreeBaseFlow.GenericAnnotatorBuilder().type(annotatorType).fromJson(json));
        return this;
    }

    public BarFlowSupport<T> renderer(String name, Class<? extends Renderer> rendererType, String json) {
        rendererBuilders.put(name, new XsdTreeBaseFlow.GenericRendererBuilder().type(rendererType).fromJson(json));
        return this;
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
