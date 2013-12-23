/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class KeywordMessageElement implements MessageElement {

    private final List<BinaryObjectDescription> binaryObjectDescriptions;
    private final StringBuffer keywords;
    private final int line;

    public KeywordMessageElement(String keyword, int line, BinaryObjectDescription binaryObjectDescription) {
        binaryObjectDescriptions = new ArrayList<BinaryObjectDescription>();
        keywords = new StringBuffer();
        this.line = line;
        add(keyword, line, binaryObjectDescription);
    }

    public void add(String keyword, int line, BinaryObjectDescription binaryObjectDescription) {
        keywords.append(keyword);
        binaryObjectDescriptions.add(binaryObjectDescription);
    }

    public String keywords() {
        return keywords.toString();
    }

    public List<BinaryObjectDescription> binaryObjectDescriptions() {
        return binaryObjectDescriptions;
    }

    public void accept(NodeVisitor nodeVisitor) {
        String keywords = keywords();
        nodeVisitor.visitBegin(this, keywords, binaryObjectDescriptions.size(), line);
        for (BinaryObjectDescription binaryObjectDescription : binaryObjectDescriptions)
            binaryObjectDescription.accept(nodeVisitor);
        nodeVisitor.visitEnd(this, keywords, binaryObjectDescriptions.size(), line);
    }

    public boolean hasBlockWithAnswerExpression() {
        for (BinaryObjectDescription binaryObjectDescription : binaryObjectDescriptions)
            if (binaryObjectDescription.hasBlockWithAnswerExpression())
                return true;
        return false;
    }
}
