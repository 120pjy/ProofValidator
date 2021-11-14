package ftkxtk.validator;

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
            return "Source{" +
                    "statements="+statements+
                    '}';
        }

    }


    public static abstract class Statement extends Ast {

        public static final class Expression extends Statement {
            private final int line;
            private final Ast.Reason reason;
            private final Ast.Expression expression;

            public Expression(int line, Ast.Reason reason, Ast.Expression expression) {
                this.line = line;
                this.reason = reason;
                this.expression = expression;
            }

            public int getLine() {return line;}
            public Ast.Reason getReason() {return reason;}

            public Ast.Expression getExpression() {
                return expression;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Ast.Statement.Expression &&
                        line == ((Ast.Statement.Expression) obj).line &&
                        reason.equals(((Ast.Statement.Expression) obj).reason) &&
                        expression.equals(((Ast.Statement.Expression) obj).expression);
            }

            @Override
            public String toString() {
                return "Statement.Expression{" +
                        "line=" + line +
                        "reason=" + reason +
                        "expression=" + expression +
                        '}';
            }

        }

        public static final class Lemma extends Statement {
            private final String name;
            private final Ast.Expression expression;

            private List<Ast.Expression> structure;
            private List<Ast.Expression> otherWayStructure;


            public Lemma(String name, Ast.Expression expression) {
                super();
                this.name = name;
                this.expression = expression;
                otherWayStructure = null;
            }

            public void setStructure(List<Ast.Expression> structure) {
                this.structure = structure;
            }
            public void setOtherWayStructure(List<Ast.Expression> structure) {otherWayStructure = structure;}

            public String getName() {
                return name;
            }

            public Ast.Expression getExpression() {
                return expression;
            }

            public List<Ast.Expression> getStructure() {
                return structure;
            }
            public List<Ast.Expression> getOtherWayStructure() {return otherWayStructure;}

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Ast.Statement.Lemma &&
                        name.equals(((Ast.Statement.Lemma) obj).name) &&
                        expression.equals(((Ast.Statement.Lemma) obj).expression);
            }

            @Override
            public String toString() {
                return "Lemma{" +
                        "name=" + name +
                        "expression=" + expression +
                        '}';
            }

        }

        public static final class Transformation extends Statement {
            private final String name;


            private final Ast.Expression expression;
            private final Ast.Expression inference;
            private List<Ast.Expression> exprStructure;
            private List<Ast.Expression> infrStructure;

            public Transformation(String name, Ast.Expression expression, Ast.Expression inference) {
                this.name = name;
                this.expression = expression;
                this.inference = inference;
            }

            public List<Ast.Expression> getExprStructure() {
                return exprStructure;
            }

            public void setExprStructure(List<Ast.Expression> exprStructure) {
                this.exprStructure = exprStructure;
            }

            public List<Ast.Expression> getInfrStructure() {
                return infrStructure;
            }

            public void setInfrStructure(List<Ast.Expression> infrStructure) {
                this.infrStructure = infrStructure;
            }

            public Ast.Expression getInference() {
                return inference;
            }

            public String getName() {
                return name;
            }

            public Ast.Expression getExpression() {
                return expression;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Transformation that = (Transformation) o;
                return Objects.equals(name, that.name) && Objects.equals(expression, that.expression) && Objects.equals(inference, that.inference);
            }

            @Override
            public String toString() {
                return "Transformation{" +
                        "name='" + name + '\'' +
                        ", expression=" + expression +
                        ", inference=" + inference +
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
                return "Literal{" +
                        "literal=" + literal +
                        '}';
            }

        }

        public static final class Variable extends Ast.Expression {

            private final String name;
            private boolean value;

            public Variable(String name, boolean value) {
                this.name = name; this.value = value;
            }

            public Variable(String name) {
                this.name = name; this.value = false;
            }

            public String getName() {
                return name;
            }

            public boolean getValue() {
                return value;
            }

            public void setValue(boolean value) { this.value = value;}

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Variable &&
                        Objects.equals(name, ((Variable) obj).name) && Objects.equals(value, ((Variable) obj).value);
            }

            @Override
            public String toString() {
                return "Variable{" +
                        "name=" + name +
                        " value=" + value +
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
                return "Group{" +
                        "expression=" + expression +
                        '}';
            }

        }

        public static final class Binary extends Ast.Expression {

            private final String operator;
            private final Ast.Expression left;
            private final Ast.Expression right;

            public Binary(String operator, Ast.Expression left, Ast.Expression right) {
                this.operator = operator;
                this.left = left;
                this.right = right;
            }

            public String getOperator() {
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
                return "Binary{" +
                        "operator='" + operator + '\'' +
                        ", left=" + left +
                        ", right=" + right +
                        '}';
            }
        }

        public static final class Not extends Ast.Expression {

            Ast.Expression expr;

            public Not(Expression expr) {
                this.expr = expr;
            }


            public Ast.Expression getExpression() {
                return expr;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Not &&
                        expr.equals(((Not)obj).expr);
            }

            @Override
            public String toString() {
                return "Not{" +
                        "expression="+expr+
                        '}';
            }
        }
    }

    public static final class Reason extends Ast {
        private String reason;

        private List<Integer> lines;

        public Reason(String reason, List<Integer> lines) {
            this.reason = reason;
            this.lines = lines;
        }

        public String getReason() {return reason;}
        public List<Integer> getLines() {return lines;}

        @Override
        public String toString() {
            return "Reason{" +
                    "reason='" + reason + '\'' +
                    ", lines=" + lines +
                    '}';
        }
    }

    public interface Visitor<T> {

        default T visit(Ast ast) {
            if (ast instanceof Source) {
                return visit((Source) ast);
            } else if (ast instanceof Statement.Transformation) {
                return visit((Statement.Transformation) ast);
            } else if (ast instanceof Statement.Lemma) {
                return visit((Statement.Lemma) ast);
            } else if (ast instanceof Statement.Expression) {
                return visit((Statement.Expression) ast);
            } else if (ast instanceof Expression.Literal) {
                return visit((Expression.Literal) ast);
            } else if (ast instanceof Expression.Binary) {
                return visit((Expression.Binary) ast);
            } else if (ast instanceof Expression.Not) {
                return visit((Expression.Not) ast);
            } else if (ast instanceof Expression.Variable) {
                return visit((Expression.Variable) ast);
            } else if (ast instanceof Reason) {
                return visit((Reason) ast);
            }  else {
                throw new AssertionError("Unimplemented AST type: " + ast.getClass().getName() + ".");
            }
        }

        T visit(Source ast);

        T visit(Statement.Transformation ast);

        T visit(Statement.Lemma ast);

        T visit(Statement.Expression ast);

        T visit(Expression.Literal ast);

        T visit(Expression.Binary ast);

        T visit(Expression.Not ast);

        T visit(Expression.Variable ast);

        T visit(Reason ast);
    }
}

