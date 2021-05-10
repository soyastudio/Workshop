package soya.framework.tao.edis;

import com.google.gson.*;
import com.samskivert.mustache.Mustache;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tao.util.JsonUtils;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class MustacheRenderer extends EdisRenderer {
    private Gson gson = new Gson();
    private File templateFile;
    private JsonObject variables = new JsonObject();

    public MustacheRenderer templateFile(File templateFile) {
        this.templateFile = templateFile;
        return this;
    }

    public MustacheRenderer variables(Object values) {
        variables = gson.toJsonTree(values).getAsJsonObject();
        return this;
    }

    public MustacheRenderer setVariable(String name, Object value) {
        variables.add(name, gson.toJsonTree(value));
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) throws T123W.FlowExecutionException {
        try {
            return Mustache.compiler().compile(new FileReader(templateFile)).execute(JsonUtils.toMap(variables));

        } catch (Exception e) {
            e.printStackTrace();
            throw new T123W.FlowExecutionException(e);
        }
    }

}
