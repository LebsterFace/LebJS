package xyz.lebster.node.expression;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;

public interface LeftHandSideExpression extends Expression {
	Reference toReference(Interpreter interpreter) throws AbruptCompletion;
}
