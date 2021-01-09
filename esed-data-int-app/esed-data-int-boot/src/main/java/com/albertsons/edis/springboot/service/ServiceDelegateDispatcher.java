package com.albertsons.edis.springboot.service;

import com.albertsons.edis.Delegate;
import com.albertsons.edis.DelegateParameter;
import com.albertsons.edis.ServiceDelegate;
import com.albertsons.edis.ServiceDispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDelegateDispatcher implements ServiceDispatcher {
    private static Map<ServiceKey, Operation> operationMap = new ConcurrentHashMap<>();

    private Registry registry;

    public ServiceDelegateDispatcher() {
        this.registry = new DelegateAnnotationRegistry();
    }

    public ServiceDelegateDispatcher(Registry registry) {
        this.registry = registry;
    }

    @Override
    public ServiceCallResult dispatch(ServiceCall call) {
        return call.invoke();
    }

    @Override
    public void register(Object... delegates) {
        for (Object o : delegates) {
            operationMap.putAll(registry.register(o));
        }

    }

    @Override
    public ServiceCall newServiceCall(String servcieName, String operationName) {
        return new DefaultServiceCall(servcieName, operationName);
    }

    static class DefaultServiceCall implements ServiceCall {
        private final ServiceKey serviceKey;
        private final Operation operation;
        private Object[] params;

        DefaultServiceCall(String serviceName, String operationName) {
            this.serviceKey = new ServiceKey(serviceName, operationName);
            if(!operationMap.containsKey(serviceKey)) {
                throw new IllegalArgumentException("Cannot find service operation!");
            }
            this.operation = operationMap.get(serviceKey);
            this.params = new Object[operation.paramTypes.size()];
        }

        @Override
        public ServiceCall addParameter(String paramName, Object paramValue) {
            return this;
        }

        @Override
        public ServiceCallResult invoke() {
            try {
                Object result = operation.invoke(params);

                return new DefaultServiceCallResult(result);

            } catch (InvocationTargetException e) {
                return new DefaultServiceCallResult(e.getTargetException());

            } catch (IllegalAccessException e) {
                return new DefaultServiceCallResult(e);
            }
        }
    }

    static class DefaultServiceCallResult implements ServiceCallResult {
        private Object result;
        private Throwable exception;

        DefaultServiceCallResult(Object result) {
            this.result = result;
        }

        DefaultServiceCallResult(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return exception == null;
        }

        @Override
        public Throwable getException() {
            return exception;
        }

        @Override
        public <T> T getResult(Class<T> type) {
            return (T) result;
        }
    }

    static class ServiceKey {
        private final String serviceName;
        private final String operationName;

        ServiceKey(String serviceName, String operationName) {
            this.serviceName = serviceName;
            this.operationName = operationName;
            if (serviceName == null || serviceName.trim().length() == 0) {
                throw new IllegalArgumentException("Service name cannot be null or empty.");
            }

            if (operationName == null || operationName.trim().length() == 0) {
                throw new IllegalArgumentException("Operation name cannot be null or empty.");
            }
        }
    }

    static class Operation {
        private Method method;
        private Object delegate;
        private LinkedHashMap<String, Class<?>> paramTypes = new LinkedHashMap<>();

        Operation(Method method, Object delegate) {
            this.method = method;
            this.delegate = delegate;

        }

        Object invoke(Object[] input) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(delegate, input);
        }
    }

    static interface Registry {
        Map<ServiceKey, Operation> register(Object o);
    }

    static class ServiceDelegateRegistry implements Registry {

        @Override
        public Map<ServiceKey, Operation> register(Object o) {
            Map<ServiceKey, Operation> map = new LinkedHashMap<>();
            if(o instanceof ServiceDelegate) {
                Class<?> c = o.getClass();
                Class<?> api = findDelegateInterface(c);
                String serviceName = c.getSimpleName();
                Method[] methods = new Method[0];
                if(api != null) {
                    methods = api.getDeclaredMethods();
                    serviceName = api.getSimpleName();

                } else {
                    methods = c.getDeclaredMethods();
                }

                for (Method method : c.getDeclaredMethods()) {
                    if (method.isAccessible()) {
                        String operationName = method.getName();
                        ServiceKey key = new ServiceKey(serviceName, operationName);

                        Operation operation = new Operation(method, o);

                        if (operationMap.containsKey(key)) {
                            throw new IllegalArgumentException("Service Operation already exists.");
                        }

                        Class<?>[] types = method.getParameterTypes();
                        for (int i = 0; i < types.length; i ++) {
                            operation.paramTypes.put("arg" + i, types[i]);
                        }

                        operationMap.put(key, operation);
                    }

                }
            } else {

            }
            return  map;
        }

        private Class<?> findDelegateInterface(Class<?> c) {
            for(Class<?> i: c.getInterfaces()) {
                if(ServiceDelegate.class.isAssignableFrom(i) && !i.equals(ServiceDelegate.class)) {
                    return i;
                }
            }
            return null;
        }
    }

    static class DelegateAnnotationRegistry implements Registry {

        @Override
        public Map<ServiceKey, Operation> register(Object o) {
            Map<ServiceKey, Operation> map = new LinkedHashMap<>();
            Class<?> c = o.getClass();
            if (c.getAnnotation(Delegate.class) != null) {
                String serviceName = c.getAnnotation(Delegate.class).value();
                for (Method method : c.getDeclaredMethods()) {
                    if (method.isAccessible()) {
                        String operationName = method.getName();
                        if (method.getAnnotation(Delegate.class) != null) {
                            operationName = method.getAnnotation(Delegate.class).value();
                        }

                        ServiceKey key = new ServiceKey(serviceName, operationName);
                        Operation operation = new Operation(method, o);

                        if (operationMap.containsKey(key)) {
                            throw new IllegalArgumentException("Service Operation already exists.");
                        }

                        Class<?>[] types = method.getParameterTypes();
                        Annotation[][] annotations = method.getParameterAnnotations();
                        for (int i = 0; i < types.length; i++) {
                            c = types[i];
                            String name = "PARAM_" + i;

                            Annotation[] anns = annotations[i];
                            for (Annotation a : anns) {
                                if (a.getClass().equals(DelegateParameter.class)) {
                                    DelegateParameter dp = (DelegateParameter) a;
                                    name = dp.value();
                                    break;
                                }
                            }

                            operation.paramTypes.put(name, c);
                        }

                        operationMap.put(key, operation);
                    }

                }
            }

            return  map;
        }
    }


}
