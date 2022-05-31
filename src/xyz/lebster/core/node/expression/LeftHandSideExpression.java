package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.Assignable;

public interface LeftHandSideExpression extends Expression, Assignable {
	Reference toReference(Interpreter interpreter) throws AbruptCompletion;
}