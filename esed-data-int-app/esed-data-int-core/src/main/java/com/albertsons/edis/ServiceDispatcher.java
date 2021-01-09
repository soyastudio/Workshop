package com.albertsons.edis;

public interface ServiceDispatcher {
    void register(Object... delegate);

    ServiceCallResult dispatch(ServiceCall call);

    ServiceCall newServiceCall(String serviceName, String operationName);

    static interface ServiceCall {

        ServiceCall addParameter(String paramName, Object paramValue);

        ServiceCallResult invoke();
    }

    static interface ServiceCallResult {
        boolean isSuccess();

        Throwable getException();

        <T> T getResult(Class<T> type);

    }
}
