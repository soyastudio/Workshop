package soya.framework.tools.iib;

import soya.framework.tools.iib.functions.AbstractFunctionParser;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

class Function {
    private static Map<String, FunctionParser> parsers;

    private final String expression;
    private final String name;
    private final String param;

    private FunctionParser parser;

    static {
        try {
            Map<String, FunctionParser> map = new HashMap<>();
            ClassPath classPath = ClassPath.from(Function.class.getClassLoader());
            classPath.getTopLevelClassesRecursive(AbstractFunctionParser.class.getPackage().getName()).forEach(e -> {
                Class c = e.load();
                if (AbstractFunctionParser.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                    try {
                        AbstractFunctionParser par = (AbstractFunctionParser) c.newInstance();
                        map.put(par.getName(), par);
                    } catch (InstantiationException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            parsers = ImmutableMap.copyOf(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Function(String expression) {
        this.expression = expression;

        int start = expression.indexOf("(");
        int end = expression.lastIndexOf(")");

        if(start > 0) {
            this.name = expression.substring(0, start);
            if (end > 0) {
                this.param = expression.substring(start + 1, end);
            } else {
                this.param = expression.substring(start + 1);
            }

        } else {
            this.name = expression;
            this.param = null;
        }

        this.parser = parsers.get(name);
    }

    public String getName() {
        return name;
    }

    public String getParam() {
        return param;
    }

    public String toESQL(CmmESQLGenerator context) {
        return parser != null ? parser.parse(param, context) : param;
    }

    @Override
    public String toString() {
        return expression;
    }
}
