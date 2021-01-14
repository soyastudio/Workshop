package com.albertsons.edis.springboot.service;

import com.albertsons.edis.Delegate;
import com.albertsons.edis.DelegateParameter;
import com.albertsons.edis.ServiceDelegate;
import com.albertsons.edis.ServiceDispatcher;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDelegateDispatcher implements ServiceDispatcher, ServiceDispatcher.ServiceRegistry {

    private static Map<String, String[]> services = new LinkedHashMap<>();
    private static Map<ServiceKey, Operation> operationMap = new ConcurrentHashMap<>();

    private Registry registry;

    public ServiceDelegateDispatcher() {
        this.registry = new DelegateAnnotationRegistry();
    }

    public ServiceDelegateDispatcher(Registry registry) {
        this.registry = registry;
    }


    @Override
    public void register(Object... delegates) {

        for (Object o : delegates) {
            Map<ServiceKey, Operation> map = registry.register(o);

            String service = null;
            List<String> operations = new ArrayList<>();
            for (ServiceKey key : map.keySet()) {
                if (service == null) {
                    service = key.serviceName;
                }
                operations.add(key.operationName);
            }

            Collections.sort(operations);
            services.put(service, operations.toArray(new String[operations.size()]));
            operationMap.putAll(map);
        }
    }

    @Override
    public String[] getServices() {
        List<String> list = new ArrayList<>(services.keySet());
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    @Override
    public String[] getOperations(String serviceName) {
        return services.get(serviceName);
    }

    @Override
    public ServiceCall newServiceCall(String serviceName, String operationName) {
        return new DefaultServiceCall(serviceName, operationName);
    }

    @Override
    public Object dispatch(String service, String operation, String json) throws Exception {
        ServiceCallResult result = newServiceCall(service, operation).fromJson(json).invoke();
        if (result.isSuccess()) {
            return result.getResult();

        } else {
            throw (Exception) result.getException();
        }
    }

    static class DefaultServiceCall implements ServiceCall {
        private final ServiceKey serviceKey;
        private final Operation operation;
        private Object[] params;

        DefaultServiceCall(String serviceName, String operationName) {
            this.serviceKey = new ServiceKey(serviceName, operationName);
            if (!operationMap.containsKey(serviceKey)) {
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
        public ServiceCall fromJson(String json) {
            if (json != null) {
                Gson gson = new Gson();
                JsonElement jsonElement = JsonParser.parseString(json);
                if (jsonElement.isJsonArray()) {
                    JsonArray array = jsonElement.getAsJsonArray();
                    Class<?>[] types = operation.paramTypes.values().toArray(new Class[operation.paramTypes.size()]);
                    int i = 0;
                    for(Class<?> type: types) {
                        params[i] = gson.fromJson(array.get(i), type);
                        i ++;
                    }
                }

            }
            return this;
        }

        @Override
        public ServiceCallResult invoke() {
            try {
                Object result = operation.invoke(params);
                return DefaultServiceCallResult.successResult(result);

            } catch (InvocationTargetException | IllegalAccessException e) {
                return DefaultServiceCallResult.failureResult(e);

            }
        }
    }

    static class DefaultServiceCallResult implements ServiceCallResult {
        private Object result;
        private Throwable exception;

        private DefaultServiceCallResult() {}

        static DefaultServiceCallResult failureResult(Exception exception) {
            DefaultServiceCallResult result = new DefaultServiceCallResult();
            result.exception = exception;
            return result;
        }

        static DefaultServiceCallResult successResult(Object value) {
            DefaultServiceCallResult result = new DefaultServiceCallResult();
            result.result = value;
            return result;
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
        public Object getResult() {
            return result;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServiceKey key = (ServiceKey) o;

            if (!serviceName.equals(key.serviceName)) return false;
            return operationName.equals(key.operationName);
        }

        @Override
        public int hashCode() {
            int result = serviceName.hashCode();
            result = 31 * result + operationName.hashCode();
            return result;
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
            if (o instanceof ServiceDelegate) {
                Class<?> c = o.getClass();
                Class<?> api = findDelegateInterface(c);
                String serviceName = c.getSimpleName();
                Method[] methods = new Method[0];
                if (api != null) {
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
                        for (int i = 0; i < types.length; i++) {
                            operation.paramTypes.put("arg" + i, types[i]);
                        }

                        operationMap.put(key, operation);
                    }

                }
            } else {

            }
            return map;
        }

        private Class<?> findDelegateInterface(Class<?> c) {
            for (Class<?> i : c.getInterfaces()) {
                if (ServiceDelegate.class.isAssignableFrom(i) && !i.equals(ServiceDelegate.class)) {
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
                    if (method.getAnnotation(Delegate.class) != null) {
                        String operationName = method.getAnnotation(Delegate.class).value();
                        ServiceKey key = new ServiceKey(serviceName, operationName);
                        Operation operation = new Operation(method, o);

                        if (map.containsKey(key)) {
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

                        map.put(key, operation);
                    }
                }
            }

            return map;
        }
    }


}
