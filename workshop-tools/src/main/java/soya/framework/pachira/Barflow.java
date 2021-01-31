package soya.framework.pachira;

public class Barflow<T> {

    private T base;

    public Barflow<T> baseline(BaselineBuilder<T> builder) {
        this.base = builder.create();
        return this;
    }

    public Barflow<T> annotate() {
        return this;
    }

    public static interface BaselineBuilder<T> {
        T create();

    }

    public static interface AnnotatorBuilder {

    }

}
