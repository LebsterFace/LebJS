package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.expression.Expression;

public interface Declarable extends Dumpable {
	void declare(Interpreter interpreter, VariableDeclaration.Kind kind, Expression rhs) throws AbruptCompletion;
}
