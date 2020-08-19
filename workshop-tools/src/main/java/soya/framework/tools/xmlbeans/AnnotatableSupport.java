package soya.framework.tools.xmlbeans;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotatableSupport implements Annotatable {

    private static Gson gson = new Gson();
    private Map<String, JsonElement> annotations = new LinkedHashMap<>();

    @Override
    public Map<String, Object> getAnnotations() {
        return ImmutableMap.copyOf(annotations);
    }

    public void annotate(String key, Object value) {
        if (value == null) {
            annotations.remove(key);
        } else {
            annotations.put(key, gson.toJsonTree(value));
        }
    }

    public void annotateAsArrayElement(String key, Object value) {
        JsonElement jsonElement = annotations.get(key);
        if (jsonElement == null) {
            jsonElement = new JsonArray();
            annotations.put(key, jsonElement);
        }
        jsonElement.getAsJsonArray().add(gson.toJsonTree(value));
    }

    public void annotateAsMappedElement(String key, String propName, Object value) {
        JsonElement jsonElement = annotations.get(key);
        if (jsonElement == null) {
            jsonElement = new JsonObject();
            annotations.put(key, jsonElement);
        }
        jsonElement.getAsJsonObject().add(propName, gson.toJsonTree(value));
    }

    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    public <T> T getAnnotation(String key, Class<T> type) {
        if (annotations.containsKey(key)) {
            return gson.fromJson(annotations.get(key), type);
        } else {
            return null;
        }
    }

    public Boolean getAsBoolean(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsBoolean();
        } else {
            return null;
        }
    }

    public Number getAsNumber(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsNumber();
        } else {
            return null;
        }
    }

    public String getAsString(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsString();

        } else {
            return null;
        }
    }

    public Double getAsDouble(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsDouble();
        } else {
            return null;
        }
    }

    public Float getAsFloat(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsFloat();
        } else {
            return null;
        }
    }

    public Long getAsLong(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsLong();
        } else {
            return null;
        }
    }

    public Integer getAsInteger(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsInt();
        } else {
            return null;
        }
    }

    public Short getAsShort(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsShort();
        } else {
            return null;
        }
    }

    public BigDecimal getAsBigDecimal(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsBigDecimal();
        } else {
            return null;
        }
    }

    public BigInteger getAsBigInteger(String key) {
        if (annotations.containsKey(key)) {
            return annotations.get(key).getAsBigInteger();
        } else {
            return null;
        }
    }
}
