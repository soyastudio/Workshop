package soya.framework.pachira;

public interface Annotatable {

    void annotate(String namespace, Object annotation);

    Object getAnnotation(String namespace);

    <T> T getAnnotation(String namespace, Class<T> annotationType);
}
