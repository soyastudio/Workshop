package soya.framework.tools.xmlbeans;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.samskivert.mustache.Mustache;

public class MustacheVariableVisitor implements Mustache.Visitor {

    private JsonObject jsonObject = new JsonObject();

    public Object getVariables() {
        return jsonObject;
    }

    @Override
    public void visitText(String s) {

    }

    @Override
    public void visitVariable(String s) {
        JsonObject current = jsonObject;
        String path = s;
        while (path.contains(".")) {
            int point = path.indexOf('.');
            String key = path.substring(0, point);
            JsonElement jsonElement = current.get(key);
            if(jsonElement == null) {
                jsonElement = new JsonObject();
                current.add(key, jsonElement);
            }

            path = path.substring(point + 1);
            current = jsonElement.getAsJsonObject();

        }

        current.add(path, new JsonPrimitive("?"));
    }

    @Override
    public boolean visitInclude(String s) {
        return false;
    }

    @Override
    public boolean visitSection(String s) {
        return false;
    }

    @Override
    public boolean visitInvertedSection(String s) {
        return false;
    }
}
