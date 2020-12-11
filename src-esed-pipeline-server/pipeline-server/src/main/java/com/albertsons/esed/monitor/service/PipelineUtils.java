package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.Pipeline;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import org.apache.commons.beanutils.DynaBean;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipelineUtils {
    private static Gson gson = new Gson();

    private PipelineUtils() {
    }

    public static String[] createInsertStatement(Pipeline pipeline, JsonArray data) {
        String[] inserts = new String[data.size()];
        String template = pipeline.getSourceTable().getInsert();
        for (int i = 0; i < data.size(); i++) {
            inserts[i] = compile(template, pipeline, data.get(i).getAsJsonObject());
        }
        return inserts;
    }

    public static String[] createUpdateStatement(Pipeline pipeline, JsonArray data) {
        String[] inserts = new String[data.size()];
        String template = pipeline.getSourceTable().getUpdate();
        for (int i = 0; i < data.size(); i++) {
            inserts[i] = compile(template, pipeline, data.get(i).getAsJsonObject());
        }

        return inserts;
    }

    private static String compile(String template, Pipeline pipeline, JsonObject json) {
        Map<String, Pipeline.TableColumn> metadata = pipeline.getSourceTable().metadata();
        String result = template;
        for (String col : pipeline.getSourceTable().getColumns()) {
            String token = ":" + col;
            JsonElement element = json.get(col);
            if (element == null) {
                result = result.replaceAll(token, "null");

            } else if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                String replacement = null;
                if (primitive.isString()) {
                    replacement = primitive.getAsString();
                    replacement = "'" + escape(replacement) + "'";

                } else if (primitive.isNumber()) {
                    replacement = "" + primitive.getAsNumber();

                } else if (primitive.isBoolean()) {
                    replacement = "" + primitive.getAsBoolean();

                }
                result = result.replaceAll(token, replacement);

            }
        }

        return result;
    }

    public static String escape(String src) {
        String result = src;
        if (result.contains("'")) {
            result = result.replaceAll("'", "''");
        }

        return result;
    }

    public static String createUpdateStatement(Pipeline pipeline) {
        String[] columns = pipeline.getSourceTable().getColumns();
        String pk = pipeline.getSourceTable().getPrimaryKey();
        List<String> valueColumns = new ArrayList<>();
        for (String col : columns) {
            if (!col.equalsIgnoreCase(pk)) {
                valueColumns.add(col);
            }
        }
        StringBuilder builder = new StringBuilder("UPDATE ")
                .append(pipeline.getSourceTable().getSchema())
                .append(".")
                .append(pipeline.getSourceTable().getTable())
                .append(" SET ");

        for (int i = 0; i < valueColumns.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(valueColumns.get(i)).append(" = ").append(":").append(valueColumns.get(i));
        }

        builder.append(" WHERE ").append(pk).append(" = :").append(pk);

        return builder.toString();
    }

    public static JsonObject toJsonObject(Pipeline pipeline, DynaBean bean) {
        JsonObject jsonObject = new JsonObject();

        for (String col : pipeline.getSourceTable().getColumns()) {
            Object v = bean.get(col);
            if (v instanceof String) {
                jsonObject.add(col, new JsonPrimitive(((String) v).trim()));
            }
        }

        return jsonObject;
    }

    public static ApiInvocation createApiInvocation(Pipeline pipeline) {
        ApiInvocation invocation = new ApiInvocation();
        invocation.setUrl(pipeline.getApi().getUrl());
        return invocation;
    }

    public static JsonArray transform(JsonArray array, Pipeline pipeline) {
        JsonArray results = array;
        if(pipeline.getApi().getFilter() != null) {
            results = filterByJsonPath(pipeline.getApi().getFilter(), gson.toJson(array));
        }

        return results;
    }

    public static JsonArray filterByJsonPath(String jsonPath, String json) {
        Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration.builder().jsonProvider(new GsonJsonProvider())
                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

        Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider())
                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

        try {
            return JsonPath.using(conf).parse(json).read(jsonPath);

        } catch (Exception e) {
            return new JsonArray();
        }
    }

    public static JsonArray shift(JsonArray array, Pipeline.Shifter[] shifters) {

        JsonArray result = new JsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject source = array.get(i).getAsJsonObject();
            JsonObject target = new JsonObject();

            for (Pipeline.Shifter shifter : shifters) {
                Pipeline.DataType type = shifter.getType();
                String to = shifter.getTo();
                String from = shifter.getFrom();
                String exp = shifter.getExpression();

                JsonElement value = source.get(from);
                if (exp != null) {
                    value = Expressions.evaluate(value, exp);
                }

                if (value != null) {
                    if (Pipeline.DataType.String.equals(type)) {
                        if (!value.isJsonPrimitive()) {
                            value = new JsonPrimitive(gson.toJson(value));
                        }

                    } else if (Pipeline.DataType.Number.equals(type)) {

                    } else if (Pipeline.DataType.Boolean.equals(type)) {

                    }
                }

                target.add(to, value);
            }
            result.add(target);
        }

        return result;
    }
    
    public static JsonElement fromJsonPath(JsonElement json, String path) {
    	return null;
    	
    }

    public static boolean compare(Pipeline pipeline, @NotNull JsonObject newJson, @NotNull JsonObject oldJson) {
        for (String col : pipeline.getSourceTable().getColumns()) {
            JsonElement v1 = newJson.get(col);
            JsonElement v2 = oldJson.get(col);
            if (!compare(v1, v2)) {
                return false;
            }
        }

        return true;
    }

    public static boolean compare(JsonElement a, JsonElement b) {
        if (a == null && b == null || a == null && b.isJsonNull() || a.isJsonNull() && b == null) {
            return true;

        } else if (a == null && b.getAsString().isEmpty() || b == null && a.getAsString().isEmpty()) {
            return true;

        } else if (a != null && b == null || a == null && b != null) {
            return false;

        } else if (!a.getClass().equals(b.getClass())) {
            return false;

        } else if (a.isJsonPrimitive()) {
            return a.equals(b);

        } else if (a.isJsonNull()) {
            return true;

        } else {
            Gson gson = new Gson();
            return gson.toJson(a).equals(gson.toJson(b));
        }
    }

}
