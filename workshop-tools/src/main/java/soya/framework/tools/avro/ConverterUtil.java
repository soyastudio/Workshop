package soya.framework.tools.avro;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

public class ConverterUtil {
	
	private final static String HYPHEN = "-";
	private final static String COLON = ":";
	private final static String DOT = ".";
	private final static String PLUS = "+";
	private final static String ZEE = "Z";
	private final static String ZERO_ZEE = ".000Z";
	private final static String YYYYMMDD = "yyyy-MM-dd";
	private final static String YYYYMMDD_HMS_S = "yyyy-MM-dd HH:mm:ss.SSSSSS";
	private final static String YYYYMMDD_HM = "yyyy-MM-dd HH:mm:ss";
	private final static String YYYYMMDD_T_HMS = "yyyy-MM-dd'T'HH:mm:ss";
	private final static String YYYYMMDD_T_HMS_S = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
	private final static String EMPTY_STRING = "";
	private final static String INT = "INT";
	private final static String DOUBLE = "DOUBLE";
	private final static String LONG = "LONG";
	private final static String BOOLEAN = "BOOLEAN";
	private final static String FLOAT = "FLOAT";
	private final static String UNIX_DATE = "1970-01-01 ";
	
	private final static DateTimeFormatter dtformatter_YYYYMMDD = DateTimeFormat.forPattern(YYYYMMDD);
	private final static DateTimeFormatter dtformatter_YYYYMMDD_HMS_S = DateTimeFormat.forPattern(YYYYMMDD_HMS_S);
	private final static DateTimeFormatter dtformatter_YYYYMMDD_HM = DateTimeFormat.forPattern(YYYYMMDD_HM);
	private final static DateTimeFormatter dtformatter_YYYYMMDD_T_HMS =  DateTimeFormat.forPattern(YYYYMMDD_T_HMS);
	private final static DateTimeFormatter dtformatter_YYYYMMDD_T_HMS_S = DateTimeFormat.forPattern(YYYYMMDD_T_HMS_S);

	private static final Logger logger = LoggerFactory.getLogger(ConverterUtil.class);
	
	protected static long convertDateTime(String value, String message) {
		long millisecond = 0;
		Date date = new Date();
		try {
			if (value.contains(HYPHEN) && !value.contains(COLON)) {
				//dtformatter = DateTimeFormat.forPattern(YYYYMMDD);
				return dtformatter_YYYYMMDD.parseMillis(value);

			} else if (value.contains(COLON) && !value.contains(HYPHEN)) {

				value = UNIX_DATE + value;
				if (value.contains(DOT)) {
					return dtformatter_YYYYMMDD_HMS_S.parseMillis(value);
				} else {
					return dtformatter_YYYYMMDD_HM.parseMillis(value);
				}

			} else if (value.contains(HYPHEN) && value.contains(COLON)) {
				if (value.contains(PLUS) || value.lastIndexOf(HYPHEN) == 19 || value.lastIndexOf(HYPHEN) == 23) {
					OffsetDateTime odt = OffsetDateTime.parse(value);
					return odt.toEpochSecond() * 1000;
				} else if (value.contains(ZEE)) {
					Instant instant = null;
					if (value.contains(DOT)) {
						instant = Instant.parse(value);
						date = Date.from(instant);
					} else {
						instant = Instant.parse(value.replace(ZEE, ZERO_ZEE));
						date = Date.from(instant);
					}
					return date.getTime();
				} else if (!value.contains(DOT)) {
					return dtformatter_YYYYMMDD_T_HMS.parseMillis(value);

				} else {
					return dtformatter_YYYYMMDD_T_HMS_S.parseMillis(value);

				}
			}
		} catch (Exception e) {
			logger.error(" Timestamp: " + new Timestamp(System.currentTimeMillis()) + " Fetching Value: " + value
					+ " Error : " + e + " ErrorDescription: Unable to convert xml to avro");
		}
		return millisecond;
	}

	protected static Object processDataType(String type, String value, String message) {
		try {

			switch (type) {
			case INT:
				if (value.equalsIgnoreCase(EMPTY_STRING)) {
					return 0;
				} else {
					if ((value.contains(HYPHEN) && value.indexOf(HYPHEN) != 0) || value.contains(COLON)) {
						return ConverterUtil.convertDateTime(value, message);

					} else {
						return Long.valueOf(value);
					}
				}
			case FLOAT:
				if (value.equalsIgnoreCase(EMPTY_STRING)) {
					return Float.valueOf(0.0f);
				} else {
					return Float.valueOf(value);
				}
			case DOUBLE:
				if (value.equalsIgnoreCase(EMPTY_STRING)) {
					return Double.valueOf(0.00);
				} else {
					return Double.valueOf(value);
				}
			case LONG:
				if (value.equalsIgnoreCase(EMPTY_STRING)) {
					return Long.valueOf(0);
				} else {
					if (value.contains(HYPHEN) || value.contains(COLON)) {
						return ConverterUtil.convertDateTime(value, message);
					} else {
						return Long.valueOf(value);
					}
				}
			case BOOLEAN:
				if (value.equalsIgnoreCase(EMPTY_STRING)) {
					return false;
				} else {
					return Boolean.valueOf(value);
				}
			default:
				return value;
			}

		} catch (Exception e) {
			logger.error(" Timestamp: " + new Timestamp(System.currentTimeMillis()) + " Fetching Value: " + type
					+ " Fetching Value: " + value + " Error : " + e
					+ " ErrorDescription: Unable to convert xml to avro");
		}
		return value;
	}
}
