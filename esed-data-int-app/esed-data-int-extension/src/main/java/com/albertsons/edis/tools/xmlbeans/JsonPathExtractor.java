package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JsonPathExtractor implements Buffalo.Annotator<XmlSchemaBase> {
    private List<String> urls;

    private Set<String> jsonPaths = new LinkedHashSet<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        if (urls != null) {
            urls.forEach(url -> {
                try {
                    FileReader reader = new FileReader(new File(url));
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    extract(root, null);
                    base.annotate("jsonpaths", jsonPaths);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void extract(JsonObject jsonObject, String parent) {
        String prefix = parent == null ? "" : parent + "/";
        jsonObject.entrySet().forEach(e -> {
            String path = prefix + e.getKey();
            JsonElement element = e.getValue();
            if (element.isJsonArray()) {
                path = path + "[*]";
                jsonPaths.add(path);

                JsonArray array = element.getAsJsonArray();
                String finalPath = path;
                array.forEach(c -> {
                    if (c.isJsonObject()) {
                        extract(c.getAsJsonObject(), finalPath);
                    }
                });

            } else {
                jsonPaths.add(path);
                if (element.isJsonObject()) {
                    extract(element.getAsJsonObject(), path);
                }

            }

        });
    }
}
