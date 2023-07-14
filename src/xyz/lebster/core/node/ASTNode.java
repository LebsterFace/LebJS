package xyz.lebster.core.node;

import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;

public interface ASTNode extends Representable {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	default SourceRange range() {
		throw new ShouldNotHappen("range() not implemented for " + getClass().getSimpleName());
	}
}