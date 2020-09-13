package soya.framework.tools.xmlbeans;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;

public class XmlMessageRenderer extends XmlSchemaBaseRenderer {

    private String inputMessageFile;

    @Override
    public String render(XmlSchemaBase base) {
        Reader reader = null;
        try {
            reader = new FileReader(new File(inputMessageFile));

        } catch (FileNotFoundException e) {
            return null;
        }

        JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();

        return iibJsonMessageConvert(data);
    }



    private String iibJsonMessageConvert(JsonObject data) {
        StringBuilder builder = new StringBuilder();
        builder.append("<message><JSON><Data>");
        data.entrySet().forEach(e -> {
            String propName = e.getKey();
            JsonElement propValue = e.getValue();
            append(propName, propValue, builder);
        });
        builder.append("</Data></JSON></message>");
        return builder.toString();
    }

    private void append(String propName, JsonElement jsonElement, StringBuilder builder) {
        builder.append("<").append(propName).append(">");
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            builder.append(fromJsonPrimitive(primitive));

        } else if (jsonElement.isJsonArray()) {
            jsonElement.getAsJsonArray().forEach(a -> {
                builder.append("<Item>");
                if (a.isJsonObject()) {
                    JsonObject jsonObject = a.getAsJsonObject();
                    jsonObject.entrySet().forEach(en -> {
                        append(en.getKey(), en.getValue(), builder);
                    });
                } else if (a.isJsonPrimitive()) {
                    builder.append(fromJsonPrimitive(a.getAsJsonPrimitive()));
                }
                builder.append("</Item>");
            });

        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.entrySet().forEach(en -> {
                append(en.getKey(), en.getValue(), builder);
            });
        }
        builder.append("</").append(propName).append(">");

    }

    private Object fromJsonPrimitive(JsonPrimitive primitive) {
        Object result = null;
        if (primitive.isBoolean()) {
            result = primitive.getAsBoolean();

        } else if (primitive.isString()) {
            result = primitive.getAsString();

        } else if (primitive.isNumber()) {
            Number number = primitive.getAsNumber();
            if (number != null) {
                if (number.toString().contains(".")) {
                    result = new BigDecimal(number.toString()).toEngineeringString();
                } else {
                    result = number.intValue();
                }
            }
        }

        return result;
    }
}
