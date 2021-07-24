package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;

public interface LeftHandSideExpression extends Expression {
	Reference toReference(Interpreter interpreter) throws AbruptCompletion;
}