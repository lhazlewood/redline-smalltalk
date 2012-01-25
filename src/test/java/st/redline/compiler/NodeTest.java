/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline.compiler;

import org.hamcrest.SelfDescribing;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class NodeTest {

	NodeVisitor visitor;

	@Before
	public void setup() {
		visitor = mock(NodeVisitor.class);
	}

	@Test
	public void answerStatementNodeConstructionShouldTellExpressionToLeaveResultOnStack() {
		SimpleExpression simpleExpression = mock(SimpleExpression.class);
		new AnswerStatement(42, simpleExpression);
		verify(simpleExpression).leaveResultOnStack();
	}

	@Test
	public void answerStatementNodeShouldBeVisitable() {
		SimpleExpression simpleExpression = mock(SimpleExpression.class);
		AnswerStatement answerStatement = new AnswerStatement(42, simpleExpression);
		answerStatement.accept(visitor);
		verify(visitor).visitBegin(answerStatement);
		verify(simpleExpression).accept(visitor);
		verify(visitor).visitEnd(answerStatement);
	}

	@Test
	public void assignmentExpressionNodeShouldBeVisitable() {
		Identifier identifier = mock(Identifier.class);
		SimpleExpression simpleExpression = mock(SimpleExpression.class);
		AssignmentExpression assignmentExpression = new AssignmentExpression(identifier, simpleExpression);
		assignmentExpression.accept(visitor);
		verify(visitor).visitBegin(assignmentExpression);
		verify(identifier).accept(visitor);
		verify(simpleExpression).accept(visitor);
		verify(visitor).visitEnd(assignmentExpression);
	}

	@Test
	public void unaryExpressionNodeShouldBeVisitable() {
		UnarySelector unarySelector = mock(UnarySelector.class);
		MessageExpression messageExpression = mock(MessageExpression.class);
		UnaryExpression unaryExpression = new UnaryExpression();
		unaryExpression.add(unarySelector);
		unaryExpression.add(messageExpression);
		unaryExpression.accept(visitor);
		verify(visitor).visitBegin(unaryExpression);
		verify(unarySelector).accept(visitor);
		verify(messageExpression).accept(visitor);
		verify(visitor).visitEnd(unaryExpression);
	}

	@Test
	public void unarySelectorNodeShouldBeVisitable() {
		UnarySelector unarySelector = new UnarySelector("yourself", 1);
		unarySelector.accept(visitor);
		verify(visitor).visit(unarySelector, "yourself", 1);
	}

	@Test
	public void shouldVisitSelfSyntheticNodeWhenIdentifierValueIsSelf() {
		Identifier identifier = new Identifier("self", 1);
		identifier.accept(visitor);
		verify(visitor).visit((Self) argThat(new IsClass(Self.class)), anyInt());
	}

	@Test
	public void shouldVisitSuperSyntheticNodeWhenIdentifierValueIsSuper() {
		Identifier identifier = new Identifier("super", 1);
		identifier.accept(visitor);
		verify(visitor).visit((Super) argThat(new IsClass(Super.class)), anyInt());
	}

	@Test
	public void shouldVisitTrueSyntheticNodeWhenIdentifierValueIsTrue() {
		Identifier identifier = new Identifier("true", 1);
		identifier.accept(visitor);
		verify(visitor).visit((True) argThat(new IsClass(True.class)), anyInt());
	}

	@Test
	public void shouldVisitFalseSyntheticNodeWhenIdentifierValueIsFalse() {
		Identifier identifier = new Identifier("false", 1);
		identifier.accept(visitor);
		verify(visitor).visit((False) argThat(new IsClass(False.class)), anyInt());
	}

	@Test
	public void shouldVisitNilSyntheticNodeWhenIdentifierValueIsNil() {
		Identifier identifier = new Identifier("nil", 1);
		identifier.accept(visitor);
		verify(visitor).visit((Nil) argThat(new IsClass(Nil.class)), anyInt());
	}

	@Test
	public void shouldVisitJVMSyntheticNodeWhenIdentifierValueIsJVM() {
		Identifier identifier = new Identifier("JVM", 1);
		identifier.accept(visitor);
		verify(visitor).visit((JVM) argThat(new IsClass(JVM.class)), anyInt());
	}

	@Test
	public void simpleExpressionNodeWithCascadeShouldBeVisitable() {
		Cascade cascade = mock(Cascade.class);
		Primary primary = mock(Primary.class);
		MessageExpression messageExpression = mock(MessageExpression.class);
		MessageElement messageElement = mock(MessageElement.class);
		SimpleExpression.cascade = cascade;
		SimpleExpression simpleExpression = new SimpleExpression();
		simpleExpression.add(primary);
		simpleExpression.add(messageExpression);
		simpleExpression.add(messageElement);
		simpleExpression.add(messageElement);
		simpleExpression.accept(visitor);
		verify(visitor).visitBegin(simpleExpression);
		verify(primary).accept(visitor);
		verify(messageExpression).accept(visitor);
		verify(cascade, times(2)).begin(visitor);
		verify(messageElement, times(2)).accept(visitor);
		verify(cascade).end(visitor);
		verify(visitor).visitEnd(simpleExpression);
	}

	@Test
	public void statementsNodeShouldBeVisitable() {
		Expression expression = mock(Expression.class);
		Statements nestedStatements = mock(Statements.class);
		Statements statements = new Statements(expression, nestedStatements);
		statements.accept(visitor);
		verify(visitor).visitBegin(statements);
		verify(expression).accept(visitor);
		verify(nestedStatements).accept(visitor);
		verify(visitor).visitEnd(statements);
	}

	@Test
	public void programNodeShouldBeVisitable() {
		Temporaries temporaries = mock(Temporaries.class);
		Statements statements = mock(Statements.class);
		Program program = new Program(temporaries, statements);
		program.accept(visitor);
		verify(visitor).visitBegin(program);
		verify(temporaries).accept(visitor);
		verify(statements).accept(visitor);
		verify(visitor).visitEnd(program);
	}

	@Test
	public void temporariesNodeShouldBeVisitable() {
		Temporary temporary = mock(Temporary.class);
		List<Temporary> list = new ArrayList<Temporary>();
		list.add(temporary);
		Temporaries temporaries = new Temporaries(list);
		assertEquals(1, temporaries.size());
		assertFalse(temporaries.isEmpty());
		assertEquals(temporary, temporaries.get(0));
		temporaries.accept(visitor);
		verify(visitor).visitBegin(temporaries);
		verify(temporary).accept(visitor);
		verify(visitor).visitEnd(temporaries);
	}

	@Test
	public void temporaryNodeShouldBeVisitable() {
		Temporary temporary = new Temporary("x", 42);
		temporary.accept(visitor);
		verify(visitor).visit(temporary, "x", 42);
	}

	class IsClass extends ArgumentMatcher {
		private Class aClass;
		public IsClass(Class aClass) {
			this.aClass = aClass;
		}
		public boolean matches(Object anObject) {
			return aClass.equals(anObject.getClass());
		}
	}
}
