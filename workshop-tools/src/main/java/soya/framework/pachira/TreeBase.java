package soya.framework.pachira;

public interface TreeBase<T> extends Annotatable {
    Tree extract(T source);
}
