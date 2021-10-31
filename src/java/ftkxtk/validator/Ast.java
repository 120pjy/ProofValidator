package plc.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        public static final class Function extends Ast.Expression {

            private final String name;
            private final List<Ast.Expression> arguments;

            public Function(String name, List<Ast.Expression> arguments) {
                this.name = name;
                this.arguments = arguments;
            }

            public String getName() {
                return name;
            }

            public List<Ast.Expression> getArguments() {
                return arguments;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Ast.Expression.Function &&
                        name.equals(((Ast.Expression.Function) obj).name) &&
                        arguments.equals(((Ast.Expression.Function) obj).arguments);
            }

            @Override
            public String toString() {
                return "Ast.Expression.Function{" +
                        ", name='" + name + '\'' +
                        ", arguments=" + arguments +
                        '}';
            }

        }

        public static final class PlcList extends Ast.Expression {

            private final List<Ast.Expression> values;

            public PlcList(List<Ast.Expression> values) {
                this.values = values;
            }

            public List<Ast.Expression> getValues() {
                return values;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Ast.Expression.PlcList &&
                        values.equals(((Ast.Expression.PlcList) obj).values);
            }

            @Override
            public String toString() {
                return "Ast.Expression.PlcList{" +
                        ", values=[" + values + "]" +
                        '}';
            }

        }

    }

}
