package soya.framework.tools.xmlbeans;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.util.Base64;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;
import org.w3c.dom.Node;
import org.yaml.snakeyaml.Yaml;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class XmlSchemaBase extends AnnotatableSupport {

    private boolean _soapEnc;
    private static final int MAX_ELEMENTS = 1000;
    private int _nElements;

    private SchemaTypeSystem schemaTypeSystem;

    private MappingNode root;
    private Map<String, MappingNode> mappings = new LinkedHashMap<>();
    private Set<String> variables = new LinkedHashSet<>();

    private XmlSchemaBase(SchemaTypeSystem schemaTypeSystem) {
        this.schemaTypeSystem = schemaTypeSystem;
        _soapEnc = false;
    }

    private XmlSchemaBase(boolean soapEnc) {
        _soapEnc = soapEnc;
    }

    public SchemaTypeSystem getSchemaTypeSystem() {
        return schemaTypeSystem;
    }

    public MappingNode getRoot() {
        return root;
    }

    public Map<String, MappingNode> getMappings() {
        return mappings;
    }

    public MappingNode get(String path) {
        return mappings.get(path);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String createSampleForType(SchemaType sType) {
        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();

        // Skip the document node
        cursor.toNextToken();
        // Using the type and the cursor, call the utility method to get a
        // sample XML payload for that Schema element
        XmlSchemaBase generator = new XmlSchemaBase(false);
        generator.introspect(sType, cursor);
        // Cursor now contains the sample payload
        // Pretty print the result.  Note that the cursor is positioned at the
        // end of the doc so we use the original xml object that the cursor was
        // created upon to do the xmlText() against.
        XmlOptions options = new XmlOptions();
        options.put(XmlOptions.SAVE_PRETTY_PRINT);
        options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 2);
        options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);

        String result = object.xmlText(options);
        return result;
    }

    Random _picker = new Random(1);

    /**
     * Cursor position
     * Before:
     * <theElement>^</theElement>
     * After:
     * <theElement><lots of stuff/>^</theElement>
     */
    private void introspect(SchemaType stype, XmlCursor xmlc) {

        MappingNode node = new MappingNode(xmlc, stype);

        if (node.getPath() != null) {
            mappings.put(node.getPath(), node);

            if (stype.isSimpleType()) {
                if (stype.getName() != null) {
                    node.dataType = stype.getName().getLocalPart();

                } else {
                    node.dataType = stype.getBaseType().getName().getLocalPart();
                    Field[] fields = SchemaType.class.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().startsWith("FACET_")) {
                            try {
                                int facet = field.getInt(null);
                                if (stype.getFacet(facet) != null) {
                                    if (node.restriction == null) {
                                        node.restriction = new LinkedHashMap<>();
                                    }

                                    String key = field.getName().substring("FACET_".length());
                                    key = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(key);
                                    node.restriction.put(key, stype.getFacet(facet).getStringValue());
                                }

                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                node.dataType = "complexType";
            }
        }

        if (node.getLevel() == 1) {
            node.alias = "xmlDocRoot";
            this.root = node;

        } else if (node.getLevel() > 1) {
            String alias = node.getName() + "_";

            int count = 1;
            while (variables.contains(alias)) {
                alias = node.getName() + "_" + count;
                count++;
            }

            node.alias = alias;
            variables.add(alias);

            int lastSlash = node.getPath().lastIndexOf('/');
            if (lastSlash > 0) {
                String parentPath = node.getPath().substring(0, lastSlash);
                MappingNode parent = mappings.get(parentPath);
                parent.children.add(node);
                node.parent = parent;

                // Cardinality:
                if (parent != null) {
                    SchemaProperty[] properties = parent.schemaType.getElementProperties();
                    for (SchemaProperty property : properties) {
                        if (property.getName().getLocalPart().equals(node.name)) {
                            BigInteger min = property.getMinOccurs();
                            BigInteger max = property.getMaxOccurs();
                            if (min == null) {
                                min = BigInteger.ZERO;
                            }
                            StringBuilder builder = new StringBuilder(min.toString()).append("-");
                            if (max == null) {
                                builder.append("n");
                            } else {
                                builder.append(max.toString());
                            }

                            node.cardinality = builder.toString();
                        }
                    }
                } else {
                    node.cardinality = "1-1";
                }


            }
        }

        if (_typeStack.contains(stype))
            return;

        _typeStack.add(stype);

        try {
            if (stype.isSimpleType() || stype.isURType()) {
                node.nodeType = NodeType.Field;
                node.defaultValue = sampleDataForSimpleType(stype);

                processSimpleType(stype, xmlc);
                return;
            }

            // complex Type
            // <theElement>^</theElement>
            processAttributes(stype, xmlc);

            // <theElement attri1="string">^</theElement>
            switch (stype.getContentType()) {
                case SchemaType.NOT_COMPLEX_TYPE:
                case SchemaType.EMPTY_CONTENT:
                    // noop
                    break;
                case SchemaType.SIMPLE_CONTENT: {
                    processSimpleType(stype, xmlc);
                }
                break;
                case SchemaType.MIXED_CONTENT:
                    xmlc.insertChars(pick(WORDS) + " ");
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), xmlc, true);
                    }
                    xmlc.insertChars(pick(WORDS));
                    break;
                case SchemaType.ELEMENT_CONTENT:
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), xmlc, false);
                    }
                    break;
            }
        } finally {
            _typeStack.remove(_typeStack.size() - 1);
        }
    }

    private void processSimpleType(SchemaType stype, XmlCursor xmlc) {
        String sample = sampleDataForSimpleType(stype);
        sample = xmlc.getDomNode().getLocalName().toLowerCase();

        xmlc.insertChars(sample);
    }

    private static String path(XmlCursor xmlc) {
        Node node = xmlc.getDomNode();
        String path = node.getLocalName();

        if (node.getParentNode() == null) {
            // FIXME:


        } else {
            while (node.getParentNode() != null) {
                node = node.getParentNode();
                if (node.getLocalName() != null) {
                    path = node.getLocalName() + "/" + path;
                }
            }

        }

        return path;
    }

    private String sampleDataForSimpleType(SchemaType sType) {
        if (XmlObject.type.equals(sType))
            return "anyType";

        if (XmlAnySimpleType.type.equals(sType))
            return "anySimpleType";

        if (sType.getSimpleVariety() == SchemaType.LIST) {
            SchemaType itemType = sType.getListItemType();
            StringBuilder sb = new StringBuilder();
            int length = pickLength(sType);
            if (length > 0)
                sb.append(sampleDataForSimpleType(itemType));
            for (int i = 1; i < length; i += 1) {
                sb.append(' ');
                sb.append(sampleDataForSimpleType(itemType));
            }
            return sb.toString();
        }

        if (sType.getSimpleVariety() == SchemaType.UNION) {
            SchemaType[] possibleTypes = sType.getUnionConstituentTypes();
            if (possibleTypes.length == 0)
                return "";
            return sampleDataForSimpleType(possibleTypes[pick(possibleTypes.length)]);
        }

        XmlAnySimpleType[] enumValues = sType.getEnumerationValues();
        if (enumValues != null && enumValues.length > 0) {
            return enumValues[pick(enumValues.length)].getStringValue();
        }

        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            default:
            case SchemaType.BTC_NOT_BUILTIN:
                return "";

            case SchemaType.BTC_ANY_TYPE:
            case SchemaType.BTC_ANY_SIMPLE:
                return "anything";

            case SchemaType.BTC_BOOLEAN:
                return pick(2) == 0 ? "true" : "false";

            case SchemaType.BTC_BASE_64_BINARY: {
                String result = null;
                try {
                    result = new String(Base64.encode(formatToLength(pick(WORDS), sType).getBytes("utf-8")));
                } catch (java.io.UnsupportedEncodingException e) {  /* Can't possibly happen */ }
                return result;
            }

            case SchemaType.BTC_HEX_BINARY:
                return HexBin.encode(formatToLength(pick(WORDS), sType));

            case SchemaType.BTC_ANY_URI:
                return formatToLength("http://www." + pick(DNS1) + "." + pick(DNS2) + "/" + pick(WORDS) + "/" + pick(WORDS), sType);

            case SchemaType.BTC_QNAME:
                return formatToLength("qname", sType);

            case SchemaType.BTC_NOTATION:
                return formatToLength("notation", sType);

            case SchemaType.BTC_FLOAT:
                return "1.5E2";
            case SchemaType.BTC_DOUBLE:
                return "1.051732E7";
            case SchemaType.BTC_DECIMAL:
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_SHORT:
                        return formatDecimal("1", sType);
                    case SchemaType.BTC_UNSIGNED_SHORT:
                        return formatDecimal("5", sType);
                    case SchemaType.BTC_BYTE:
                        return formatDecimal("2", sType);
                    case SchemaType.BTC_UNSIGNED_BYTE:
                        return formatDecimal("6", sType);
                    case SchemaType.BTC_INT:
                        return formatDecimal("3", sType);
                    case SchemaType.BTC_UNSIGNED_INT:
                        return formatDecimal("7", sType);
                    case SchemaType.BTC_LONG:
                        return formatDecimal("10", sType);
                    case SchemaType.BTC_UNSIGNED_LONG:
                        return formatDecimal("11", sType);
                    case SchemaType.BTC_INTEGER:
                        return formatDecimal("100", sType);
                    case SchemaType.BTC_NON_POSITIVE_INTEGER:
                        return formatDecimal("-200", sType);
                    case SchemaType.BTC_NEGATIVE_INTEGER:
                        return formatDecimal("-201", sType);
                    case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                        return formatDecimal("200", sType);
                    case SchemaType.BTC_POSITIVE_INTEGER:
                        return formatDecimal("201", sType);
                    default:
                    case SchemaType.BTC_DECIMAL:
                        return formatDecimal("1000.00", sType);
                }

            case SchemaType.BTC_STRING: {
                String result;
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_STRING:
                    case SchemaType.BTC_NORMALIZED_STRING:
                        result = "string";
                        break;

                    case SchemaType.BTC_TOKEN:
                        result = "token";
                        break;

                    default:
                        result = "string";
                        break;
                }

                return formatToLength(result, sType);
            }

            case SchemaType.BTC_DURATION:
                return formatDuration(sType);

            case SchemaType.BTC_DATE_TIME:
            case SchemaType.BTC_TIME:
            case SchemaType.BTC_DATE:
            case SchemaType.BTC_G_YEAR_MONTH:
            case SchemaType.BTC_G_YEAR:
            case SchemaType.BTC_G_MONTH_DAY:
            case SchemaType.BTC_G_DAY:
            case SchemaType.BTC_G_MONTH:
                return formatDate(sType);
        }
    }

    // a bit from the Aenid
    public static final String[] WORDS = new String[]
            {
                    "ipsa", "iovis", "rapidum", "iaculata", "e", "nubibus", "ignem",
                    "disiecitque", "rates", "evertitque", "aequora", "ventis",
                    "illum", "exspirantem", "transfixo", "pectore", "flammas",
                    "turbine", "corripuit", "scopuloque", "infixit", "acuto",
                    "ast", "ego", "quae", "divum", "incedo", "regina", "iovisque",
                    "et", "soror", "et", "coniunx", "una", "cum", "gente", "tot", "annos",
                    "bella", "gero", "et", "quisquam", "numen", "iunonis", "adorat",
                    "praeterea", "aut", "supplex", "aris", "imponet", "honorem",
                    "talia", "flammato", "secum", "dea", "corde", "volutans",
                    "nimborum", "in", "patriam", "loca", "feta", "furentibus", "austris",
                    "aeoliam", "venit", "hic", "vasto", "rex", "aeolus", "antro",
                    "luctantis", "ventos", "tempestatesque", "sonoras",
                    "imperio", "premit", "ac", "vinclis", "et", "carcere", "frenat",
                    "illi", "indignantes", "magno", "cum", "murmure", "montis",
                    "circum", "claustra", "fremunt", "celsa", "sedet", "aeolus", "arce",
                    "sceptra", "tenens", "mollitque", "animos", "et", "temperat", "iras",
                    "ni", "faciat", "maria", "ac", "terras", "caelumque", "profundum",
                    "quippe", "ferant", "rapidi", "secum", "verrantque", "per", "auras",
                    "sed", "pater", "omnipotens", "speluncis", "abdidit", "atris",
                    "hoc", "metuens", "molemque", "et", "montis", "insuper", "altos",
                    "imposuit", "regemque", "dedit", "qui", "foedere", "certo",
                    "et", "premere", "et", "laxas", "sciret", "dare", "iussus", "habenas",
            };


    private static final String[] DNS1 = new String[]{"corp", "your", "my", "sample", "company", "test", "any"};
    private static final String[] DNS2 = new String[]{"com", "org", "com", "gov", "org", "com", "org", "com", "edu"};

    private int pick(int n) {
        return _picker.nextInt(n);
    }

    private String pick(String[] a) {
        return a[pick(a.length)];
    }

    private String pick(String[] a, int count) {
        if (count <= 0)
            return "";

        int i = pick(a.length);
        StringBuilder sb = new StringBuilder(a[i]);
        while (count-- > 0) {
            i += 1;
            if (i >= a.length)
                i = 0;
            sb.append(' ');
            sb.append(a[i]);
        }
        return sb.toString();
    }

    private String pickDigits(int digits) {
        StringBuilder sb = new StringBuilder();
        while (digits-- > 0)
            sb.append(Integer.toString(pick(10)));
        return sb.toString();
    }

    private int pickLength(SchemaType sType) {
        XmlInteger length = (XmlInteger) sType.getFacet(SchemaType.FACET_LENGTH);
        if (length != null)
            return length.getBigIntegerValue().intValue();
        XmlInteger min = (XmlInteger) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
        XmlInteger max = (XmlInteger) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
        int minInt, maxInt;
        if (min == null)
            minInt = 0;
        else
            minInt = min.getBigIntegerValue().intValue();
        if (max == null)
            maxInt = Integer.MAX_VALUE;
        else
            maxInt = max.getBigIntegerValue().intValue();
        // We try to keep the length of the array within reasonable limits,
        // at least 1 item and at most 3 if possible
        if (minInt == 0 && maxInt >= 1)
            minInt = 1;
        if (maxInt > minInt + 2)
            maxInt = minInt + 2;
        if (maxInt < minInt)
            maxInt = minInt;
        return minInt + pick(maxInt - minInt);
    }

    /**
     * Formats a given string to the required length, using the following operations:
     * - append the source string to itself as necessary to pass the minLength;
     * - truncate the result of previous step, if necessary, to keep it within minLength.
     */
    private String formatToLength(String s, SchemaType sType) {
        String result = s;
        try {
            SimpleValue min = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (min == null)
                min = (SimpleValue) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
            if (min != null) {
                int len = min.getIntValue();
                while (result.length() < len)
                    result = result + result;
            }
            SimpleValue max = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (max == null)
                max = (SimpleValue) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
            if (max != null) {
                int len = max.getIntValue();
                if (result.length() > len)
                    result = result.substring(0, len);
            }
        } catch (Exception e) // intValue can be out of range
        {
        }
        return result;
    }

    private String formatDecimal(String start, SchemaType sType) {
        BigDecimal result = new BigDecimal(start);
        XmlDecimal xmlD;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        BigDecimal min = xmlD != null ? xmlD.getBigDecimalValue() : null;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        BigDecimal max = xmlD != null ? xmlD.getBigDecimalValue() : null;
        boolean minInclusive = true, maxInclusive = true;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal minExcl = xmlD.getBigDecimalValue();
            if (min == null || min.compareTo(minExcl) < 0) {
                min = minExcl;
                minInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal maxExcl = xmlD.getBigDecimalValue();
            if (max == null || max.compareTo(maxExcl) > 0) {
                max = maxExcl;
                maxInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_TOTAL_DIGITS);
        int totalDigits = -1;
        if (xmlD != null) {
            totalDigits = xmlD.getBigDecimalValue().intValue();

            StringBuilder sb = new StringBuilder(totalDigits);
            for (int i = 0; i < totalDigits; i++)
                sb.append('9');
            BigDecimal digitsLimit = new BigDecimal(sb.toString());
            if (max != null && max.compareTo(digitsLimit) > 0) {
                max = digitsLimit;
                maxInclusive = true;
            }
            digitsLimit = digitsLimit.negate();
            if (min != null && min.compareTo(digitsLimit) < 0) {
                min = digitsLimit;
                minInclusive = true;
            }
        }

        int sigMin = min == null ? 1 : result.compareTo(min);
        int sigMax = max == null ? -1 : result.compareTo(max);
        boolean minOk = sigMin > 0 || sigMin == 0 && minInclusive;
        boolean maxOk = sigMax < 0 || sigMax == 0 && maxInclusive;

        // Compute the minimum increment
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_FRACTION_DIGITS);
        int fractionDigits = -1;
        BigDecimal increment;
        if (xmlD == null)
            increment = new BigDecimal(1);
        else {
            fractionDigits = xmlD.getBigDecimalValue().intValue();
            if (fractionDigits > 0) {
                StringBuilder sb = new StringBuilder("0.");
                for (int i = 1; i < fractionDigits; i++)
                    sb.append('0');
                sb.append('1');
                increment = new BigDecimal(sb.toString());
            } else
                increment = new BigDecimal(1.0);
        }

        if (minOk && maxOk) {
            // OK
        } else if (minOk && !maxOk) {
            // TOO BIG
            if (maxInclusive)
                result = max;
            else
                result = max.subtract(increment);
        } else if (!minOk && maxOk) {
            // TOO SMALL
            if (minInclusive)
                result = min;
            else
                result = min.add(increment);
        } else {
            // MIN > MAX!!
        }

        // We have the number
        // Adjust the scale according to the totalDigits and fractionDigits
        int digits = 0;
        BigDecimal ONE = new BigDecimal(BigInteger.ONE);
        for (BigDecimal n = result; n.abs().compareTo(ONE) >= 0; digits++)
            n = n.movePointLeft(1);

        if (fractionDigits > 0)
            if (totalDigits >= 0)
                result = result.setScale(Math.max(fractionDigits, totalDigits - digits));
            else
                result = result.setScale(fractionDigits);
        else if (fractionDigits == 0)
            result = result.setScale(0);

        return result.toString();
    }

    private String formatDuration(SchemaType sType) {
        XmlDuration d =
                (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        GDuration minInclusive = null;
        if (d != null)
            minInclusive = d.getGDurationValue();

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        GDuration maxInclusive = null;
        if (d != null)
            maxInclusive = d.getGDurationValue();

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        GDuration minExclusive = null;
        if (d != null)
            minExclusive = d.getGDurationValue();

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        GDuration maxExclusive = null;
        if (d != null)
            maxExclusive = d.getGDurationValue();

        GDurationBuilder gdurb = new GDurationBuilder();
        BigInteger min, max;

        gdurb.setSecond(pick(800000));
        gdurb.setMonth(pick(20));

        // Years
        // Months
        // Days
        // Hours
        // Minutes
        // Seconds
        // Fractions
        if (minInclusive != null) {
            if (gdurb.getYear() < minInclusive.getYear())
                gdurb.setYear(minInclusive.getYear());
            if (gdurb.getMonth() < minInclusive.getMonth())
                gdurb.setMonth(minInclusive.getMonth());
            if (gdurb.getDay() < minInclusive.getDay())
                gdurb.setDay(minInclusive.getDay());
            if (gdurb.getHour() < minInclusive.getHour())
                gdurb.setHour(minInclusive.getHour());
            if (gdurb.getMinute() < minInclusive.getMinute())
                gdurb.setMinute(minInclusive.getMinute());
            if (gdurb.getSecond() < minInclusive.getSecond())
                gdurb.setSecond(minInclusive.getSecond());
            if (gdurb.getFraction().compareTo(minInclusive.getFraction()) < 0)
                gdurb.setFraction(minInclusive.getFraction());
        }

        if (maxInclusive != null) {
            if (gdurb.getYear() > maxInclusive.getYear())
                gdurb.setYear(maxInclusive.getYear());
            if (gdurb.getMonth() > maxInclusive.getMonth())
                gdurb.setMonth(maxInclusive.getMonth());
            if (gdurb.getDay() > maxInclusive.getDay())
                gdurb.setDay(maxInclusive.getDay());
            if (gdurb.getHour() > maxInclusive.getHour())
                gdurb.setHour(maxInclusive.getHour());
            if (gdurb.getMinute() > maxInclusive.getMinute())
                gdurb.setMinute(maxInclusive.getMinute());
            if (gdurb.getSecond() > maxInclusive.getSecond())
                gdurb.setSecond(maxInclusive.getSecond());
            if (gdurb.getFraction().compareTo(maxInclusive.getFraction()) > 0)
                gdurb.setFraction(maxInclusive.getFraction());
        }

        if (minExclusive != null) {
            if (gdurb.getYear() <= minExclusive.getYear())
                gdurb.setYear(minExclusive.getYear() + 1);
            if (gdurb.getMonth() <= minExclusive.getMonth())
                gdurb.setMonth(minExclusive.getMonth() + 1);
            if (gdurb.getDay() <= minExclusive.getDay())
                gdurb.setDay(minExclusive.getDay() + 1);
            if (gdurb.getHour() <= minExclusive.getHour())
                gdurb.setHour(minExclusive.getHour() + 1);
            if (gdurb.getMinute() <= minExclusive.getMinute())
                gdurb.setMinute(minExclusive.getMinute() + 1);
            if (gdurb.getSecond() <= minExclusive.getSecond())
                gdurb.setSecond(minExclusive.getSecond() + 1);
            if (gdurb.getFraction().compareTo(minExclusive.getFraction()) <= 0)
                gdurb.setFraction(minExclusive.getFraction().add(new BigDecimal(0.001)));
        }

        if (maxExclusive != null) {
            if (gdurb.getYear() > maxExclusive.getYear())
                gdurb.setYear(maxExclusive.getYear());
            if (gdurb.getMonth() > maxExclusive.getMonth())
                gdurb.setMonth(maxExclusive.getMonth());
            if (gdurb.getDay() > maxExclusive.getDay())
                gdurb.setDay(maxExclusive.getDay());
            if (gdurb.getHour() > maxExclusive.getHour())
                gdurb.setHour(maxExclusive.getHour());
            if (gdurb.getMinute() > maxExclusive.getMinute())
                gdurb.setMinute(maxExclusive.getMinute());
            if (gdurb.getSecond() > maxExclusive.getSecond())
                gdurb.setSecond(maxExclusive.getSecond());
            if (gdurb.getFraction().compareTo(maxExclusive.getFraction()) > 0)
                gdurb.setFraction(maxExclusive.getFraction());
        }

        gdurb.normalize();
        return gdurb.toString();
    }

    private String formatDate(SchemaType sType) {
        GDateBuilder gdateb = new GDateBuilder(new Date(1000L * pick(365 * 24 * 60 * 60) + (30L + pick(20)) * 365 * 24 * 60 * 60 * 1000));
        GDate min = null, max = null;
        GDate temp;

        // Find the min and the max according to the type
        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            case SchemaType.BTC_DATE_TIME: {
                XmlDateTime x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_TIME: {
                XmlTime x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_DATE: {
                XmlDate x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_G_YEAR_MONTH: {
                XmlGYearMonth x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_G_YEAR: {
                XmlGYear x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_G_MONTH_DAY: {
                XmlGMonthDay x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_G_DAY: {
                XmlGDay x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
            case SchemaType.BTC_G_MONTH: {
                XmlGMonth x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null)
                    min = x.getGDateValue();
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null)
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0)
                        min = x.getGDateValue();

                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null)
                    max = x.getGDateValue();
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null)
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0)
                        max = x.getGDateValue();
                break;
            }
        }

        if (min != null && max == null) {
            if (min.compareToGDate(gdateb) >= 0) {
                // Reset the date to min + (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min == null && max != null) {
            if (max.compareToGDate(gdateb) <= 0) {
                // Reset the date to max - (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 0 - pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min != null && max != null) {
            if (min.compareToGDate(gdateb) >= 0 || max.compareToGDate(gdateb) <= 0) {
                // Find a date between the two
                Calendar c = min.getCalendar();
                Calendar cmax = max.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 1);
                if (c.after(cmax)) {
                    c.add(Calendar.HOUR_OF_DAY, -1);
                    c.add(Calendar.MINUTE, 1);
                    if (c.after(cmax)) {
                        c.add(Calendar.MINUTE, -1);
                        c.add(Calendar.SECOND, 1);
                        if (c.after(cmax)) {
                            c.add(Calendar.SECOND, -1);
                            c.add(Calendar.MILLISECOND, 1);
                            if (c.after(cmax))
                                c.add(Calendar.MILLISECOND, -1);
                        }
                    }
                }
                gdateb = new GDateBuilder(c);
            }
        }

        gdateb.setBuiltinTypeCode(sType.getPrimitiveType().getBuiltinTypeCode());
        if (pick(2) == 0)
            gdateb.clearTimeZone();
        return gdateb.toString();
    }

    private SchemaType closestBuiltin(SchemaType sType) {
        while (!sType.isBuiltinType())
            sType = sType.getBaseType();
        return sType;
    }

    /**
     * Cracks a combined QName of the form URL:localname
     */
    public static QName crackQName(String qName) {
        String ns;
        String name;

        int index = qName.lastIndexOf(':');
        if (index >= 0) {
            ns = qName.substring(0, index);
            name = qName.substring(index + 1);
        } else {
            ns = "";
            name = qName;
        }

        return new QName(ns, name);
    }


    /**
     * Cursor position:
     * Before this call:
     * <outer><foo/>^</outer>  (cursor at the ^)
     * After this call:
     * <<outer><foo/><bar/>som text<etc/>^</outer>
     */
    private void processParticle(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        int loop = determineMinMaxForSample(sp, xmlc);

        while (loop-- > 0) {
            switch (sp.getParticleType()) {
                case (SchemaParticle.ELEMENT):
                    processElement(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.SEQUENCE):
                    processSequence(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.CHOICE):
                    processChoice(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.ALL):
                    processAll(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.WILDCARD):
                    processWildCard(sp, xmlc, mixed);
                    break;
                default:
                    // throw new Exception("No Match on Schema Particle Type: " + String.valueOf(sp.getParticleType()));
            }
        }
    }

    private int determineMinMaxForSample(SchemaParticle sp, XmlCursor xmlc) {
        int minOccurs = sp.getIntMinOccurs();
        int maxOccurs = sp.getIntMaxOccurs();

        if (minOccurs == maxOccurs)
            return minOccurs;

        int result = minOccurs;
        if (result == 0 && _nElements < MAX_ELEMENTS)
            result = 1;

        if (sp.getParticleType() != SchemaParticle.ELEMENT)
            return result;

        // it probably only makes sense to put comments in front of individual elements that repeat

        if (sp.getMaxOccurs() == null) {
            // xmlc.insertComment("The next " + getItemNameOrType(sp, xmlc) + " may be repeated " + minOccurs + " or more times");
            if (minOccurs == 0)
                xmlc.insertComment("Zero or more repetitions:");
            else
                xmlc.insertComment(minOccurs + " or more repetitions:");
        } else if (sp.getIntMaxOccurs() > 1) {
            xmlc.insertComment(minOccurs + " to " + String.valueOf(sp.getMaxOccurs()) + " repetitions:");
        } else {
            xmlc.insertComment("Optional:");
        }
        return result;
    }

    /*
     Return a name for the element or the particle type to use in the comment for minoccurs, max occurs
    */
    private String getItemNameOrType(SchemaParticle sp, XmlCursor xmlc) {
        String elementOrTypeName = null;
        if (sp.getParticleType() == SchemaParticle.ELEMENT) {
            elementOrTypeName = "Element (" + sp.getName().getLocalPart() + ")";
        } else {
            elementOrTypeName = printParticleType(sp.getParticleType());
        }
        return elementOrTypeName;
    }

    private void processElement(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        // cast as schema local element
        SchemaLocalElement element = (SchemaLocalElement) sp;
        /// ^  -> <elemenname></elem>^
        if (_soapEnc)
            xmlc.insertElement(element.getName().getLocalPart()); // soap encoded? drop namespaces.
        else
            xmlc.insertElement(element.getName().getLocalPart(), element.getName().getNamespaceURI());
        _nElements++;
        /// -> <elem>^</elem>
        xmlc.toPrevToken();
        // -> <elem>stuff^</elem>

        introspect(element.getType(), xmlc);
        // -> <elem>stuff</elem>^
        xmlc.toNextToken();

    }

    private void moveToken(int numToMove, XmlCursor xmlc) {
        for (int i = 0; i < Math.abs(numToMove); i++) {
            if (numToMove < 0) {
                xmlc.toPrevToken();
            } else {
                xmlc.toNextToken();
            }
        }
    }

    private static final String formatQName(XmlCursor xmlc, QName qName) {
        XmlCursor parent = xmlc.newCursor();
        parent.toParent();
        String prefix = parent.prefixForNamespace(qName.getNamespaceURI());
        parent.dispose();
        String name;
        if (prefix == null || prefix.length() == 0)
            name = qName.getLocalPart();
        else
            name = prefix + ":" + qName.getLocalPart();
        return name;
    }

    private static final QName HREF = new QName("href");
    private static final QName ID = new QName("id");
    private static final QName XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    private static final QName ENC_ARRAYTYPE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
    private static final QName ENC_OFFSET = new QName("http://schemas.xmlsoap.org/soap/encoding/", "offset");

    private static final Set SKIPPED_SOAP_ATTRS = new HashSet(Arrays.asList(new QName[]{HREF, ID, ENC_OFFSET}));

    private void processAttributes(SchemaType stype, XmlCursor xmlc) {
        if (_soapEnc) {
            QName typeName = stype.getName();
            if (typeName != null) {
                xmlc.insertAttributeWithValue(XSI_TYPE, formatQName(xmlc, typeName));
            }
        }

        SchemaProperty[] attrProps = stype.getAttributeProperties();
        for (int i = 0; i < attrProps.length; i++) {
            SchemaProperty attr = attrProps[i];
            MappingNode node = new MappingNode(xmlc, attr);
            String parentPath = path(xmlc);
            MappingNode parent = mappings.get(parentPath);
            parent.children.add(node);
            node.parent = parent;
            mappings.put(node.getPath(), node);

            if (_soapEnc) {
                if (SKIPPED_SOAP_ATTRS.contains(attr.getName()))
                    continue;
                if (ENC_ARRAYTYPE.equals(attr.getName())) {
                    SOAPArrayType arrayType = ((SchemaWSDLArrayType) stype.getAttributeModel().getAttribute(attr.getName())).getWSDLArrayType();
                    if (arrayType != null)
                        xmlc.insertAttributeWithValue(attr.getName(), formatQName(xmlc, arrayType.getQName()) + arrayType.soap11DimensionString());
                    continue;
                }
            }
            String defaultValue = attr.getDefaultText();
            xmlc.insertAttributeWithValue(attr.getName(), defaultValue == null ?
                    sampleDataForSimpleType(attr.getType()) : defaultValue);
        }
    }

    private void processSequence(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();
        for (int i = 0; i < spc.length; i++) {
            /// <parent>maybestuff^</parent>
            processParticle(spc[i], xmlc, mixed);
            //<parent>maybestuff...morestuff^</parent>
            if (mixed && i < spc.length - 1)
                xmlc.insertChars(pick(WORDS));
        }
    }

    private void processChoice(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();
        xmlc.insertComment("You have a CHOICE of the next " + String.valueOf(spc.length) + " items at this level");
        for (int i = 0; i < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed);
        }
    }

    private void processAll(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();
        // xmlc.insertComment("You may enter the following " + String.valueOf(spc.length) + " items in any order");
        for (int i = 0; i < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed);
            if (mixed && i < spc.length - 1)
                xmlc.insertChars(pick(WORDS));
        }
    }

    private void processWildCard(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        xmlc.insertComment("You may enter ANY elements at this point");
        xmlc.insertElement("AnyElement");
    }

    /**
     * This method will get the base type for the schema type
     */

    private static QName getClosestName(SchemaType sType) {
        while (sType.getName() == null)
            sType = sType.getBaseType();

        return sType.getName();
    }

    private String printParticleType(int particleType) {
        StringBuilder returnParticleType = new StringBuilder();
        returnParticleType.append("Schema Particle Type: ");

        switch (particleType) {
            case SchemaParticle.ALL:
                returnParticleType.append("ALL\n");
                break;
            case SchemaParticle.CHOICE:
                returnParticleType.append("CHOICE\n");
                break;
            case SchemaParticle.ELEMENT:
                returnParticleType.append("ELEMENT\n");
                break;
            case SchemaParticle.SEQUENCE:
                returnParticleType.append("SEQUENCE\n");
                break;
            case SchemaParticle.WILDCARD:
                returnParticleType.append("WILDCARD\n");
                break;
            default:
                returnParticleType.append("Schema Particle Type Unknown");
                break;
        }

        return returnParticleType.toString();
    }

    private ArrayList _typeStack = new ArrayList();

    public static class MappingNode extends AnnotatableSupport {

        private transient SchemaType schemaType;

        private transient SchemaProperty attribute;
        private transient MappingNode parent;
        private transient List<MappingNode> children = new ArrayList<>();

        private String path;
        private String name;
        private String dataType;
        private Map<String, String> restriction;
        private String cardinality;

        private String namespaceURI;
        private int level;
        private NodeType nodeType = NodeType.Folder;
        private String alias;
        private String defaultValue;

        public MappingNode(XmlCursor xmlc, SchemaType schemaType) {
            this.schemaType = schemaType;
            this.path = path(xmlc);
            this.name = xmlc.getDomNode().getLocalName();

            this.namespaceURI = xmlc.getDomNode().getNamespaceURI();
            if (path == null) {
                level = 0;
            } else {
                level = path.split("/").length;
            }

        }

        public MappingNode(XmlCursor xmlc, SchemaProperty attribute) {
            this.attribute = attribute;

            this.path = path(xmlc) + "/@" + attribute.getName().getLocalPart();
            this.name = attribute.getName().getLocalPart();
            this.namespaceURI = attribute.getName().getNamespaceURI();

            this.level = path.split("/").length;

            this.nodeType = NodeType.Attribute;
            this.defaultValue = attribute.getName().getLocalPart();
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public String getDataType() {
            return dataType;
        }

        public Map<String, String> getRestriction() {
            return restriction;
        }

        public String getCardinality() {
            return cardinality;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public String getNamespaceURI() {
            return namespaceURI;
        }

        public String getAlias() {
            return alias;
        }

        public String getDefaultValue() {
            return defaultValue;
        }


        public void setNamespaceURI(String namespaceURI) {
            this.namespaceURI = namespaceURI;
        }

        public void setCardinality(String cardinality) {
            this.cardinality = cardinality;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public MappingNode getParent() {
            return parent;
        }

        public List<MappingNode> getChildren() {
            return children;
        }

        public MappingNode getChild(String name) {
            for (MappingNode e : children) {
                if (name.equals(e.name)) {
                    return e;
                }
            }

            return null;
        }
    }

    public static enum NodeType {
        Folder, Field, Attribute;
    }

    public static class Builder {

        private SchemaType sType;
        private XmlObject object;
        private XmlOptions options = new XmlOptions();
        private XmlSchemaBase generator;
        private Buffalo.Renderer renderer;

        private Builder() {
        }

        public Builder fromYaml(String yml) {
            Yaml yaml = new Yaml();
            Map<String, Object> configuration = yaml.load(yml);

            configuration.entrySet().forEach(e -> {
                String key = e.getKey();
                Object value = e.getValue();

                Object component = createObject(key, value);
                if (component instanceof Buffalo.Annotator) {
                    annotate((Buffalo.Annotator) component);
                }

            });

            return this;
        }

        private Object createObject(String className, Object settings) {
            Object o = null;
            try {
                Class cls = Class.forName(className);
                Gson gson = new Gson();
                if (settings != null && settings instanceof Map) {
                    o = gson.fromJson(gson.toJson(settings), cls);
                } else {
                    o = cls.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return o;
        }

        public XmlSchemaBase create() {
            return generator;
        }

        public Builder schemaTypeSystem(SchemaTypeSystem schemaTypeSystem) {
            this.generator = new XmlSchemaBase(schemaTypeSystem);
            SchemaType sType = schemaTypeSystem.documentTypes()[0];

            this.object = XmlObject.Factory.newInstance();
            XmlCursor cursor = object.newCursor();

            // Skip the document node
            cursor.toNextToken();
            // Using the type and the cursor, call the utility method to get a
            // sample XML payload for that Schema element
            generator.introspect(sType, cursor);
            // Cursor now contains the sample payload
            // Pretty print the result.  Note that the cursor is positioned at the
            // end of the doc so we use the original xml object that the cursor was
            // created upon to do the xmlText() against.
            this.options = new XmlOptions();
            options.put(XmlOptions.SAVE_PRETTY_PRINT);
            options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 2);
            options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);

            return this;
        }

        public Builder annotate(Buffalo.Annotator annotator) {
            annotator.annotate(generator);
            return this;
        }

        public String render(Buffalo.Renderer renderer) {
            return renderer.render(generator);
        }

        public String sampleXmlText() {
            return object.xmlText();
        }
    }

}
