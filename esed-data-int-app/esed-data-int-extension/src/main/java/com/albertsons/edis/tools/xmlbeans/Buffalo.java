package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Buffalo<T> {
    private T base;
    private Renderer<T> renderer;
    private Map<String, Renderer<T>> renderers = new LinkedHashMap<>();

    protected Buffalo() {
    }

    public static <B> Buffalo<B> newInstance(BaseLoader<B> baseLoader) {
        Buffalo<B> buffalo = new Buffalo<>();
        buffalo.base = baseLoader.create();
        return buffalo;
    }

    public static <B> Buffalo<B> fromYaml(String yaml, Class<B> baseType) {
        Buffalo<B> buffalo = new Buffalo<>();

        Yaml yml = new Yaml();
        Object object = yml.load(yaml);
        if (object instanceof Map) {
            Map<String, Object> configuration = (Map<String, Object>) object;
            configuration.entrySet().forEach(e -> {
                String key = e.getKey();
                Object value = e.getValue();

                Object component = createObject(key, value);
                apply(component, buffalo);

            });
        } else if (object instanceof List) {
            List<Object> list = (List<Object>) object;
            list.forEach(e -> {
                if (e instanceof Map) {
                    Map<String, Object> m = (Map<String, Object>) e;
                    m.entrySet().forEach(c -> {
                        Object component = createObject(c.getKey(), c.getValue());
                        apply(component, buffalo);

                    });
                }
            });

        }

        return buffalo;
    }

    private static <B> void apply(Object component, Buffalo<B> buffalo) {

        if (component instanceof Buffalo.BaseLoader) {
            buffalo.base = (B) ((BaseLoader) component).create();

        } else if (component instanceof Buffalo.Annotator) {
            buffalo.annotate((Annotator) component);

        } else if (component instanceof Buffalo.Renderer) {
            Renderer<B> renderer = (Renderer<B>) component;
            if (buffalo.renderer == null) {
                buffalo.renderer = renderer;
            }

            if(renderer.getName() != null) {
                buffalo.renderers.put(renderer.getName(), renderer);

            } else {
                buffalo.renderers.put(component.getClass().getSimpleName(), renderer);

            }


        }
    }

    private static Object createObject(String className, Object settings) {
        Object o = null;
        try {
            Class cls = Class.forName(className);
            Gson gson = new Gson();
            if (settings != null && settings instanceof Map) {
                o = gson.fromJson(gson.toJson(settings), cls);
            } else {
                o = cls.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return o;
    }

    public Buffalo<T> annotate(Annotator<T> annotator) {
        annotator.annotate(base);
        return this;
    }

    public Buffalo<T> annotate(String yaml) {
        return this;
    }

    public String render() {
        List<String> list = new ArrayList<>(renderers.keySet());
        return new Gson().toJson(list);
    }

    public String render(Renderer<T> renderer) {
        return renderer.render(base);
    }

    public String render(String keyword) {
        Renderer<T> renderer = findRenderer(keyword);
        return renderer == null ? null : renderer.render(base);
    }

    private Renderer<T> findRenderer(String keyword) {
        if (renderers.containsKey(keyword)) {
            return renderers.get(keyword);
        } else {
            String token = keyword.toUpperCase();
            for (Map.Entry<String, Renderer<T>> entry : renderers.entrySet()) {
                String key = entry.getKey();
                if (key.toUpperCase().contains(token)) {
                    return entry.getValue();
                }
            }

            return null;
        }
    }

    public static interface BaseLoader<T> {
        T create();
    }

    public static interface Annotator<T> {
        void annotate(T base);
    }

    public static interface Renderer<T> {
        String getName();

        String render(T base);

    }
}
