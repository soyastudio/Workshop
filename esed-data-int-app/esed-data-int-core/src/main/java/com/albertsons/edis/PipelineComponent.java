package com.albertsons.edis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

public class PipelineComponent<T extends PipelineComponent.Processor> implements PipelinePattern<T> {
    private static Logger logger = LoggerFactory.getLogger(PipelineComponent.class);

    protected transient Class<T> processorType;
    protected transient File location;
    protected transient String pipeline;
    protected transient long timeout;

    @Override
    public T build() {
        try {
            this.processorType = findProcessorType();
            Constructor constructor = processorType.getConstructor();
            constructor.setAccessible(true);
            T processor = (T) constructor.newInstance(new Object[0]);

            processor.location = this.location;
            processor.pipeline = this.pipeline;
            processor.timeout = this.timeout;

            configure(processor);

            return processor;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void configure(T processor) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    Field target = processor.getClass().getDeclaredField(field.getName());
                    if (target.getType().isAssignableFrom(field.getType())) {
                        target.setAccessible(true);
                        target.set(processor, field.get(this));
                    }

                } catch (Exception e) {

                }

            }
        }
    }

    protected Class<T> findProcessorType() {
        Class c = getClass();
        Class<T> clazz = null;

        while (clazz == null) {
            if (Object.class.equals(c)) {
                break;
            }

            try {
                clazz = (Class<T>) ((ParameterizedType)
                        c.getGenericSuperclass()).getActualTypeArguments()[0];

            } catch (Exception e) {

            }

            c = c.getSuperclass();
        }

        return clazz;
    }

    public static abstract class Processor implements PipelineProcessor {

        protected String pipeline;
        protected long timeout;
        protected File location;
        protected PipelineContext pipelineContext;

        @Override
        public PipelineContext getPipelineContext() {
            return pipelineContext;
        }

        protected final void init(PipelineContext pipelineContext) {
            this.pipelineContext = pipelineContext;
            init();
        }

        protected void init() {
            logger.info("init method can be overwrite by sub class");
        }

        protected void destroy() {
            logger.info("destroy method can be overwrite by sub class");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Processor processor = (Processor) o;

            return pipeline.equals(processor.pipeline);
        }

        @Override
        public int hashCode() {
            return pipeline.hashCode();
        }
    }
}
