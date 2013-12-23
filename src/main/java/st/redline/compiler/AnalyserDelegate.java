/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline.compiler;

import st.redline.compiler.ast.NodeVisitor;

public interface AnalyserDelegate extends NodeVisitor {
	byte[] classBytes();
}
