package com.albertsons.edis.cms;

public abstract class ContentResourceWrapper<T> implements ContentResource {
    private final String path;
    private final T wrappedResource;

    protected ContentResourceWrapper(String path, T wrappedResource) {
        this.path = path;
        this.wrappedResource = wrappedResource;
    }

    @Override
    public String getPath() {
        return path;
    }

    public T getResource() {
        return wrappedResource;
    }
}
