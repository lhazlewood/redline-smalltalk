/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline.compiler.ast;

public class AnswerStatement extends Statements {

    private final int line;

    public AnswerStatement(int line, Expression expression) {
        super(expression, null);
        this.line = line;
        expression.leaveResultOnStack();
    }

    public int line() {
        return line;
    }

    public boolean hasAnswerExpression() {
        return isAnswerExpression();
    }

    public boolean isAnswerExpression() {
        return true;
    }

    public void accept(NodeVisitor nodeVisitor) {
        nodeVisitor.visitBegin(this);
        if (expression() != null)
            expression().accept(nodeVisitor);
        nodeVisitor.visitEnd(this);
    }
}
