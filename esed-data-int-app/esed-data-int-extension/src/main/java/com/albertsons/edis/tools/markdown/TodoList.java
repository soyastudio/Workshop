package com.albertsons.edis.tools.markdown;

import com.google.common.collect.ImmutableList;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class TodoList {
    private static final String TOKEN = "TODO:";

    public static List<Todo> scan(String markdown) {
        Parser parser = Parser.builder().build();
        Node node = parser.parse(markdown);
        TodoListVisitor visitor = new TodoListVisitor();
        node.accept(visitor);
        return ImmutableList.copyOf(visitor.todoList);
    }

    static class Todo {
        private String name;
        private String description;

        public Todo(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    static class TodoListVisitor extends AbstractVisitor {
        private List<Todo> todoList = new ArrayList<>();

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            String info = fencedCodeBlock.getInfo();
            if (info.startsWith(TOKEN)) {
                String name = info.substring(TOKEN.length()).trim();
                String desc = fencedCodeBlock.getLiteral().trim();
                if (desc.startsWith("[") && desc.endsWith("]")) {
                    desc = desc.substring(1, desc.length() - 1);
                }
                todoList.add(new Todo(name, desc));
            }
        }
    }
}
