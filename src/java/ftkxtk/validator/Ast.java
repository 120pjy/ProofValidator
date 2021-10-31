package ftkxtk.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * See the Parser assignment specification for specific notes on each AST class
 * and how to use it.
 */
public abstract class Ast {

    public static final class Source extends Ast {

        private final List<Statement> statements;

        public Source(List<Statement> statements) {
            this.statements = statements;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Source &&
                    statements.equals(((Source)obj).statements);
        }

        @Override
        public String toString() {
            return "Ast.Source{" +
                    "statements="+statements+
                    '}';
        }

    }


    public static abstract class Statement extends Ast {
        private final int line;
        private final Ast.Reason reason;

        protected Statement(int line, Ast.Reason reason) {
            this.line = line;
            this.reason = reason;
        }
        public int getLine() {return line;}
        public Ast.Reason getReason() {return reason;}

        public static final class Expression extends Statement {
            private final Ast.Expression expression;

            public Expression(int line, Ast.Reason reason, Ast.Expression expression) {
                super(line, reason);
                this.expression = expression;
            }

            public Ast.Expression getExpression() {
                return expression;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Ast.Statement.Expression &&
                        expression.equals(((Ast.Statement.Expression) obj).expression);
            }

            @Override
            public String toString() {
                return "Ast.Statement.Expression{" +
                        "expression=" + expression +
                        '}';
            }

        }

        public static final class Declaration extends Statement {
            private String name;
            private String value;

            public Declaration(int line, Ast.Reason reason, String name, String value) {
                super(line, reason);
                this.name = name;
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Declaration &&
                        name.equals(((Declaration) obj).name) &&
                        value.equals(((Declaration) obj).value);
            }

            @Override
            public String toString() {
                return "Ast.Statement.Declaration{" +
                        "name='" + name + '\'' +
                        ", value=" + value +
                        '}';
            }

        }



    }

    public static abstract class Expression extends Ast {

        public static final class Literal extends Ast.Expression {

            private final Object literal;

            public Literal(Object literal) {
                this.literal = literal;
            }

            public Object getLiteral() {
                return literal;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Literal &&
                        Objects.equals(literal, ((Literal) obj).literal);
            }

            @Override
            public String toString() {
                return "Ast.Expression.Literal{" +
                        "literal=" + literal +
                        '}';
            }

        }

        public static final class Group extends Ast.Expression {

            private final Ast.Expression expression;

            public Group(Ast.Expression expression) {
                this.expression = expression;
            }

            public Ast.Expression getExpression() {
                return expression;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Group &&
                        expression.equals(((Group) obj).expression);
            }

            @Override
            public String toString() {
                return "Ast.Expression.Group{" +
                        "expression=" + expression +
                        '}';
            }

        }

        public static final class Binary extends Ast.Expression {

            public enum operator {
                implies,
                iff,
                equals
            }

            private final operator operator;
            private final Ast.Expression left;
            private final Ast.Expression right;

            public Binary(operator operator, Ast.Expression left, Ast.Expression right) {
                this.operator = operator;
                this.left = left;
                this.right = right;
            }

            public operator getOperator() {
                return operator;
            }

            public Ast.Expression getLeft() {
                return left;
            }

            public Ast.Expression getRight() {
                return right;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Binary &&
                        operator.equals(((Binary) obj).operator) &&
                        left.equals(((Binary) obj).left) &&
                        right.equals(((Binary) obj).right);
            }

            @Override
            public String toString() {
                return "Ast.Expression.Binary{" +
                        "operator='" + operator + '\'' +
                        ", left=" + left +
                        ", right=" + right +
                        '}';
            }

        }

    }

    public class Reason extends Ast {
        private String reason;
        private List<Integer> lines;

        public Reason(String reason, List<Integer> lines) {
            this.reason = reason;
            this.lines = lines;
        }

        public String getReason() {return reason;}
        public List<Integer> getLines() {return lines;}
    }
}
