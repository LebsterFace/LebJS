package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.Reference;

public interface LeftHandSideExpression extends Expression {
	Reference toReference(Interpreter interpreter) throws AbruptCompletion;
}
