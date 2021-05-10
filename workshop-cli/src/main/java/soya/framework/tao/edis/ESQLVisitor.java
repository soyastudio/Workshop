package soya.framework.tao.edis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class ESQLVisitor {

    protected int cursor = -1;

    public void visit(File esql) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(esql));
        String line = reader.readLine();

        cursor = 0;
        while (line != null) {
            cursor++;
            String token = line.trim();

            if (token.startsWith("-- ")) {
                visitComment(line);

            } else if (token.startsWith("SET ")) {
                visitAssignment(line);

            } else if (token.startsWith("DECLARE ")) {
                visitDeclaration(line);

            } else if (token.startsWith("CREATE ")) {
                visitDeclaration(line);
            }

            line = reader.readLine();
        }

        cursor = -1;
    }

    protected abstract void visitComment(String line);

    protected abstract void visitDeclaration(String line);

    protected abstract void visitAssignment(String line);

    public static class Setter {
        protected int lineNum;
        protected String outputVariable;
        protected String type;
        protected String prefix;
        protected String name;

        protected String inputVariable;
        protected String value;

        public Setter(int lineNum, String line) {
            this.lineNum = lineNum;

            String token = line.trim().substring("SET ".length());
            if (token.endsWith(";")) {
                token = token.substring(0, token.length() - 1);
            }

            if (token.contains("=")) {
                int index = token.indexOf("=");
                String left = token.substring(0, index).trim();
                String right = token.substring(index + 1).trim();

                if (left.contains(".")) {
                    outputVariable = left.substring(0, left.indexOf("."));
                    name = left.substring(left.indexOf(".") + 1);

                    if (name.contains("(") && name.contains(")")) {
                        type = name.substring(name.indexOf("(") + 1, name.lastIndexOf(")"));
                        name = name.substring(name.lastIndexOf(")") + 1);
                        if (name.contains(":")) {
                            prefix = name.substring(0, name.indexOf(":"));
                            name = name.substring(name.indexOf(":") + 1);
                        }
                    }

                }

                value = right;

            }
        }
    }

}
