package soya.framework.tao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

public class BarFlowSupport<O, K extends Annotatable, F extends BarFlowSupport> implements Barflow<O, K> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected Baseline<O, K> baseline;

    protected BaselineBuilder<O, K> baselineBuilder;

    protected LinkedHashMap<String, AnnotatorBuilder<K>> annotatorBuilders = new LinkedHashMap<>();
    protected LinkedHashMap<String, RendererBuilder> rendererBuilders = new LinkedHashMap<>();

    @Override
    public Baseline<O, K> getBaseLine() {
        return baseline;
    }

    @Override
    public F baseline(BaselineBuilder<O, K> builder) throws FlowBuilderException {
        this.baselineBuilder = builder;
        this.baseline = builder.create();
        return (F) this;
    }

    @Override
    public F annotator(AnnotatorBuilder<K> builder) throws FlowBuilderException {
        this.annotatorBuilders.put(builder.getName(), builder);
        return (F) this;
    }

    @Override
    public F renderer(RendererBuilder builder) throws FlowBuilderException {
        rendererBuilders.put(builder.getName(), builder);
        return (F) this;
    }

    public String flowInstance(String format) {
        JsonObject jsonObject = new JsonObject();

        JsonArray annotators = new JsonArray();
        annotatorBuilders.entrySet().forEach(e -> {
            String name = e.getKey();
            JsonObject object = new JsonObject();

            annotators.add(object);


        });
        jsonObject.add("annotator", annotators);

        JsonArray renderers = new JsonArray();
        rendererBuilders.entrySet().forEach(e -> {
            String name = e.getKey();
            JsonObject object = new JsonObject();

            annotators.add(object);


        });

        jsonObject.add("renderer", renderers);

        return gson.toJson(jsonObject);
    }



}
