package soya.framework.tao.support;

import soya.framework.tao.Annotatable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Feature<T> implements Annotatable<T> {

    protected T origin;
    protected Map<String, Object> annotations = new LinkedHashMap<>();

    protected Feature(T origin) {
        this.origin = origin;
    }

    public T origin() {
        return origin;
    }

    @Override
    public void annotate(String namespace, Object annotation) {
        this.annotations.put(namespace, annotation);
    }

    @Override
    public Object getAnnotation(String namespace) {
        return annotations.get(namespace);
    }

    @Override
    public <A> A getAnnotation(String namespace, Class<A> annotationType) {
        return (A) annotations.get(namespace);
    }
}
