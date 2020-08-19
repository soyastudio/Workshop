package soya.framework.tools.xmlbeans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public interface Annotatable {

    Map<String, Object> getAnnotations();

    void annotate(String key, Object value);

    void annotateAsArrayElement(String key, Object value);

    void annotateAsMappedElement(String key, String propName, Object value);

    Object getAnnotation(String key);

    <T> T getAnnotation(String key, Class<T> type);

    Boolean getAsBoolean(String key);

    Number getAsNumber(String key);

    String getAsString(String key);

    Double getAsDouble(String key);

    Float getAsFloat(String key);

    Long getAsLong(String key);

    Integer getAsInteger(String key);

    Short getAsShort(String key);

    BigDecimal getAsBigDecimal(String key);

    BigInteger getAsBigInteger(String key);
}
