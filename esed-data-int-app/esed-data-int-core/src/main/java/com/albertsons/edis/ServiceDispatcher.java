package com.albertsons.edis;

public interface ServiceDispatcher {

    Object dispatch(String service, String operation, String json) throws Exception;

    interface ServiceRegistry {
        void register(Object... delegate);

        String[] getServices();

        String[] getOperations(String serviceName);

        ServiceCall newServiceCall(String serviceName, String operationName);
    }

    interface ServiceCall {

        ServiceCall addParameter(String paramName, Object paramValue);

        ServiceCall fromJson(String json);

        ServiceCallResult invoke();
    }

    interface ServiceCallResult {
        boolean isSuccess();

        Throwable getException();

        Object getResult();

    }
}
