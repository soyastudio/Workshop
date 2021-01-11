package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.JsonObject;
import com.samskivert.mustache.Mustache;

import java.util.List;
import java.util.StringTokenizer;

public class OverridePropertiesRenderer extends XmlSchemaBaseRenderer {
    private String template;
    private boolean includeEmpty;
    private List<String> excludes;

    @Override
    public String render(XmlSchemaBase base) {
        JsonObject jsonObject = base.getAnnotation(APPLICATION, JsonObject.class);
        String contents = Mustache.compiler().compile(WorkshopRepository.getResourceAsString(template)).execute(JsonUtils.toMap(jsonObject));
        if(includeEmpty) {
            return contents;

        } else {
            StringBuilder stringBuilder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(contents, "\n");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if(!token.isEmpty() && token.contains("=") && !token.contains("???") && printable(token)) {
                    stringBuilder.append(token).append("\n");
                }
            }

            return stringBuilder.toString();
        }

    }

    private boolean printable(String token) {
        if(excludes == null) {
            return true;
        }

        for(String f: excludes) {
            String prefix = f + "#";
            if(token.startsWith(prefix)) {
                return false;
            }
        }

        return true;
    }
}
